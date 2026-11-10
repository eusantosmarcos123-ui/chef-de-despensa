# Chef de Despensa (Zero Desperdício) 🍲

Aplicativo Android Nativo em Java desenvolvido com a integração **Google Stitch + Google Antigravity**.

## 📱 Visão Geral do Projeto
O aplicativo tem como objetivo combater o desperdício alimentar doméstico, controlando prazos de validade e sugerindo receitas em tempo real com base nos ingredientes que estão prestes a vencer.

### ✨ Funcionalidades Principais:
1. **Despensa Inteligente (Dashboard):**
   - Lista dinâmica com badges visuais de validade:
     - 🟢 **Seguro** (> 3 dias)
     - 🟡 **Atenção** (≤ 3 dias)
     - 🔴 **Vencido / Crítico** (hoje ou data ultrapassada)
   - Contador de alerta de itens críticos no topo.
2. **Cadastro Rápido de Ingredientes:**
   - Nome, quantidade, unidade (kg, g, L, un, ml) e seletor interativo de data de validade (`DatePickerDialog`).
3. **Sugestão de Receitas (TheMealDB API):**
   - Consumo em tempo real da API pública TheMealDB via **Retrofit**.
   - Busca de receitas que utilizam ingredientes próximos do vencimento.
   - Carregamento assíncrono de imagens com **Glide**.
4. **Persistência Local Offline (Room SQLite):**
   - Banco de dados local com Room Database, DAO e `LiveData` para sincronização reativa com a UI.

---

## 🛠️ Tecnologias e Arquitetura

- **Linguagem:** Java (Android SDK 34 / minSdk 26)
- **Design & Telas:** Google Stitch (`@google/stitch-sdk`) + Material Design 3 (XML)
- **Banco de Dados Local:** Android Room (SQLite)
- **Rede & API:** Retrofit 2 + Gson Converter + Glide
- **Arquitetura:** MVVM (Model-View-ViewModel + Repository Pattern)

---

## 🚀 Como Abrir e Rodar no Android Studio

1. Abra o **Android Studio**.
2. Clique em **Open** e selecione a pasta:
   ```
   ChefDeDespensa
   ```
3. Aguarde o **Gradle Sync** baixar as dependências configuradas no `app/build.gradle`.
4. Conecte um celular via depuração USB ou inicie um emulador Android.
5. Clique no botão verde de **Run (Shift + F10)**.

---

## 🎨 Integração com o Google Stitch

A pasta `scripts/` contém a ponte automatizada entre o **Google Stitch** e o **Google Antigravity**:
- Para sincronizar ou atualizar as telas diretamente do Stitch:
  ```bash
  cd scripts
  npm run sync
  ```
- O script consulta a API oficial do Google Stitch, gera os protótipos de interface e salva os manifestos e códigos visuais em `scripts/stitch_output/`.
