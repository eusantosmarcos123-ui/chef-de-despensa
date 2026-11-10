import 'dotenv/config';
import fs from 'fs';
import path from 'path';
import { fileURLToPath } from 'url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

const apiKey = process.env.STITCH_API_KEY;

async function downloadAsset(url, destPath) {
  try {
    // Try with X-Goog-Api-Key and Authorization Bearer headers
    let res = await fetch(url, {
      headers: {
        'X-Goog-Api-Key': apiKey,
        'Authorization': `Bearer ${apiKey}`
      }
    });

    if (!res.ok) {
      // Try query param key
      const separator = url.includes('?') ? '&' : '?';
      res = await fetch(`${url}${separator}key=${apiKey}`);
    }

    if (!res.ok) {
      console.warn(`Aviso ao baixar ${url}: status ${res.status}`);
      return false;
    }
    const buffer = await res.arrayBuffer();
    fs.writeFileSync(destPath, Buffer.from(buffer));
    console.log(`Baixado com sucesso: ${path.basename(destPath)}`);
    return true;
  } catch (err) {
    console.warn(`Erro ao baixar ${url}:`, err.message);
    return false;
  }
}

async function main() {
  const outputDir = path.join(__dirname, "stitch_output");
  const assetsDir = path.join(__dirname, "..", "design", "screens");
  if (!fs.existsSync(assetsDir)) {
    fs.mkdirSync(assetsDir, { recursive: true });
  }

  const jsonFiles = fs.readdirSync(outputDir).filter(f => f.endsWith(".json") && f !== "sync_manifest.json");

  for (const f of jsonFiles) {
    const data = JSON.parse(fs.readFileSync(path.join(outputDir, f), "utf8"));
    const baseName = f.replace(".json", "");

    if (data.htmlUrl) {
      await downloadAsset(data.htmlUrl, path.join(assetsDir, `${baseName}.html`));
    }
    if (data.imgUrl) {
      await downloadAsset(data.imgUrl, path.join(assetsDir, `${baseName}.png`));
    }
  }

  console.log("Download de protótipos visuais concluído!");
}

main();
