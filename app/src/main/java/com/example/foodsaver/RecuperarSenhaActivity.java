package com.example.foodsaver;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RecuperarSenhaActivity extends AppCompatActivity {

    EditText editEmailRecuperacao;
    Button btEnviarRecuperacao;
    EditText novaSenhaRecuperacao;
    Button btAtualizarSenha;

    BancoDados db;

    String emailParaAtualizar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_senha);

        editEmailRecuperacao = findViewById(R.id.editEmailRecuperacao);
        btEnviarRecuperacao = findViewById(R.id.btEnviarRecuperacao);
        novaSenhaRecuperacao = findViewById(R.id.novaSenhaRecuperacao);
        btAtualizarSenha = findViewById(R.id.btAtualizarSenha);

        db = new BancoDados(this);

        btEnviarRecuperacao.setOnClickListener(v -> {
            String email = editEmailRecuperacao.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(this, "Digite seu email", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!db.emailExiste(email)) {
                Toast.makeText(this, "Email não cadastrado", Toast.LENGTH_SHORT).show();
                return;
            }

            // Simula envio de email e libera atualização da senha
            Toast.makeText(this, "Senha atualizada com sucesso", Toast.LENGTH_LONG).show();

            emailParaAtualizar = email;

            // Mostrar campo e botão para atualizar senha
            novaSenhaRecuperacao.setVisibility(EditText.VISIBLE);
            btAtualizarSenha.setVisibility(Button.VISIBLE);

            // Desabilitar o botão de enviar para evitar múltiplos cliques
            btEnviarRecuperacao.setEnabled(false);
            editEmailRecuperacao.setEnabled(false);
        });

        btAtualizarSenha.setOnClickListener(v -> {
            String novaSenha = novaSenhaRecuperacao.getText().toString();

            if (novaSenha.isEmpty()) {
                Toast.makeText(this, "Digite a nova senha", Toast.LENGTH_SHORT).show();
                return;
            }

            if (emailParaAtualizar == null) {
                Toast.makeText(this, "Erro inesperado. Tente novamente.", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean atualizado = db.atualizarSenha(emailParaAtualizar, novaSenha);
            if (atualizado) {
                Toast.makeText(this, "Senha atualizada com sucesso!", Toast.LENGTH_LONG).show();

                // Opcional: resetar tela para o estado inicial
                novaSenhaRecuperacao.setText("");
                novaSenhaRecuperacao.setVisibility(EditText.GONE);
                btAtualizarSenha.setVisibility(Button.GONE);
                btEnviarRecuperacao.setEnabled(true);
                editEmailRecuperacao.setEnabled(true);
                editEmailRecuperacao.setText("");
                emailParaAtualizar = null;
            } else {
                Toast.makeText(this, "Erro ao atualizar a senha.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
