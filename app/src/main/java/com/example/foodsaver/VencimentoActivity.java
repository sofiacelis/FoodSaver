package com.example.foodsaver;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class VencimentoActivity extends AppCompatActivity {

    LinearLayout layoutVencimentos;
    BancoDados db;
    private String emailUsuarioLogado;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vencimento);

        layoutVencimentos = findViewById(R.id.layoutVencimentos);
        db = new BancoDados(this);
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        emailUsuarioLogado = prefs.getString("USER_EMAIL", null);

        if (emailUsuarioLogado == null || emailUsuarioLogado.isEmpty()) {
            Toast.makeText(this, "Usuário não identificado. Por favor, faça login.", Toast.LENGTH_LONG).show();
            Log.e("VencimentoActivity", "Email do usuário não encontrado nas SharedPreferences.");
            finish();
            return;
        }
        // --- FIM DA MODIFICAÇÃO ---

        carregarAlimentosVencendo();
    }

    private void carregarAlimentosVencendo() {
        if (emailUsuarioLogado == null || emailUsuarioLogado.isEmpty()) {
            Log.w("VencimentoActivity", "Tentativa de carregar alimentos vencendo sem email de usuário.");
            TextView erroMsg = new TextView(this);
            erroMsg.setText("Não foi possível carregar os dados. Usuário não identificado.");
            erroMsg.setTextColor(Color.RED);
            layoutVencimentos.removeAllViews();
            layoutVencimentos.addView(erroMsg);
            return;
        }

        Cursor cursor = db.listarAlimentosProximosDoVencimento(emailUsuarioLogado);

        if (cursor == null) {
            Log.e("VencimentoActivity", "Cursor nulo ao carregar alimentos próximos do vencimento.");
            TextView erro = new TextView(this);
            erro.setText("Erro ao buscar dados de vencimento.");
            erro.setTextColor(Color.RED);
            layoutVencimentos.removeAllViews();
            layoutVencimentos.addView(erro);
            return;
        }

        layoutVencimentos.removeAllViews();

        if (cursor.getCount() == 0) {
            TextView vazio = new TextView(this);
            vazio.setText("Nenhum alimento com vencimento próximo.");
            layoutVencimentos.addView(vazio);
            cursor.close();
            return;
        }

        try {
            while (cursor.moveToNext()) {
                String nome = cursor.getString(cursor.getColumnIndexOrThrow(BancoDados.COL_NOME_ALIMENTO));
                String validade = cursor.getString(cursor.getColumnIndexOrThrow(BancoDados.COL_VALIDADE_ALIMENTO));
                String quantidade = cursor.getString(cursor.getColumnIndexOrThrow(BancoDados.COL_QUANTIDADE_ALIMENTO));

                LinearLayout container = new LinearLayout(this);
                container.setOrientation(LinearLayout.VERTICAL);
                container.setPadding(20, 20, 20, 20);
                container.setBackgroundColor(0xFFF5F5F5);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 0, 0, 30);
                container.setLayoutParams(params);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    container.setElevation(10);
                }


                TextView tvNome = new TextView(this);
                tvNome.setText(nome);
                tvNome.setTextSize(18);
                tvNome.setTypeface(null, Typeface.BOLD);
                tvNome.setPadding(0, 0, 0, 8);

                TextView tvValidade = new TextView(this);
                tvValidade.setText("Vencimento: " + formatarDataParaExibicao(validade));


                TextView tvQtd = new TextView(this);
                tvQtd.setText("Qtd: " + quantidade);

                container.addView(tvNome);
                container.addView(tvValidade);
                container.addView(tvQtd);

                layoutVencimentos.addView(container);
            }
        } catch (IllegalArgumentException e) {
            Log.e("VencimentoActivity", "Erro ao ler coluna do cursor: " + e.getMessage());
            TextView erroMsg = new TextView(this);
            erroMsg.setText("Ocorreu um erro ao processar os dados dos alimentos.");
            erroMsg.setTextColor(Color.RED);
            layoutVencimentos.removeAllViews();
            layoutVencimentos.addView(erroMsg);
        }
        finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }

    private String formatarDataParaExibicao(String dataISO) {
        if (dataISO == null || dataISO.trim().isEmpty()) {
            return "N/A";
        }
        try {
            java.text.SimpleDateFormat formatoISO = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
            java.util.Date dataUtil = formatoISO.parse(dataISO);

            if (dataUtil == null) return dataISO;

            java.text.SimpleDateFormat formatoBR = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
            return formatoBR.format(dataUtil);
        } catch (java.text.ParseException e) {
            Log.w("VencimentoActivity", "Erro ao formatar data para exibição: " + dataISO, e);
            return dataISO;
        }
    }
}