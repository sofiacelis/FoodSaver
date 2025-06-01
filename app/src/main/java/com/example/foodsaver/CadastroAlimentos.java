package com.example.foodsaver;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;

public class CadastroAlimentos extends AppCompatActivity {

    EditText etNomeDescricao, etData;
    Spinner spinnerGrupo, spinnerQuantidade, spinnerTipoAlimento;
    Button btCadastrarAlimento, btVisualizarAlimento;
    BancoDados db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_alimentos);

        etNomeDescricao = findViewById(R.id.etNomeDescricao);
        etData = findViewById(R.id.etData);
        spinnerGrupo = findViewById(R.id.spinnerGrupo);
        spinnerQuantidade = findViewById(R.id.spinnerQuantidade);
        spinnerTipoAlimento = findViewById(R.id.spinnerTipoAlimento);
        btCadastrarAlimento = findViewById(R.id.btCadastrarAlimento);
        btVisualizarAlimento = findViewById(R.id.btVisualizarAlimento);

        db = new BancoDados(this);

        String[] grupos = {"Frutas", "Legumes", "Carnes", "Laticínios", "Grãos", "Carboidratos", "Outros"};
        String[] quantidades = {"Unidades", "Kilos", "Litros"};
        String[] locais = {"Geladeira", "Armário", "Congelador", "Outro"};

        ArrayAdapter<String> adapterGrupo = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, grupos);
        adapterGrupo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGrupo.setAdapter(adapterGrupo);

        ArrayAdapter<String> adapterQtd = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, quantidades);
        adapterQtd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerQuantidade.setAdapter(adapterQtd);

        ArrayAdapter<String> adapterLocal = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, locais);
        adapterLocal.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoAlimento.setAdapter(adapterLocal);


        btCadastrarAlimento.setOnClickListener(v -> {
            String nome = etNomeDescricao.getText().toString().trim();
            String grupo = spinnerGrupo.getSelectedItem().toString();
            String qtd = spinnerQuantidade.getSelectedItem().toString();
            String validadeBr = etData.getText().toString().trim();
            String local = spinnerTipoAlimento.getSelectedItem().toString();

            if (nome.isEmpty() || validadeBr.isEmpty()) {
                Toast.makeText(this, "Preencha nome do alimento e validade.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Converter data para o formato ISO (YYYY-MM-DD)
            String validadeISO = converterDataParaISO(validadeBr);
            if (validadeISO == null) {
                Toast.makeText(this, "Data de validade inválida. Use o formato DD/MM/AAAA.", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
            String emailUsuarioLogado = prefs.getString("USER_EMAIL", null);

            if (emailUsuarioLogado == null || emailUsuarioLogado.isEmpty()) {
                Toast.makeText(this, "Erro: Usuário não identificado. Por favor, faça login novamente.", Toast.LENGTH_LONG).show();
                Intent intentLogin = new Intent(CadastroAlimentos.this, LoginActivity.class);
                startActivity(intentLogin);
                finish();
                return;
            }

            boolean sucesso = db.cadastrarAlimento(nome, grupo, qtd, validadeISO, local, emailUsuarioLogado);

            if (sucesso) {
                Toast.makeText(this, "Alimento cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                limparCampos();
            } else {
                Toast.makeText(this, "Erro ao cadastrar alimento. Verifique os dados ou tente novamente.", Toast.LENGTH_SHORT).show();
            }
        });

        btVisualizarAlimento.setOnClickListener(v -> {
            Intent intent = new Intent(CadastroAlimentos.this, VisualizacaoActivity.class);
            startActivity(intent);
        });
    }

    private String converterDataParaISO(String dataNoFormatoBrasileiro) {
        if (dataNoFormatoBrasileiro == null || dataNoFormatoBrasileiro.trim().isEmpty()) {
            return null;
        }
        try {
            SimpleDateFormat formatoEntrada = new SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
            formatoEntrada.setLenient(false);
            java.util.Date dataUtil = formatoEntrada.parse(dataNoFormatoBrasileiro);

            if (dataUtil == null) return null;
            SimpleDateFormat formatoSaidaISO = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
            return formatoSaidaISO.format(dataUtil);
        } catch (java.text.ParseException e) {
            Log.e("CadastroAlimentos", "Erro ao converter data: " + dataNoFormatoBrasileiro, e);
            return null;
        }
    }

    private void limparCampos() {
        etNomeDescricao.setText("");
        etData.setText("");
        if (spinnerGrupo.getAdapter() != null && spinnerGrupo.getAdapter().getCount() > 0) {
            spinnerGrupo.setSelection(0);
        }
        if (spinnerQuantidade.getAdapter() != null && spinnerQuantidade.getAdapter().getCount() > 0) {
            spinnerQuantidade.setSelection(0);
        }
        if (spinnerTipoAlimento.getAdapter() != null && spinnerTipoAlimento.getAdapter().getCount() > 0) {
            spinnerTipoAlimento.setSelection(0);
        }
    }
}

