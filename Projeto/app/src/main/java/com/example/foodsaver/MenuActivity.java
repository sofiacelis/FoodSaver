package com.example.foodsaver;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {
    ImageButton btMNUCadastroAlimentos, btMNUVisualizacao, btMNUControle, btMNUConfig;
    Button btMNUSobre;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String emailUsuarioLogado = prefs.getString("USER_EMAIL", null);

        if (emailUsuarioLogado == null || emailUsuarioLogado.isEmpty()) {
            Log.i("MenuActivity", "Nenhum usuário logado encontrado. Redirecionando para LoginActivity.");
            Intent intentLogin = new Intent(MenuActivity.this, LoginActivity.class);
            intentLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intentLogin);
            finish();
            return;
        }
        Log.i("MenuActivity", "Usuário " + emailUsuarioLogado + " logado. Exibindo menu.");

        setContentView(R.layout.activity_menu);

        btMNUCadastroAlimentos = findViewById(R.id.image_button_cadastro);
        btMNUVisualizacao = findViewById(R.id.image_button_visualizacao);
        btMNUControle = findViewById(R.id.image_button_controle);
        btMNUConfig = findViewById(R.id.image_button_config);
        btMNUSobre = findViewById(R.id.btSobre);

        btMNUCadastroAlimentos.setOnClickListener(this);
        btMNUVisualizacao.setOnClickListener(this);
        btMNUControle.setOnClickListener(this);
        btMNUConfig.setOnClickListener(this);
        btMNUSobre.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.image_button_cadastro) {
            Intent tela = new Intent(this, CadastroAlimentos.class);
            startActivity(tela);
        } else if (id == R.id.image_button_visualizacao) {
            Intent tela = new Intent(this, VisualizacaoActivity.class);
            startActivity(tela);
        } else if (id == R.id.image_button_controle) {
            Intent tela = new Intent(this, VencimentoActivity.class);
            startActivity(tela);
        } else if (id == R.id.image_button_config) {
            Intent tela = new Intent(this, ConfigActivity.class);
            startActivity(tela);
        } else if (id == R.id.btSobre) {
            Intent tela = new Intent(this, SobreActivity.class);
            startActivity(tela);
        }
    }
}