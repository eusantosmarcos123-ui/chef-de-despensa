package com.exemplo.chefdedespensa.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.exemplo.chefdedespensa.data.model.Ingredient;
import com.exemplo.chefdedespensa.data.repository.IngredientRepository;

import java.util.List;

/**
 * ViewModel que expõe os dados da despensa e abstrai o ciclo de vida da Activity/Fragment.
 * Herda de AndroidViewModel para obter o Application context de forma segura sem memory leaks.
 */
public class PantryViewModel extends AndroidViewModel {

    private final IngredientRepository repository;
    private final LiveData<List<Ingredient>> allIngredients;

    public PantryViewModel(@NonNull Application application) {
        super(application);
        this.repository = new IngredientRepository(application);
        this.allIngredients = repository.getAllIngredients();
    }

    public LiveData<List<Ingredient>> getAllIngredients() {
        return allIngredients;
    }

    public LiveData<List<Ingredient>> getIngredientsByCategory(String category) {
        return repository.getIngredientsByCategory(category);
    }

    public void insert(Ingredient ingredient) {
        repository.insert(ingredient);
    }

    public void update(Ingredient ingredient) {
        repository.update(ingredient);
    }

    public void delete(Ingredient ingredient) {
        repository.delete(ingredient);
    }

    public void getMostUrgentIngredient(IngredientRepository.UrgentIngredientCallback callback) {
        repository.getMostUrgentIngredient(callback);
    }
}
