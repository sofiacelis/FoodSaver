package com.example.foodsaver;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CadastroActivity extends AppCompatActivity {

    EditText nomeCadastro, emailCadastro, criarSenha, confirmarSenha;
    Button btCadastrar;
    BancoDados db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        nomeCadastro = findViewById(R.id.nomeCadastro);
        emailCadastro = findViewById(R.id.emailCadastro);
        criarSenha = findViewById(R.id.criarSenha);
        confirmarSenha = findViewById(R.id.confirmarSenha);
        btCadastrar = findViewById(R.id.btCadastrar);

        db = new BancoDados(this);

        btCadastrar.setOnClickListener(v -> {
            String nome = nomeCadastro.getText().toString().trim();
            String email = emailCadastro.getText().toString().trim();
            String senha = criarSenha.getText().toString();
            String confirma = confirmarSenha.getText().toString();

            if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || confirma.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (nome.length() < 5) {
                Toast.makeText(this, "O nome deve ter pelo menos 5 letras", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!email.contains("@") || !email.contains(".")) {
                Toast.makeText(this, "Digite um email válido", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!senha.equals(confirma)) {
                Toast.makeText(this, "Senhas não conferem", Toast.LENGTH_SHORT).show();
                return;
            }

            if (db.emailExiste(email)) {
                Toast.makeText(this, "Email já cadastrado", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean cadastrado = db.cadastrarUsuario(nome, email, senha);
            if (cadastrado) {
                Toast.makeText(this, "Cadastro realizado com sucesso", Toast.LENGTH_SHORT).show();
                finish(); // Fecha a tela e volta para o login
            } else {
                Toast.makeText(this, "Erro no cadastro", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
