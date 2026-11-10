package com.exemplo.chefdedespensa.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.exemplo.chefdedespensa.R;
import com.exemplo.chefdedespensa.data.model.Ingredient;
import com.exemplo.chefdedespensa.ui.viewmodel.PantryViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CadastroActivity extends AppCompatActivity {

    private EditText etNome, etQuantidade, etValidade;
    private Spinner spinnerUnidade, spinnerCategoria;
    private Button btnSalvar;
    private PantryViewModel viewModel;
    private Date dataValidadeSelecionada = null;

    private boolean isEditMode = false;
    private long editingIngredientId = -1;

    private final String[] UNIDADES = {"g", "kg", "unidades", "ml", "L", "pacotes", "latas"};
    private final String[] CATEGORIAS = {"Hortifruti", "Laticínios & Ovos", "Proteínas & Carnes", "Grãos & Massas", "Padaria & Doces", "Bebidas", "Outros"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        viewModel = new ViewModelProvider(this).get(PantryViewModel.class);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarCadastro);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // Componentes da Interface
        etNome = findViewById(R.id.etNome);
        etQuantidade = findViewById(R.id.etQuantidade);
        etValidade = findViewById(R.id.etValidade);
        spinnerUnidade = findViewById(R.id.spinnerUnidade);
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        btnSalvar = findViewById(R.id.btnSalvar);

        // Configuração dos Spinners
        ArrayAdapter<String> adapterUnidades = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, UNIDADES);
        spinnerUnidade.setAdapter(adapterUnidades);

        ArrayAdapter<String> adapterCategorias = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, CATEGORIAS);
        spinnerCategoria.setAdapter(adapterCategorias);

        // DatePicker ao tocar no campo de validade
        etValidade.setOnClickListener(v -> exibirDatePicker());

        // Verificar se é Modo de Edição
        if (getIntent().getBooleanExtra("IS_EDIT", false)) {
            isEditMode = true;
            editingIngredientId = getIntent().getLongExtra("INGREDIENT_ID", -1);

            toolbar.setTitle("Editar Ingrediente");
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Editar Ingrediente");
            }
            btnSalvar.setText("Salvar Alterações");

            String nome = getIntent().getStringExtra("NOME");
            if (nome != null) etNome.setText(nome);

            double qtd = getIntent().getDoubleExtra("QUANTIDADE", 1.0);
            if (qtd == (long) qtd) {
                etQuantidade.setText(String.valueOf((long) qtd));
            } else {
                etQuantidade.setText(String.valueOf(qtd));
            }

            setSpinnerSelection(spinnerUnidade, UNIDADES, getIntent().getStringExtra("UNIDADE"));
            setSpinnerSelection(spinnerCategoria, CATEGORIAS, getIntent().getStringExtra("CATEGORIA"));

            long timestamp = getIntent().getLongExtra("VALIDADE_TIMESTAMP", 0);
            if (timestamp > 0) {
                dataValidadeSelecionada = new Date(timestamp);
                SimpleDateFormat brFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                etValidade.setText(brFormat.format(dataValidadeSelecionada));
            }
        }

        // Botão Salvar / Atualizar
        btnSalvar.setOnClickListener(v -> salvarIngrediente());
    }

    private void setSpinnerSelection(Spinner spinner, String[] array, String value) {
        if (value == null) return;
        for (int i = 0; i < array.length; i++) {
            if (array[i].equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void exibirDatePicker() {
        Calendar calendario = Calendar.getInstance();
        if (dataValidadeSelecionada != null) {
            calendario.setTime(dataValidadeSelecionada);
        }
        int ano = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            Calendar data = Calendar.getInstance();
            data.set(year, month, dayOfMonth, 23, 59, 59);
            dataValidadeSelecionada = data.getTime();

            SimpleDateFormat brFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            etValidade.setText(brFormat.format(dataValidadeSelecionada));
        }, ano, mes, dia);

        dialog.show();
    }

    private void salvarIngrediente() {
        String nome = etNome.getText().toString().trim();
        String strQtd = etQuantidade.getText().toString().trim();

        if (nome.isEmpty()) {
            etNome.setError("Informe o nome do alimento!");
            etNome.requestFocus();
            return;
        }

        if (strQtd.isEmpty()) {
            etQuantidade.setError("Informe a quantidade!");
            etQuantidade.requestFocus();
            return;
        }

        if (dataValidadeSelecionada == null) {
            Toast.makeText(this, "Selecione a data de validade!", Toast.LENGTH_SHORT).show();
            exibirDatePicker();
            return;
        }

        double quantidade;
        try {
            quantidade = Double.parseDouble(strQtd);
        } catch (NumberFormatException e) {
            etQuantidade.setError("Quantidade inválida!");
            return;
        }

        String unidade = spinnerUnidade.getSelectedItem().toString();
        String categoria = spinnerCategoria.getSelectedItem().toString();

        if (isEditMode) {
            Ingredient ingredienteAtualizado = new Ingredient(editingIngredientId, nome, quantidade, unidade, dataValidadeSelecionada, categoria);
            viewModel.update(ingredienteAtualizado);
            Toast.makeText(this, "Informações atualizadas com sucesso!", Toast.LENGTH_SHORT).show();
        } else {
            Ingredient novoIngrediente = new Ingredient(nome, quantidade, unidade, dataValidadeSelecionada, categoria);
            viewModel.insert(novoIngrediente);
            Toast.makeText(this, "Ingrediente adicionado à despensa!", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}
