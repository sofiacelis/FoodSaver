package com.example.foodsaver;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RecuperarSenhaActivity extends AppCompatActivity {

    EditText editEmailRecuperacao;
    Button btEnviarRecuperacao;
    BancoDados db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_senha);

        editEmailRecuperacao = findViewById(R.id.editEmailRecuperacao);
        btEnviarRecuperacao = findViewById(R.id.btEnviarRecuperacao);

        db = new BancoDados(this);

        btEnviarRecuperacao.setOnClickListener(v -> {
            String email = editEmailRecuperacao.getText().toString();

            if (email.isEmpty()) {
                Toast.makeText(this, "Digite seu email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!db.emailExiste(email)) {
                Toast.makeText(this, "Email não cadastrado", Toast.LENGTH_SHORT).show();
                return;
            }


            Toast.makeText(this, "Solicitação enviada para o seu email (simulado)", Toast.LENGTH_LONG).show();
        });
    }
}
