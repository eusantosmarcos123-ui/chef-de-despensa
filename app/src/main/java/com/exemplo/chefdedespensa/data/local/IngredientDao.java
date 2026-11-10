package com.exemplo.chefdedespensa.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.exemplo.chefdedespensa.data.model.Ingredient;

import java.util.List;

/**
 * Data Access Object (DAO) para a entidade Ingredient.
 * Define todas as interações diretas com o SQLite de forma fortemente tipada.
 */
@Dao
public interface IngredientDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Ingredient ingredient);

    @Update
    void update(Ingredient ingredient);

    @Delete
    void delete(Ingredient ingredient);

    /**
     * Retorna todos os ingredientes ordenados de forma crescente pela data de vencimento.
     * Retorna LiveData: qualquer alteração no banco aciona os observadores na UI de modo reativo.
     */
    @Query("SELECT * FROM ingredients ORDER BY expiration_date ASC")
    LiveData<List<Ingredient>> getAllIngredientsSortedByExpiration();

    /**
     * Busca um ingrediente específico por ID.
     */
    @Query("SELECT * FROM ingredients WHERE id = :id LIMIT 1")
    Ingredient getIngredientById(long id);

    /**
     * Retorna o ingrediente mais próximo do vencimento (crítico).
     * Usado para atalhos rápidos de busca de receitas na API TheMealDB.
     */
    @Query("SELECT * FROM ingredients ORDER BY expiration_date ASC LIMIT 1")
    Ingredient getMostUrgentIngredient();

    /**
     * Busca ingredientes por categoria para filtros na despensa.
     */
    @Query("SELECT * FROM ingredients WHERE category = :category ORDER BY expiration_date ASC")
    LiveData<List<Ingredient>> getIngredientsByCategory(String category);

    /**
     * Remove todos os ingredientes (útil para testes ou limpeza de despensa).
     */
    @Query("DELETE FROM ingredients")
    void deleteAll();
}
