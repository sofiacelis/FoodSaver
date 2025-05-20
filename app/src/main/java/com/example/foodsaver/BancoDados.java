package com.example.foodsaver;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BancoDados extends SQLiteOpenHelper {

    private static final String NOME_BANCO = "BancoApp.db";
    private static final int VERSAO_BANCO = 1;

    private static final String TABELA = "usuarios";
    private static final String COL_ID = "id";
    private static final String COL_NOME = "nome";
    private static final String COL_CPF = "cpf";
    private static final String COL_EMAIL = "email";
    private static final String COL_SENHA = "senha";

    public BancoDados(Context context) {
        super(context, NOME_BANCO, null, VERSAO_BANCO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABELA + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_NOME + " TEXT NOT NULL, "
                + COL_CPF + " TEXT NOT NULL, "
                + COL_EMAIL + " TEXT NOT NULL UNIQUE, "
                + COL_SENHA + " TEXT NOT NULL)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Atualizar esquema se necessário, por enquanto só recria a tabela
        db.execSQL("DROP TABLE IF EXISTS " + TABELA);
        onCreate(db);
    }


    public boolean cadastrarUsuario(String nome, String cpf, String email, String senha) {
        if (emailExiste(email)) {
            return false; // e-mail já cadastrado
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put(COL_NOME, nome);
        valores.put(COL_CPF, cpf);
        valores.put(COL_EMAIL, email);
        valores.put(COL_SENHA, senha);

        long resultado = db.insert(TABELA, null, valores);
        return resultado != -1;
    }


    public boolean emailExiste(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABELA + " WHERE " + COL_EMAIL + " = ?", new String[]{email});
        boolean existe = cursor.getCount() > 0;
        cursor.close();
        return existe;
    }


    public boolean verificarLogin(String email, String senha) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABELA + " WHERE " + COL_EMAIL + " = ? AND " + COL_SENHA + " = ?",
                new String[]{email, senha});
        boolean valido = cursor.getCount() > 0;
        cursor.close();
        return valido;
    }


    public boolean atualizarSenha(String email, String novaSenha) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put(COL_SENHA, novaSenha);

        int linhasAfetadas = db.update(TABELA, valores, COL_EMAIL + " = ?", new String[]{email});
        return linhasAfetadas > 0;
    }
}
