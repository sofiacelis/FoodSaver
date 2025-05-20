package com.example.foodsaver;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CadastroActivity extends AppCompatActivity {

    EditText nomeCadastro, editCPF, emailCadastro, criarSenha, confirmarSenha;
    Button btCadastrar;
    BancoDados db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        nomeCadastro = findViewById(R.id.nomeCadastro);
        editCPF = findViewById(R.id.editCPF);
        emailCadastro = findViewById(R.id.emailCadastro);
        criarSenha = findViewById(R.id.criarSenha);
        confirmarSenha = findViewById(R.id.confirmarSenha);
        btCadastrar = findViewById(R.id.btCadastrar);

        db = new BancoDados(this);

        btCadastrar.setOnClickListener(v -> {
            String nome = nomeCadastro.getText().toString();
            String cpf = editCPF.getText().toString();
            String email = emailCadastro.getText().toString();
            String senha = criarSenha.getText().toString();
            String confirma = confirmarSenha.getText().toString();

            if (nome.isEmpty() || cpf.isEmpty() || email.isEmpty() || senha.isEmpty() || confirma.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
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

            boolean cadastrado = db.cadastrarUsuario(nome, cpf, email, senha);
            if (cadastrado) {
                Toast.makeText(this, "Cadastro realizado com sucesso", Toast.LENGTH_SHORT).show();
                finish(); // fecha essa tela e volta para o login
            } else {
                Toast.makeText(this, "Erro no cadastro", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
