package com.exemplo.chefdedespensa.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.exemplo.chefdedespensa.R;
import com.exemplo.chefdedespensa.data.model.RecipeSummary;

import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private final Context context;
    private List<RecipeSummary> recipes = new ArrayList<>();

    public RecipeAdapter(Context context) {
        this.context = context;
    }

    public void setRecipes(List<RecipeSummary> recipes) {
        this.recipes = recipes != null ? recipes : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_receita, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecipeSummary item = recipes.get(position);

        holder.tvTitulo.setText(item.getStrMeal());

        // Carrega foto da receita do TheMealDB via Glide
        Glide.with(context)
                .load(item.getStrMealThumb())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .centerCrop()
                .into(holder.ivFoto);

        // Ao clicar, abre no navegador uma busca do prato no Google / YouTube
        holder.itemView.setOnClickListener(v -> {
            String query = "receita modo de preparo " + item.getStrMeal();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=" + Uri.encode(query)));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFoto;
        TextView tvTitulo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFoto = itemView.findViewById(R.id.ivFotoReceita);
            tvTitulo = itemView.findViewById(R.id.tvTituloReceita);
        }
    }
}
