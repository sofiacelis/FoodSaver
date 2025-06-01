package com.example.foodsaver;

import android.content.Intent;
import android.content.SharedPreferences; // NOVO
import android.os.Bundle;
import android.util.Log; // NOVO
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ConfigActivity extends AppCompatActivity {

    private Button btnExcluirConta;
    private BancoDados bancoDados;
    private String emailUsuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        bancoDados = new BancoDados(this);
        btnExcluirConta = findViewById(R.id.btnExcluirConta);
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        emailUsuarioLogado = prefs.getString("USER_EMAIL", null);

        if (emailUsuarioLogado == null || emailUsuarioLogado.isEmpty()) {
            Toast.makeText(this, "Erro: Usuário não identificado. Não é possível excluir a conta.", Toast.LENGTH_LONG).show();
            Log.e("ConfigActivity", "Email do usuário não encontrado. A funcionalidade de excluir conta será desabilitada.");
            btnExcluirConta.setEnabled(false);
        }

        if (btnExcluirConta.isEnabled()) {
            btnExcluirConta.setOnClickListener(v -> confirmarExclusao());
        }
    }

    private void confirmarExclusao() {
        if (emailUsuarioLogado == null || emailUsuarioLogado.isEmpty()) {
            Toast.makeText(this, "Não é possível excluir a conta: usuário não identificado.", Toast.LENGTH_LONG).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Excluir Conta")
                .setMessage("Tem certeza que deseja excluir sua conta? Todos os seus dados, incluindo alimentos cadastrados, serão perdidos permanentemente. Esta ação não pode ser desfeita.")
                .setPositiveButton("Sim, Excluir", (dialog, which) -> {
                    boolean excluiu = bancoDados.excluirUsuario(emailUsuarioLogado);
                    if (excluiu) {
                        Toast.makeText(ConfigActivity.this, "Conta excluída com sucesso!", Toast.LENGTH_LONG).show();
                        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.remove("USER_EMAIL");
                        editor.apply();
                        Intent intent = new Intent(ConfigActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(ConfigActivity.this, "Erro ao excluir conta. Tente novamente.", Toast.LENGTH_SHORT).show();
                        Log.e("ConfigActivity", "Falha ao excluir usuário: " + emailUsuarioLogado + " do banco de dados.");
                    }
                })
                .setNegativeButton("Cancelar", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
