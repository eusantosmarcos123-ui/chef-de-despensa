package com.exemplo.chefdedespensa.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.exemplo.chefdedespensa.R;
import com.exemplo.chefdedespensa.data.model.Ingredient;
import com.exemplo.chefdedespensa.ui.adapter.IngredientAdapter;
import com.exemplo.chefdedespensa.ui.viewmodel.PantryViewModel;

public class MainActivity extends AppCompatActivity {

    private PantryViewModel viewModel;
    private IngredientAdapter adapter;
    private LinearLayout layoutVazio;
    private CardView cardBannerReceitas;
    private TextView tvTituloBanner, tvSubtituloBanner;
    private Ingredient mostUrgentItem = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar ViewModel
        viewModel = new ViewModelProvider(this).get(PantryViewModel.class);

        // Componentes da Interface
        RecyclerView rvIngredientes = findViewById(R.id.rvIngredientes);
        layoutVazio = findViewById(R.id.layoutVazio);
        cardBannerReceitas = findViewById(R.id.cardBannerReceitas);
        tvTituloBanner = findViewById(R.id.tvTituloBanner);
        tvSubtituloBanner = findViewById(R.id.tvSubtituloBanner);
        ExtendedFloatingActionButton fabAdicionar = findViewById(R.id.fabAdicionar);

        // Configuração do RecyclerView & Adapter
        rvIngredientes.setLayoutManager(new LinearLayoutManager(this));
        adapter = new IngredientAdapter(this, this::confirmarExclusao, this::abrirEdicaoIngrediente);
        rvIngredientes.setAdapter(adapter);

        // Observar Mudanças na Lista via LiveData (Reativo)
        viewModel.getAllIngredients().observe(this, list -> {
            adapter.setIngredients(list);

            if (list == null || list.isEmpty()) {
                layoutVazio.setVisibility(View.VISIBLE);
                rvIngredientes.setVisibility(View.GONE);
                cardBannerReceitas.setVisibility(View.GONE);
                mostUrgentItem = null;
            } else {
                layoutVazio.setVisibility(View.GONE);
                rvIngredientes.setVisibility(View.VISIBLE);
                cardBannerReceitas.setVisibility(View.VISIBLE);
                atualizarBannerUrgencia();
            }
        });

        // Botão FAB para Adicionar
        fabAdicionar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CadastroActivity.class);
            startActivity(intent);
        });

        // Botão de Receitas no Banner
        findViewById(R.id.btnSugerirReceitas).setOnClickListener(v -> abrirSugestaoReceitas());
    }

    private void atualizarBannerUrgencia() {
        viewModel.getMostUrgentIngredient(ingredient -> {
            runOnUiThread(() -> {
                mostUrgentItem = ingredient;
                if (ingredient != null) {
                    long dias = ingredient.getDaysUntilExpiration();
                    if (dias < 0) {
                        tvTituloBanner.setText("🚨 Item Vencido: " + ingredient.getName());
                        tvSubtituloBanner.setText("Atenção! Este produto passou da data de validade.");
                    } else if (dias <= 3) {
                        tvTituloBanner.setText("⏳ Vencendo em breve: " + ingredient.getName());
                        tvSubtituloBanner.setText("Restam " + (dias == 0 ? "horas" : dias + " dias") + ". Que tal cozinhar algo com ele hoje?");
                    } else {
                        tvTituloBanner.setText("🍳 Dica do Chef: " + ingredient.getName());
                        tvSubtituloBanner.setText("Veja ideias de pratos deliciosos usando o que você já tem em estoque.");
                    }
                }
            });
        });
    }

    private void abrirEdicaoIngrediente(Ingredient item) {
        Intent intent = new Intent(MainActivity.this, CadastroActivity.class);
        intent.putExtra("IS_EDIT", true);
        intent.putExtra("INGREDIENT_ID", item.getId());
        intent.putExtra("NOME", item.getName());
        intent.putExtra("QUANTIDADE", item.getQuantity());
        intent.putExtra("UNIDADE", item.getUnit());
        intent.putExtra("CATEGORIA", item.getCategory());
        if (item.getExpirationDate() != null) {
            intent.putExtra("VALIDADE_TIMESTAMP", item.getExpirationDate().getTime());
        }
        startActivity(intent);
    }

    private void abrirSugestaoReceitas() {
        Intent intent = new Intent(MainActivity.this, ReceitasActivity.class);
        if (mostUrgentItem != null) {
            intent.putExtra("NOME_INGREDIENTE", mostUrgentItem.getName());
        }
        startActivity(intent);
    }

    private void confirmarExclusao(Ingredient item) {
        new AlertDialog.Builder(this)
                .setTitle("Remover da Despensa")
                .setMessage("Deseja marcar '" + item.getName() + "' como consumido ou descartado?")
                .setPositiveButton("Sim, remover", (dialog, which) -> {
                    viewModel.delete(item);
                    Toast.makeText(this, "Item removido com sucesso!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
