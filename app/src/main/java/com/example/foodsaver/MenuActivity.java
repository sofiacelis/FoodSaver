package com.example.foodsaver;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;


public class MenuActivity extends AppCompatActivity implements View.OnClickListener {
    ImageButton btMNUCadastroAlimentos, btMNUVisualizacao, btMNUControle, btMNUConfig;
    Button btMNUSobre;
    String email="";


    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Intent tela = getIntent();
        Bundle parametros = tela.getExtras();
        if (parametros != null && parametros.containsKey("email")) {
            email = parametros.getString("email");
        }


        btMNUCadastroAlimentos = findViewById(R.id.image_button_cadastro);
        btMNUVisualizacao= findViewById(R.id.image_button_visualizacao);
        btMNUControle= findViewById(R.id.image_button_controle);
        btMNUConfig= findViewById(R.id.image_button_config);
        btMNUSobre = findViewById(R.id.btSobre);



        btMNUCadastroAlimentos.setOnClickListener(this);
        btMNUVisualizacao.setOnClickListener(this);
        btMNUControle.setOnClickListener(this);
        btMNUConfig.setOnClickListener(this);
        btMNUSobre.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.image_button_cadastro){
            Intent tela = new Intent(this, CadastroAlimentos.class);
            startActivity(tela);
        }
        if (v.getId()==R.id.image_button_visualizacao){
            Intent tela = new Intent(this, VisualizacaoActivity.class);
            startActivity(tela);
        }
        if (v.getId()==R.id.image_button_controle){
            Intent tela = new Intent(this, VencimentoActivity.class);
            startActivity(tela);
        }
        if (v.getId()==R.id.image_button_config){
            Intent tela = new Intent(this, ConfigActivity.class);
            startActivity(tela);
        }
        if (v.getId()==R.id.btSobre){
            Intent tela = new Intent(this, SobreActivity.class);
            startActivity(tela);
        }
    }
}