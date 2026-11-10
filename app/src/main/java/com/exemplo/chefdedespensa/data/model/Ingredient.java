package com.exemplo.chefdedespensa.data.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;
import java.util.Objects;

/**
 * Entidade central representando um item na despensa do usuário.
 * Mapeada para a tabela 'ingredients' no SQLite gerenciado pelo Room.
 */
@Entity(tableName = "ingredients")
public class Ingredient {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long id;

    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "quantity")
    private double quantity;

    @NonNull
    @ColumnInfo(name = "unit")
    private String unit; // Ex: "g", "kg", "ml", "L", "un", "xícara"

    @NonNull
    @ColumnInfo(name = "expiration_date")
    private Date expirationDate;

    @NonNull
    @ColumnInfo(name = "category")
    private String category; // Ex: "Hortifruti", "Laticínios", "Proteínas", "Grãos", "Bebidas", "Outros"

    /**
     * Construtor principal utilizado pelo Room para hidratação das entidades.
     */
    public Ingredient(long id, @NonNull String name, double quantity, @NonNull String unit,
                      @NonNull Date expirationDate, @NonNull String category) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expirationDate = expirationDate;
        this.category = category;
    }

    /**
     * Construtor de conveniência para instanciar novos ingredientes antes de persistir (onde id ainda não existe).
     * O Room ignora este construtor através da anotação @Ignore.
     */
    @Ignore
    public Ingredient(@NonNull String name, double quantity, @NonNull String unit,
                      @NonNull Date expirationDate, @NonNull String category) {
        this(0, name, quantity, unit, expirationDate, category);
    }

    // --- GETTERS E SETTERS ---

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = Objects.requireNonNull(name, "O nome do ingrediente não pode ser nulo");
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    @NonNull
    public String getUnit() {
        return unit;
    }

    public void setUnit(@NonNull String unit) {
        this.unit = Objects.requireNonNull(unit, "A unidade de medida não pode ser nula");
    }

    @NonNull
    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(@NonNull Date expirationDate) {
        this.expirationDate = Objects.requireNonNull(expirationDate, "A data de validade não pode ser nula");
    }

    @NonNull
    public String getCategory() {
        return category;
    }

    public void setCategory(@NonNull String category) {
        this.category = Objects.requireNonNull(category, "A categoria não pode ser nula");
    }

    // --- MÉTODOS DE NEGÓCIO DE DOMÍNIO (ZERO DESPERDÍCIO) ---

    /**
     * Retorna a quantidade de dias restantes até o vencimento.
     * Valores negativos indicam produtos já vencidos.
     */
    public long getDaysUntilExpiration() {
        long nowMillis = System.currentTimeMillis();
        long diffMillis = expirationDate.getTime() - nowMillis;
        return diffMillis / (1000L * 60L * 60L * 24L);
    }

    /**
     * Avalia o nível de criticidade do alimento para orientar a interface visual:
     * - CRÍTICO: < 3 dias
     * - ATENÇÃO: entre 3 e 7 dias
     * - SEGURO: > 7 dias
     */
    public ExpirationStatus getExpirationStatus() {
        long days = getDaysUntilExpiration();
        if (days < 3) {
            return ExpirationStatus.CRITICO;
        } else if (days <= 7) {
            return ExpirationStatus.ATENCAO;
        } else {
            return ExpirationStatus.SEGURO;
        }
    }

    public enum ExpirationStatus {
        CRITICO,
        ATENCAO,
        SEGURO
    }

    // --- EQUALS & HASHCODE (Crucial para DiffUtil no RecyclerView) ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ingredient that = (Ingredient) o;
        return id == that.id &&
                Double.compare(that.quantity, quantity) == 0 &&
                name.equals(that.name) &&
                unit.equals(that.unit) &&
                expirationDate.equals(that.expirationDate) &&
                category.equals(that.category);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, quantity, unit, expirationDate, category);
    }
}
