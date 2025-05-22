package com.example.foodsaver;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

public class CadastroAlimentos extends AppCompatActivity {

    EditText etNomeDescricao, etData;
    Spinner spinnerGrupo, spinnerQuantidade, spinnerTipoAlimento;
    Button btCadastrarAlimento, btVisualizarAlimento;
    BancoDados db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_alimentos);

        // Vinculando elementos da interface
        etNomeDescricao = findViewById(R.id.etNomeDescricao);
        etData = findViewById(R.id.etData);
        spinnerGrupo = findViewById(R.id.spinnerGrupo);
        spinnerQuantidade = findViewById(R.id.spinnerQuantidade);
        spinnerTipoAlimento = findViewById(R.id.spinnerTipoAlimento);
        btCadastrarAlimento = findViewById(R.id.btCadastrarAlimento);
        btVisualizarAlimento = findViewById(R.id.btVisualizarAlimento);

        db = new BancoDados(this);

        // Preenchendo os Spinners
        String[] grupos = {"Frutas", "Legumes", "Carnes", "Laticínios", "Grãos", "Outros"};
        String[] quantidades = {"Unidades", "Kilos", "Litros"};
        String[] locais = {"Geladeira", "Armário"};

        ArrayAdapter<String> adapterGrupo = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, grupos);
        adapterGrupo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGrupo.setAdapter(adapterGrupo);

        ArrayAdapter<String> adapterQtd = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, quantidades);
        adapterQtd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerQuantidade.setAdapter(adapterQtd);

        ArrayAdapter<String> adapterLocal = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, locais);
        adapterLocal.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoAlimento.setAdapter(adapterLocal);

        // Ação ao clicar no botão "Cadastrar Alimento"
        btCadastrarAlimento.setOnClickListener(v -> {
            String nome = etNomeDescricao.getText().toString().trim();
            String grupo = spinnerGrupo.getSelectedItem().toString();
            String qtd = spinnerQuantidade.getSelectedItem().toString();
            String validade = etData.getText().toString().trim();
            String local = spinnerTipoAlimento.getSelectedItem().toString();

            if (nome.isEmpty() || validade.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean sucesso = db.cadastrarAlimento(nome, grupo, qtd, validade, local);
            if (sucesso) {
                Toast.makeText(this, "Alimento cadastrado com sucesso", Toast.LENGTH_SHORT).show();
                limparCampos();
            } else {
                Toast.makeText(this, "Erro ao cadastrar alimento", Toast.LENGTH_SHORT).show();
            }
        });

        // (Opcional) Botão "Visualizar Alimentos" — você pode programar a lógica depois
        btVisualizarAlimento.setOnClickListener(v -> {
            Intent intent = new Intent(CadastroAlimentos.this, VisualizacaoActivity.class);
            startActivity(intent);
        });
    }

    private void limparCampos() {
        etNomeDescricao.setText("");
        etData.setText("");
        spinnerGrupo.setSelection(0);
        spinnerQuantidade.setSelection(0);
        spinnerTipoAlimento.setSelection(0);
    }
}