import 'dotenv/config';
import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';
import { stitch } from '@google/stitch-sdk';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

async function run() {
  console.log("Gerando tela 3 no Stitch...");
  const project = stitch.project("7620202662312616981");

  const prompt = "Android mobile recipe suggestion screen. Header 'Receitas com Ingredientes Prestes a Vencer'. Cards showing recipe photo, recipe name, category and ingredients list with button to cook.";
  
  try {
    const screen = await project.generate(prompt);
    let htmlUrl = "";
    let imgUrl = "";

    if (typeof screen.getHtml === 'function') {
      htmlUrl = await screen.getHtml();
    }
    if (typeof screen.getImage === 'function') {
      imgUrl = await screen.getImage();
    }

    const screenData = {
      name: "3. Sugestões de Receitas (TheMealDB)",
      prompt,
      id: screen.id || "generated",
      htmlUrl,
      imgUrl,
      timestamp: new Date().toISOString()
    };

    const destFile = path.join(__dirname, "stitch_output", "03_sugestoes_receitas.json");
    fs.writeFileSync(destFile, JSON.stringify(screenData, null, 2), "utf8");
    console.log(`Tela 3 gerada com sucesso! Metadados em 03_sugestoes_receitas.json`);
    console.log(`HTML: ${htmlUrl}`);
    console.log(`Imagem: ${imgUrl}`);

    // Atualizar sync_manifest.json
    const manifestPath = path.join(__dirname, "stitch_output", "sync_manifest.json");
    if (fs.existsSync(manifestPath)) {
      const manifest = JSON.parse(fs.readFileSync(manifestPath, "utf8"));
      manifest.screens = manifest.screens || [];
      manifest.screens.push(screenData);
      fs.writeFileSync(manifestPath, JSON.stringify(manifest, null, 2), "utf8");
      console.log("Manifesto atualizado com sucesso!");
    }
  } catch (e) {
    console.error("Erro na geração da tela 3:", e.message);
  }
}

run();
