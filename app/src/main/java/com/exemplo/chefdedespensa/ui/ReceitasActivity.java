package com.exemplo.chefdedespensa.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.exemplo.chefdedespensa.R;
import com.exemplo.chefdedespensa.data.model.RecipeSummary;
import com.exemplo.chefdedespensa.data.model.TheMealDbResponse;
import com.exemplo.chefdedespensa.data.network.RetrofitClient;
import com.exemplo.chefdedespensa.ui.adapter.RecipeAdapter;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReceitasActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView tvSemReceitas, tvFocoIngrediente;
    private RecipeAdapter adapter;

    // Dicionário para mapear ingredientes em português para termos válidos da TheMealDB API
    private static final Map<String, String> DICIONARIO_INGREDIENTES = new HashMap<>();

    static {
        DICIONARIO_INGREDIENTES.put("frango", "chicken");
        DICIONARIO_INGREDIENTES.put("peito de frango", "chicken_breast");
        DICIONARIO_INGREDIENTES.put("carne", "beef");
        DICIONARIO_INGREDIENTES.put("carne moida", "minced_beef");
        DICIONARIO_INGREDIENTES.put("tomate", "tomato");
        DICIONARIO_INGREDIENTES.put("ovo", "egg");
        DICIONARIO_INGREDIENTES.put("ovos", "egg");
        DICIONARIO_INGREDIENTES.put("arroz", "rice");
        DICIONARIO_INGREDIENTES.put("queijo", "cheese");
        DICIONARIO_INGREDIENTES.put("leite", "milk");
        DICIONARIO_INGREDIENTES.put("cebola", "onion");
        DICIONARIO_INGREDIENTES.put("batata", "potato");
        DICIONARIO_INGREDIENTES.put("alho", "garlic");
        DICIONARIO_INGREDIENTES.put("peixe", "fish");
        DICIONARIO_INGREDIENTES.put("salmao", "salmon");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receitas);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarReceitas);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // Componentes da Interface
        progressBar = findViewById(R.id.progressBar);
        tvSemReceitas = findViewById(R.id.tvSemReceitas);
        tvFocoIngrediente = findViewById(R.id.tvFocoIngrediente);
        RecyclerView rvReceitas = findViewById(R.id.rvReceitas);

        // Configuração do RecyclerView
        rvReceitas.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecipeAdapter(this);
        rvReceitas.setAdapter(adapter);

        // Obter ingrediente vindo da MainActivity
        String ingredientePt = getIntent().getStringExtra("NOME_INGREDIENTE");
        if (ingredientePt == null || ingredientePt.trim().isEmpty()) {
            ingredientePt = "Frango";
        }

        tvFocoIngrediente.setText("Receitas com: " + ingredientePt);

        // Disparar requisição Retrofit
        buscarReceitas(ingredientePt);
    }

    private void buscarReceitas(String ingredienteNome) {
        progressBar.setVisibility(View.VISIBLE);
        tvSemReceitas.setVisibility(View.GONE);

        String termoBusca = ingredienteNome.toLowerCase().trim();
        String termoApi = DICIONARIO_INGREDIENTES.getOrDefault(termoBusca, termoBusca.replace(" ", "_"));

        Call<TheMealDbResponse> call = RetrofitClient.getService().searchRecipesByIngredient(termoApi);
        call.enqueue(new Callback<TheMealDbResponse>() {
            @Override
            public void onResponse(@NonNull Call<TheMealDbResponse> call, @NonNull Response<TheMealDbResponse> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null && response.body().getMeals() != null && !response.body().getMeals().isEmpty()) {
                    adapter.setRecipes(response.body().getMeals());
                } else {
                    buscarFallbackFrango();
                }
            }

            @Override
            public void onFailure(@NonNull Call<TheMealDbResponse> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                tvSemReceitas.setVisibility(View.VISIBLE);
                tvSemReceitas.setText("Erro de conexão ao buscar receitas.\nVerifique sua internet.");
                Toast.makeText(ReceitasActivity.this, "Falha na requisição da API", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void buscarFallbackFrango() {
        Call<TheMealDbResponse> fallbackCall = RetrofitClient.getService().searchRecipesByIngredient("chicken");
        fallbackCall.enqueue(new Callback<TheMealDbResponse>() {
            @Override
            public void onResponse(@NonNull Call<TheMealDbResponse> call, @NonNull Response<TheMealDbResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getMeals() != null) {
                    adapter.setRecipes(response.body().getMeals());
                    tvFocoIngrediente.setText("Sugestões alternativas com: Frango (Chicken)");
                } else {
                    tvSemReceitas.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<TheMealDbResponse> call, @NonNull Throwable t) {
                tvSemReceitas.setVisibility(View.VISIBLE);
            }
        });
    }
}
