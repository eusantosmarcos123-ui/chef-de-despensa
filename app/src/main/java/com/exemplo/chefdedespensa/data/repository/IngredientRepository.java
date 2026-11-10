package com.exemplo.chefdedespensa.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;

import com.exemplo.chefdedespensa.data.local.AppDatabase;
import com.exemplo.chefdedespensa.data.local.IngredientDao;
import com.exemplo.chefdedespensa.data.model.Ingredient;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Repositório que implementa o padrão Single Source of Truth para ingredientes.
 * Isola totalmente a camada de banco de dados do ViewModel e executa tarefas de I/O em pool de threads.
 */
public class IngredientRepository {

    private final IngredientDao ingredientDao;
    private final LiveData<List<Ingredient>> allIngredients;
    private final ExecutorService databaseExecutor;

    public interface UrgentIngredientCallback {
        void onLoaded(Ingredient ingredient);
    }

    public interface SingleIngredientCallback {
        void onLoaded(Ingredient ingredient);
    }

    public IngredientRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        this.ingredientDao = db.ingredientDao();
        this.allIngredients = ingredientDao.getAllIngredientsSortedByExpiration();
        // Pool dedicado de threads para operações de disco/banco de dados
        this.databaseExecutor = Executors.newFixedThreadPool(2);
    }

    /**
     * Retorna o fluxo reativo de todos os ingredientes ordenados por data de expiração.
     */
    public LiveData<List<Ingredient>> getAllIngredients() {
        return allIngredients;
    }

    /**
     * Retorna ingredientes filtrados por categoria.
     */
    public LiveData<List<Ingredient>> getIngredientsByCategory(String category) {
        return ingredientDao.getIngredientsByCategory(category);
    }

    /**
     * Insere um novo ingrediente fora da Main Thread.
     */
    public void insert(Ingredient ingredient) {
        databaseExecutor.execute(() -> ingredientDao.insert(ingredient));
    }

    /**
     * Atualiza um ingrediente existente.
     */
    public void update(Ingredient ingredient) {
        databaseExecutor.execute(() -> ingredientDao.update(ingredient));
    }

    /**
     * Exclui um ingrediente da despensa.
     */
    public void delete(Ingredient ingredient) {
        databaseExecutor.execute(() -> ingredientDao.delete(ingredient));
    }

    /**
     * Busca de forma assíncrona o ingrediente mais crítico para a sugestão de receitas.
     */
    public void getMostUrgentIngredient(UrgentIngredientCallback callback) {
        databaseExecutor.execute(() -> {
            Ingredient urgent = ingredientDao.getMostUrgentIngredient();
            if (callback != null) {
                callback.onLoaded(urgent);
            }
        });
    }

    /**
     * Busca um ingrediente por ID de forma assíncrona.
     */
    public void getIngredientById(long id, SingleIngredientCallback callback) {
        databaseExecutor.execute(() -> {
            Ingredient item = ingredientDao.getIngredientById(id);
            if (callback != null) {
                callback.onLoaded(item);
            }
        });
    }
}
