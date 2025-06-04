package com.example.foodsaver;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class VisualizacaoActivity extends AppCompatActivity {

    LinearLayout layoutProdutos;
    Spinner spinnerGrupo, spinnerLocal;
    BancoDados db;
    private String emailUsuarioLogado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizacao);

        layoutProdutos = findViewById(R.id.layoutProdutos);
        spinnerGrupo = findViewById(R.id.spinnerGrupo);
        spinnerLocal = findViewById(R.id.spinnerLocal);
        db = new BancoDados(this);

        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        emailUsuarioLogado = prefs.getString("USER_EMAIL", null);

        if (emailUsuarioLogado == null || emailUsuarioLogado.isEmpty()) {
            Toast.makeText(this, "Usuário não identificado. Faça login.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        String[] grupos = {"Todos", "Frutas", "Legumes", "Carnes", "Laticínios", "Grãos", "Carboidratos","Outros"};
        ArrayAdapter<String> adapterGrupo = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, grupos);
        adapterGrupo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGrupo.setAdapter(adapterGrupo);

        String[] locais = {"Todos", "Geladeira", "Armário", "Congelador", "Outro"};
        ArrayAdapter<String> adapterLocal = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, locais);
        adapterLocal.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocal.setAdapter(adapterLocal);

        AdapterView.OnItemSelectedListener listenerFiltros = new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                recarregarLista();
            }
            public void onNothingSelected(AdapterView<?> parent) {}
        };
        spinnerGrupo.setOnItemSelectedListener(listenerFiltros);
        spinnerLocal.setOnItemSelectedListener(listenerFiltros);

        recarregarLista();
    }

    private void recarregarLista() {
        if (emailUsuarioLogado == null || emailUsuarioLogado.isEmpty()) {
            Log.e("VisualizacaoActivity", "Email do usuário nulo ou vazio ao recarregar lista.");
            Toast.makeText(this, "Erro ao identificar usuário.", Toast.LENGTH_SHORT).show();
            layoutProdutos.removeAllViews();
            TextView erroMsg = new TextView(this);
            erroMsg.setText("Não foi possível carregar os alimentos. Faça login novamente.");
            layoutProdutos.addView(erroMsg);
            return;
        }
        String grupoSelecionado = spinnerGrupo.getSelectedItem().toString();
        String localSelecionado = spinnerLocal.getSelectedItem().toString();
        layoutProdutos.removeAllViews();
        carregarAlimentos(emailUsuarioLogado, grupoSelecionado, localSelecionado);
    }

    private void carregarAlimentos(String emailProprietario, String grupo, String local) {
        Cursor cursor = db.listarAlimentosFiltrados(emailProprietario, grupo, local);

        if (cursor == null) {
            Log.e("VisualizacaoActivity", "Cursor nulo ao carregar alimentos.");
            TextView erro = new TextView(this);
            erro.setText("Erro ao carregar alimentos.");
            layoutProdutos.addView(erro);
            return;
        }

        if (cursor.getCount() == 0) {
            TextView vazio = new TextView(this);
            vazio.setText("Nenhum alimento cadastrado para os filtros selecionados.");
            LinearLayout.LayoutParams paramsMsgVazio = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            paramsMsgVazio.gravity = Gravity.CENTER;
            vazio.setGravity(Gravity.CENTER);
            vazio.setPadding(0, 50, 0, 0);
            layoutProdutos.addView(vazio, paramsMsgVazio);
            cursor.close();
            return;
        }

        try {
            while (cursor.moveToNext()) {
                final int id = cursor.getInt(cursor.getColumnIndexOrThrow(BancoDados.COL_ID_ALIMENTO));
                final String nomeOriginal = cursor.getString(cursor.getColumnIndexOrThrow(BancoDados.COL_NOME_ALIMENTO));
                final String validadeOriginalISO = cursor.getString(cursor.getColumnIndexOrThrow(BancoDados.COL_VALIDADE_ALIMENTO));
                final String quantidadeOriginal = cursor.getString(cursor.getColumnIndexOrThrow(BancoDados.COL_QUANTIDADE_ALIMENTO));

                LinearLayout container = new LinearLayout(this);
                container.setOrientation(LinearLayout.VERTICAL);
                container.setPadding(20, 20, 20, 20);
                container.setBackgroundColor(0xFFF5F5F5);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, 30);
                container.setLayoutParams(params);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    container.setElevation(10);
                }

                TextView tvNome = new TextView(this);
                tvNome.setText(nomeOriginal);
                tvNome.setTextSize(18);
                tvNome.setPadding(0, 0, 0, 8);
                tvNome.setTypeface(null, Typeface.BOLD);

                TextView tvValidade = new TextView(this);
                tvValidade.setText("Vencimento: " + formatarDataParaExibicao(validadeOriginalISO));

                TextView tvQtd = new TextView(this);
                tvQtd.setText("Qtd: " + quantidadeOriginal);

                LinearLayout botoesLayout = new LinearLayout(this);
                botoesLayout.setOrientation(LinearLayout.HORIZONTAL);
                botoesLayout.setGravity(Gravity.END);

                Button btExcluir = new Button(this);
                btExcluir.setText("Excluir");

                btExcluir.setOnClickListener(v -> {
                    int linhasAfetadas = db.getWritableDatabase().delete(
                            BancoDados.TABELA_ALIMENTOS,
                            BancoDados.COL_ID_ALIMENTO + " = ? AND " + BancoDados.COL_EMAIL_PROPRIETARIO_ALIMENTO + " = ?",
                            new String[]{String.valueOf(id), emailUsuarioLogado});
                    if (linhasAfetadas > 0) {
                        Toast.makeText(VisualizacaoActivity.this, "Alimento excluído", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(VisualizacaoActivity.this, "Erro ao excluir", Toast.LENGTH_SHORT).show();
                    }
                    recarregarLista();
                });

                Button btEditar = new Button(this);
                btEditar.setText("Editar");
                btEditar.setOnClickListener(v -> {

                    container.removeAllViews();

                    EditText etNomeEdit = new EditText(this);
                    etNomeEdit.setText(nomeOriginal);
                    etNomeEdit.setHint("Nome do alimento");

                    EditText etValidadeEdit = new EditText(this);
                    etValidadeEdit.setText(formatarDataParaExibicao(validadeOriginalISO));
                    etValidadeEdit.setHint("Validade (DD/MM/AAAA)");

                    EditText etQtdEdit = new EditText(this);
                    etQtdEdit.setText(quantidadeOriginal);
                    etQtdEdit.setHint("Quantidade");

                    container.addView(etNomeEdit);
                    container.addView(etValidadeEdit);
                    container.addView(etQtdEdit);

                    LinearLayout layoutBotoesEdicao = new LinearLayout(this);
                    layoutBotoesEdicao.setOrientation(LinearLayout.HORIZONTAL);
                    layoutBotoesEdicao.setGravity(Gravity.END);

                    Button btSalvar = new Button(this);
                    btSalvar.setText("Salvar");
                    btSalvar.setOnClickListener(salvarView -> {
                        String nomeEditado = etNomeEdit.getText().toString().trim();
                        String validadeDigitada = etValidadeEdit.getText().toString().trim();
                        String quantidadeEditada = etQtdEdit.getText().toString().trim();

                        String validadeParaSalvarISO = null;
                        if (!validadeDigitada.isEmpty()) {
                            validadeParaSalvarISO = converterDataParaISO(validadeDigitada);
                            if (validadeParaSalvarISO == null) {
                                Toast.makeText(VisualizacaoActivity.this, "Data de validade inválida. Use DD/MM/AAAA.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }

                        if (nomeEditado.isEmpty()) {
                            Toast.makeText(VisualizacaoActivity.this, "O nome do alimento não pode ser vazio.", Toast.LENGTH_SHORT).show();
                            return;
                        }


                        ContentValues valores = new ContentValues();
                        valores.put(BancoDados.COL_NOME_ALIMENTO, nomeEditado);
                        if (validadeParaSalvarISO != null) {
                            valores.put(BancoDados.COL_VALIDADE_ALIMENTO, validadeParaSalvarISO);
                        } else {

                        }
                        valores.put(BancoDados.COL_QUANTIDADE_ALIMENTO, quantidadeEditada);

                        int linhasAfetadasUpdate = db.getWritableDatabase().update(
                                BancoDados.TABELA_ALIMENTOS,
                                valores,
                                BancoDados.COL_ID_ALIMENTO + " = ? AND " + BancoDados.COL_EMAIL_PROPRIETARIO_ALIMENTO + " = ?",
                                new String[]{String.valueOf(id), emailUsuarioLogado});

                        if (linhasAfetadasUpdate > 0) {
                            Toast.makeText(VisualizacaoActivity.this, "Alimento atualizado", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(VisualizacaoActivity.this, "Nenhuma alteração feita ou erro ao atualizar.", Toast.LENGTH_SHORT).show();
                        }
                        recarregarLista();
                    });

                    Button btCancelar = new Button(this);
                    btCancelar.setText("Cancelar");
                    btCancelar.setOnClickListener(cancelarView -> recarregarLista());

                    layoutBotoesEdicao.addView(btSalvar);
                    layoutBotoesEdicao.addView(btCancelar);
                    container.addView(layoutBotoesEdicao);
                });

                botoesLayout.addView(btExcluir);
                botoesLayout.addView(btEditar);

                container.addView(tvNome);
                container.addView(tvValidade);
                container.addView(tvQtd);
                container.addView(botoesLayout);

                layoutProdutos.addView(container);
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }

    private String formatarDataParaExibicao(String dataISO) { // yyyy-MM-dd -> dd/MM/yyyy
        if (dataISO == null || dataISO.trim().isEmpty()) {
            return ""; // Ou "N/A"
        }
        try {
            SimpleDateFormat formatoEntrada = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date data = formatoEntrada.parse(dataISO);
            SimpleDateFormat formatoSaida = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return formatoSaida.format(data);
        } catch (ParseException e) {
            Log.e("VisualizacaoActivity", "Erro ao formatar dataISO para exibição: " + dataISO, e);
            return dataISO;
        }
    }

    private String converterDataParaISO(String dataBR) { // dd/MM/yyyy -> yyyy-MM-dd
        if (dataBR == null || dataBR.trim().isEmpty()) {
            return null;
        }
        try {
            SimpleDateFormat formatoEntrada = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            formatoEntrada.setLenient(false);
            Date data = formatoEntrada.parse(dataBR);
            SimpleDateFormat formatoSaida = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return formatoSaida.format(data);
        } catch (ParseException e) {
            Log.e("VisualizacaoActivity", "Erro ao converter dataBR para ISO: " + dataBR, e);
            return null;
        }
    }
}