package com.example.foodsaver;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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

        // --- NOVO: Verificar se já existe um usuário logado ---
        // Se sim, vai direto para a MenuActivity. Isso evita que o usuário tenha que logar toda vez que abre o app.
        SharedPreferences sharedPreferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        String emailLogado = sharedPreferences.getString("USER_EMAIL", null);

        if (emailLogado != null && !emailLogado.isEmpty()) {
            Log.i("LoginActivity", "Usuário já logado: " + emailLogado + ". Indo para MenuActivity.");
            Intent intentMenu = new Intent(LoginActivity.this, MenuActivity.class);
            startActivity(intentMenu);
            finish();
            return;
        }

        editEmail = findViewById(R.id.editEmail);
        editSenha = findViewById(R.id.editSenha);
        btEntrar = findViewById(R.id.btEntrar);
        btEsqueciSenha = findViewById(R.id.btEsqueciSenha);
        btCadastro = findViewById(R.id.btCadastro);

        db = new BancoDados(this);

        btEntrar.setOnClickListener(v -> {
            String email = editEmail.getText().toString().trim();
            String senha = editSenha.getText().toString();

            Log.d("LoginActivity", "Tentando login com Email: [" + email + "]");

            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha e-mail e senha", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean loginValido = db.verificarLogin(email, senha);
            Log.d("LoginActivity", "Resultado de verificarLogin: " + loginValido);

            if (loginValido) {
                Toast.makeText(this, "Login bem-sucedido!", Toast.LENGTH_SHORT).show();

                SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("USER_EMAIL", email);
                editor.apply();

                Intent tela = new Intent(this, MenuActivity.class);

                // Flags para limpar a pilha e garantir que o usuário não volte para Login com o botão "voltar"
                tela.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(tela);
                finish(); // Fecha a LoginActivity
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
