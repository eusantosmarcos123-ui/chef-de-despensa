package com.exemplo.chefdedespensa.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.exemplo.chefdedespensa.R;
import com.exemplo.chefdedespensa.data.model.Ingredient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder> {

    public interface OnDeleteClickListener {
        void onDeleteClick(Ingredient ingredient);
    }

    public interface OnEditClickListener {
        void onEditClick(Ingredient ingredient);
    }

    private final Context context;
    private List<Ingredient> ingredients = new ArrayList<>();
    private final OnDeleteClickListener deleteListener;
    private final OnEditClickListener editListener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public IngredientAdapter(Context context, OnDeleteClickListener deleteListener, OnEditClickListener editListener) {
        this.context = context;
        this.deleteListener = deleteListener;
        this.editListener = editListener;
    }

    public IngredientAdapter(Context context, OnDeleteClickListener deleteListener) {
        this(context, deleteListener, null);
    }

    public void setIngredients(List<Ingredient> newIngredients) {
        this.ingredients = newIngredients != null ? newIngredients : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ingrediente, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ingredient item = ingredients.get(position);

        holder.tvNome.setText(item.getName());
        
        // Ex: "Hortifruti • 2.0 kg"
        String infoQuantidade = item.getCategory() + " • " + item.getQuantity() + " " + item.getUnit();
        holder.tvInfoQuantidade.setText(infoQuantidade);

        holder.tvDataValidade.setText("Validade: " + dateFormat.format(item.getExpirationDate()));

        // ================= LÓGICA DE CORES DO BADGE (CRÍTICO / ATENÇÃO / SEGURO) =================
        long days = item.getDaysUntilExpiration();
        Ingredient.ExpirationStatus status = item.getExpirationStatus();

        if (status == Ingredient.ExpirationStatus.CRITICO) {
            holder.tvBadgeValidade.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_badge_vencido));
            holder.tvBadgeValidade.setTextColor(ContextCompat.getColor(context, R.color.badge_vencido_text));
            
            if (days < 0) {
                holder.tvBadgeValidade.setText("Vencido há " + Math.abs(days) + "d");
            } else if (days == 0) {
                holder.tvBadgeValidade.setText("🚨 Vence hoje!");
            } else if (days == 1) {
                holder.tvBadgeValidade.setText("⚠️ Vence amanhã!");
            } else {
                holder.tvBadgeValidade.setText("⚠️ Vence em " + days + " dias");
            }
        } else if (status == Ingredient.ExpirationStatus.ATENCAO) {
            holder.tvBadgeValidade.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_badge_urgente));
            holder.tvBadgeValidade.setTextColor(ContextCompat.getColor(context, R.color.badge_urgente_text));
            holder.tvBadgeValidade.setText("Vence em " + days + " dias");
        } else {
            // SEGURO
            holder.tvBadgeValidade.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_badge_seguro));
            holder.tvBadgeValidade.setTextColor(ContextCompat.getColor(context, R.color.badge_seguro_text));
            holder.tvBadgeValidade.setText("Vence em " + days + " dias");
        }

        // Ação de excluir item
        holder.btnDeletar.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDeleteClick(item);
            }
        });

        // Ação de editar item
        holder.btnEditar.setOnClickListener(v -> {
            if (editListener != null) {
                editListener.onEditClick(item);
            }
        });

        // Toque no card inteiro também aciona a edição
        holder.itemView.setOnClickListener(v -> {
            if (editListener != null) {
                editListener.onEditClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNome, tvInfoQuantidade, tvDataValidade, tvBadgeValidade;
        ImageButton btnDeletar, btnEditar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNome = itemView.findViewById(R.id.tvNomeIngrediente);
            tvInfoQuantidade = itemView.findViewById(R.id.tvInfoQuantidadeCategoria);
            tvDataValidade = itemView.findViewById(R.id.tvDataValidade);
            tvBadgeValidade = itemView.findViewById(R.id.tvBadgeValidade);
            btnDeletar = itemView.findViewById(R.id.btnDeletar);
            btnEditar = itemView.findViewById(R.id.btnEditar);
        }
    }
}
