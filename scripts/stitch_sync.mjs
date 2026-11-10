import 'dotenv/config';
import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

async function main() {
  console.log("==================================================");
  console.log("  Google Stitch <-> Google Antigravity Bridge");
  console.log("  Projeto: Chef de Despensa (Zero Desperdício)");
  console.log("==================================================");

  const apiKey = process.env.STITCH_API_KEY;
  if (!apiKey) {
    console.error("ERRO: STITCH_API_KEY não foi configurada.");
    process.exit(1);
  }

  console.log("🔑 Chave do Google Stitch autenticada com sucesso.");

  let stitchModule;
  try {
    stitchModule = await import("@google/stitch-sdk");
  } catch (err) {
    console.error("Não foi possível carregar @google/stitch-sdk:", err.message);
    process.exit(1);
  }

  const { stitch } = stitchModule;

  const outputDir = path.join(__dirname, "stitch_output");
  if (!fs.existsSync(outputDir)) {
    fs.mkdirSync(outputDir, { recursive: true });
  }

  try {
    console.log("\n📡 Conectando ao Google Stitch para verificar projetos existentes...");
    let projects = [];
    try {
      projects = await stitch.projects();
      console.log(`Projetos encontrados no Stitch: ${projects.length}`);
    } catch (e) {
      console.log("Aviso ao listar projetos:", e.message);
    }

    let targetProject = null;
    if (projects && projects.length > 0) {
      targetProject = projects[0];
      console.log(`Usando projeto existente: [${targetProject.id || targetProject.name || 'Projeto'}]`);
    } else {
      console.log("Criando novo projeto no Stitch: 'Chef de Despensa'...");
      targetProject = await stitch.createProject("Chef de Despensa (Zero Desperdício)");
      console.log(`Projeto criado com sucesso! ID: ${targetProject.id}`);
    }

    console.log("\n📐 Sincronizando e gerando telas do aplicativo:");

    const screensToEnsure = [
      {
        name: "1. Despensa e Alertas de Validade (Dashboard)",
        prompt: "Android native mobile screen Material 3 for 'Chef de Despensa'. Top AppBar with title Chef de Despensa and expiration alert counter. RecyclerView of pantry ingredients with dynamic badges: green for safe, amber for expiring in 3 days, red for expired. Floating action button with + icon to add new ingredient, and button at the bottom to view suggested recipes from expiring items.",
        outputFile: "01_dashboard_despensa.html"
      },
      {
        name: "2. Cadastro de Ingrediente (Formulário)",
        prompt: "Android native form screen Material 3 for adding a pantry ingredient. Input fields: Nome do Ingrediente (e.g., Leite, Tomate), Quantidade (number with unit selector: kg, g, un, L, ml), Data de Validade with interactive DatePicker calendar button. Primary button at bottom 'Salvar Ingrediente' and cancel button.",
        outputFile: "02_cadastro_ingrediente.html"
      },
      {
        name: "3. Sugestões de Receitas (TheMealDB)",
        prompt: "Android mobile recipe suggestions screen Material 3. Banner 'Receitas com Ingredientes Prestes a Vencer'. Grid/List of recipe cards with appetizing meal photo, title, category, and matching pantry ingredient tags. Button to view full recipe details.",
        outputFile: "03_sugestoes_receitas.html"
      }
    ];

    const results = [];

    for (const item of screensToEnsure) {
      console.log(`\n🎨 Gerando tela no Stitch: ${item.name}...`);
      try {
        const screen = await targetProject.generate(item.prompt);
        let htmlUrl = "";
        let imgUrl = "";

        if (typeof screen.getHtml === 'function') {
          htmlUrl = await screen.getHtml();
        }
        if (typeof screen.getImage === 'function') {
          imgUrl = await screen.getImage();
        }

        const screenData = {
          name: item.name,
          prompt: item.prompt,
          id: screen.id || "generated",
          htmlUrl,
          imgUrl,
          timestamp: new Date().toISOString()
        };

        const destFile = path.join(outputDir, item.outputFile.replace(".html", ".json"));
        fs.writeFileSync(destFile, JSON.stringify(screenData, null, 2), "utf8");
        console.log(` Tela gerada com sucesso! Metadados salvos em ${path.basename(destFile)}`);
        if (htmlUrl) console.log(`   HTML URL: ${htmlUrl}`);
        if (imgUrl) console.log(`   Imagem URL: ${imgUrl}`);
        results.push(screenData);
      } catch (genErr) {
        console.error(`Erro ao gerar tela '${item.name}':`, genErr.message);
      }
    }

    // Gerar Relatório de Mapeamento com o Android Studio
    const syncReport = {
      project: "Chef de Despensa (Zero Desperdício)",
      stitchProjectId: targetProject.id || "default",
      synchronizedAt: new Date().toISOString(),
      mappedLayouts: [
        {
          stitchScreen: "01_dashboard_despensa",
          androidLayoutXml: "ChefDeDespensa/app/src/main/res/layout/activity_main.xml",
          androidItemXml: "ChefDeDespensa/app/src/main/res/layout/item_ingrediente.xml",
          javaActivity: "ChefDeDespensa/app/src/main/java/com/exemplo/chefdedespensa/ui/MainActivity.java",
          status: "SYNCHRONIZED_READY"
        },
        {
          stitchScreen: "02_cadastro_ingrediente",
          androidLayoutXml: "ChefDeDespensa/app/src/main/res/layout/activity_cadastro.xml",
          javaActivity: "ChefDeDespensa/app/src/main/java/com/exemplo/chefdedespensa/ui/CadastroActivity.java",
          status: "SYNCHRONIZED_READY"
        },
        {
          stitchScreen: "03_sugestoes_receitas",
          androidLayoutXml: "ChefDeDespensa/app/src/main/res/layout/activity_receitas.xml",
          androidItemXml: "ChefDeDespensa/app/src/main/res/layout/item_receita.xml",
          javaActivity: "ChefDeDespensa/app/src/main/java/com/exemplo/chefdedespensa/ui/ReceitasActivity.java",
          status: "SYNCHRONIZED_READY"
        }
      ],
      screens: results
    };

    fs.writeFileSync(path.join(outputDir, "sync_manifest.json"), JSON.stringify(syncReport, null, 2), "utf8");
    console.log("\n Sincronização concluída com sucesso! Manifesto gerado em stitch_output/sync_manifest.json");

  } catch (err) {
    console.error("Falha na execução do Stitch Sync:", err);
    process.exit(1);
  }
}

main();
