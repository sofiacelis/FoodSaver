package com.example.foodsaver;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText editEmail, editSenha;
    Button btEntrar, btEsqueciSenha, btCadastro;
    BancoDados db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editEmail = findViewById(R.id.editEmail);
        editSenha = findViewById(R.id.editSenha);
        btEntrar = findViewById(R.id.btEntrar);
        btEsqueciSenha = findViewById(R.id.btEsqueciSenha);
        btCadastro = findViewById(R.id.btCadastro);

        db = new BancoDados(this);

        btEntrar.setOnClickListener(v -> {
            String email = editEmail.getText().toString();
            String senha = editSenha.getText().toString();

            if (db.verificarLogin(email, senha)) {
                Toast.makeText(this, "Login bem-sucedido", Toast.LENGTH_SHORT).show();
                // Aqui pode abrir a próxima tela do app
                Intent tela = new Intent(this, MenuActivity.class);
                tela.putExtra("email", email); // aqui "email" deve ser a variável que você capturou do usuário
                startActivity(tela);
                finish();
            } else {
                Toast.makeText(this, "Email ou senha inválidos", Toast.LENGTH_SHORT).show();
            }
        });

        btEsqueciSenha.setOnClickListener(v -> {
            startActivity(new Intent(this, RecuperarSenhaActivity.class));
        });

        btCadastro.setOnClickListener(v -> {
            startActivity(new Intent(this, CadastroActivity.class));
        });
    }
}
