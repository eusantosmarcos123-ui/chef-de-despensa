package com.exemplo.chefdedespensa.data.network;

import com.exemplo.chefdedespensa.data.model.TheMealDbResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TheMealDbService {
    @GET("api/json/v1/1/filter.php")
    Call<TheMealDbResponse> searchRecipesByIngredient(@Query("i") String ingredient);
}
