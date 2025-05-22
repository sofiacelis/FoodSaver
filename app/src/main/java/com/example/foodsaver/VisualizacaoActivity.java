package com.example.foodsaver;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
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

public class VisualizacaoActivity extends AppCompatActivity {

    LinearLayout layoutProdutos;
    Spinner spinnerGrupo, spinnerLocal;
    BancoDados db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizacao);

        layoutProdutos = findViewById(R.id.layoutProdutos);
        spinnerGrupo = findViewById(R.id.spinnerGrupo);
        spinnerLocal = findViewById(R.id.spinnerLocal);
        db = new BancoDados(this);

        // Preencher os filtros
        String[] grupos = {"Todos", "Frutas", "Legumes", "Carnes", "Laticínios", "Grãos"};
        String[] locais = {"Todos", "Geladeira", "Armário"};

        ArrayAdapter<String> adapterGrupo = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, grupos);
        adapterGrupo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGrupo.setAdapter(adapterGrupo);

        ArrayAdapter<String> adapterLocal = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, locais);
        adapterLocal.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocal.setAdapter(adapterLocal);

        // Eventos de mudança nos filtros
        spinnerGrupo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                recarregarLista();
            }

            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerLocal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                recarregarLista();
            }

            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Carrega a lista inicial
        carregarAlimentos("Todos", "Todos");
    }

    private void recarregarLista() {
        String grupoSelecionado = spinnerGrupo.getSelectedItem().toString();
        String localSelecionado = spinnerLocal.getSelectedItem().toString();
        layoutProdutos.removeAllViews();
        carregarAlimentos(grupoSelecionado, localSelecionado);
    }

    private void carregarAlimentos(String grupo, String local) {
        Cursor cursor = db.listarAlimentosFiltrados(grupo, local);

        if (cursor.getCount() == 0) {
            TextView vazio = new TextView(this);
            vazio.setText("Nenhum alimento cadastrado.");
            layoutProdutos.addView(vazio);
            return;
        }

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String nome = cursor.getString(cursor.getColumnIndexOrThrow("nome"));
            String validade = cursor.getString(cursor.getColumnIndexOrThrow("validade"));
            String quantidade = cursor.getString(cursor.getColumnIndexOrThrow("quantidade"));

            // Layout do card
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
            container.setElevation(10);

            // TextViews
            TextView tvNome = new TextView(this);
            tvNome.setText(nome);
            tvNome.setTextSize(18);
            tvNome.setPadding(0, 0, 0, 8);
            tvNome.setTypeface(null, android.graphics.Typeface.BOLD);

            TextView tvValidade = new TextView(this);
            tvValidade.setText("Vencimento: " + validade);

            TextView tvQtd = new TextView(this);
            tvQtd.setText("Qtd: " + quantidade);

            // Botões
            LinearLayout botoesLayout = new LinearLayout(this);
            botoesLayout.setOrientation(LinearLayout.HORIZONTAL);
            botoesLayout.setGravity(Gravity.CENTER);

            Button btExcluir = new Button(this);
            btExcluir.setText("Excluir");
            btExcluir.setTextColor(0xFFFFFFFF);
            btExcluir.setBackgroundColor(0xFF000000);
            btExcluir.setOnClickListener(v -> {
                db.getWritableDatabase().delete("alimentos", "id = ?", new String[]{String.valueOf(id)});
                Toast.makeText(this, "Alimento excluído", Toast.LENGTH_SHORT).show();
                recarregarLista();
            });

            Button btEditar = new Button(this);
            btEditar.setText("Editar");
            btEditar.setTextColor(0xFFFFFFFF);
            btEditar.setBackgroundColor(0xFF000000);
            btEditar.setOnClickListener(v -> {
                // Edição inline
                EditText etNome = new EditText(this);
                etNome.setText(nome);
                EditText etValidade = new EditText(this);
                etValidade.setText(validade);
                EditText etQtd = new EditText(this);
                etQtd.setText(quantidade);

                container.removeView(tvNome);
                container.removeView(tvValidade);
                container.removeView(tvQtd);
                container.removeView(botoesLayout);

                container.addView(etNome);
                container.addView(etValidade);
                container.addView(etQtd);

                // Novos botões
                LinearLayout novoLayout = new LinearLayout(this);
                novoLayout.setOrientation(LinearLayout.HORIZONTAL);
                novoLayout.setGravity(Gravity.CENTER);

                Button btSalvar = new Button(this);
                btSalvar.setText("Salvar");
                btSalvar.setTextColor(0xFFFFFFFF);
                btSalvar.setBackgroundColor(0xFF000000);

                Button btCancelar = new Button(this);
                btCancelar.setText("Cancelar");
                btCancelar.setTextColor(0xFFFFFFFF);
                btCancelar.setBackgroundColor(0xFF000000);

                novoLayout.addView(btSalvar);
                novoLayout.addView(btCancelar);
                container.addView(novoLayout);

                btSalvar.setOnClickListener(salvarView -> {
                    String novoNome = etNome.getText().toString();
                    String novaValidade = etValidade.getText().toString();
                    String novaQtd = etQtd.getText().toString();

                    ContentValues valores = new ContentValues();
                    valores.put("nome", novoNome);
                    valores.put("validade", novaValidade);
                    valores.put("quantidade", novaQtd);

                    db.getWritableDatabase().update("alimentos", valores, "id = ?", new String[]{String.valueOf(id)});
                    Toast.makeText(this, "Alimento atualizado", Toast.LENGTH_SHORT).show();
                    recarregarLista();
                });

                btCancelar.setOnClickListener(cancelarView -> recarregarLista());
            });

            botoesLayout.addView(btExcluir);
            botoesLayout.addView(btEditar);

            container.addView(tvNome);
            container.addView(tvValidade);
            container.addView(tvQtd);
            container.addView(botoesLayout);

            layoutProdutos.addView(container);
        }

        cursor.close();
    }
}
