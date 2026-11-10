package com.exemplo.chefdedespensa.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.exemplo.chefdedespensa.data.model.Ingredient;

/**
 * Ponto de entrada do Room Database para o Chef de Despensa.
 * Implementa o padrão Singleton Thread-Safe com verificação dupla (Double-Checked Locking).
 */
@Database(entities = {Ingredient.class}, version = 1, exportSchema = false)
@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "chef_despensa_db";
    private static volatile AppDatabase INSTANCE;

    public abstract IngredientDao ingredientDao();

    public static AppDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            DATABASE_NAME
                    )
                    // Estratégia de migração destrutiva para desenvolvimento inicial
                    .fallbackToDestructiveMigration()
                    .build();
                }
            }
        }
        return INSTANCE;
    }
}
