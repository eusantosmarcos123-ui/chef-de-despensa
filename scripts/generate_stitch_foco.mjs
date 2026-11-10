import 'dotenv/config';
import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';
import { stitch } from '@google/stitch-sdk';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

async function run() {
  console.log("🎨 Conectando ao Google Stitch para o tema Foco nos Estudos (CheckList Gamer)...");
  const project = stitch.project("7620202662312616981");

  const prompt = "Modern single mobile screen for youth study app called 'Foco nos Estudos (CheckList Gamer)'. Top section: Gamer Quest header with XP counter, level badge and Concluded vs Pending tasks progress card. Middle section: Clean input form with two fields (Matéria and Tarefa/Trabalho) and a vibrant action button 'Adicionar Missão'. Bottom section: list of quest cards with subject tag, checkbox, task name and delete button. Dark mode gaming aesthetic with neon purple, cyan accents and rounded corners.";
  
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
      theme: "5. Foco nos Estudos (CheckList Gamer)",
      prompt,
      id: screen.id || "generated",
      htmlUrl,
      imgUrl,
      timestamp: new Date().toISOString()
    };

    const outDir = path.join(__dirname, "..", "..", "foco-estudos", "stitch_design");
    if (!fs.existsSync(outDir)) {
      fs.mkdirSync(outDir, { recursive: true });
    }

    fs.writeFileSync(path.join(outDir, "stitch_metadata.json"), JSON.stringify(screenData, null, 2), "utf8");
    console.log("✅ Protótipo do Stitch gerado com sucesso para UC-18!");
    console.log(`HTML: ${htmlUrl}`);
    console.log(`Imagem: ${imgUrl}`);
  } catch (e) {
    console.warn("Aviso na geração Stitch:", e.message);
  }
}

run();
