package com.exemplo.chefdedespensa.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TheMealDbResponse {
    @SerializedName("meals")
    private List<RecipeSummary> meals;

    public List<RecipeSummary> getMeals() {
        return meals;
    }

    public void setMeals(List<RecipeSummary> meals) {
        this.meals = meals;
    }
}
