package com.example.foodsaver;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BancoDados extends SQLiteOpenHelper {

    private static final String NOME_BANCO = "BancoApp.db";
    private static final int VERSAO_BANCO = 2;

    // Tabela de usuários
    private static final String TABELA_USUARIOS = "usuarios";
    private static final String COL_ID = "id";
    private static final String COL_NOME = "nome";
    private static final String COL_CPF = "cpf";
    private static final String COL_EMAIL = "email";
    private static final String COL_SENHA = "senha";

    // Tabela de alimentos
    private static final String TABELA_ALIMENTOS = "alimentos";
    private static final String COL_ID_ALIMENTO = "id";
    private static final String COL_NOME_ALIMENTO = "nome";
    private static final String COL_GRUPO = "grupoAlimentar";
    private static final String COL_QUANTIDADE = "quantidade";
    private static final String COL_VALIDADE = "validade";
    private static final String COL_LOCAL = "localArmazenamento";

    public BancoDados(Context context) {
        super(context, NOME_BANCO, null, VERSAO_BANCO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlUsuarios = "CREATE TABLE " + TABELA_USUARIOS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NOME + " TEXT NOT NULL, " +
                COL_CPF + " TEXT NOT NULL, " +
                COL_EMAIL + " TEXT NOT NULL UNIQUE, " +
                COL_SENHA + " TEXT NOT NULL)";

        String sqlAlimentos = "CREATE TABLE " + TABELA_ALIMENTOS + " (" +
                COL_ID_ALIMENTO + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NOME_ALIMENTO + " TEXT NOT NULL, " +
                COL_GRUPO + " TEXT, " +
                COL_QUANTIDADE + " TEXT, " +
                COL_VALIDADE + " TEXT, " +
                COL_LOCAL + " TEXT)";

        db.execSQL(sqlUsuarios);
        db.execSQL(sqlAlimentos);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_USUARIOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_ALIMENTOS);
        onCreate(db);
    }

    // ------------------------- USUÁRIOS -------------------------

    public boolean cadastrarUsuario(String nome, String cpf, String email, String senha) {
        if (emailExiste(email)) return false;

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put(COL_NOME, nome);
        valores.put(COL_CPF, cpf);
        valores.put(COL_EMAIL, email);
        valores.put(COL_SENHA, senha);

        long resultado = db.insert(TABELA_USUARIOS, null, valores);
        return resultado != -1;
    }

    public boolean emailExiste(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABELA_USUARIOS + " WHERE " + COL_EMAIL + " = ?", new String[]{email});
        boolean existe = cursor.getCount() > 0;
        cursor.close();
        return existe;
    }

    public boolean verificarLogin(String email, String senha) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABELA_USUARIOS + " WHERE " + COL_EMAIL + " = ? AND " + COL_SENHA + " = ?",
                new String[]{email, senha});
        boolean valido = cursor.getCount() > 0;
        cursor.close();
        return valido;
    }

    public boolean atualizarSenha(String email, String novaSenha) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put(COL_SENHA, novaSenha);

        int linhasAfetadas = db.update(TABELA_USUARIOS, valores, COL_EMAIL + " = ?", new String[]{email});
        return linhasAfetadas > 0;
    }

    // ------------------------- ALIMENTOS -------------------------

    public boolean cadastrarAlimento(String nome, String grupoAlimentar, String quantidade, String validade, String local) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put(COL_NOME_ALIMENTO, nome);
        valores.put(COL_GRUPO, grupoAlimentar);
        valores.put(COL_QUANTIDADE, quantidade);
        valores.put(COL_VALIDADE, validade);
        valores.put(COL_LOCAL, local);

        long resultado = db.insert(TABELA_ALIMENTOS, null, valores);
        return resultado != -1;
    }
    public Cursor listarAlimentosFiltrados(String grupo, String local) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM alimentos WHERE 1=1";

        if (!grupo.equals("Todos")) {
            query += " AND grupoAlimentar = '" + grupo + "'";
        }

        if (!local.equals("Todos")) {
            query += " AND localArmazenamento = '" + local + "'";
        }

        query += " ORDER BY validade ASC";
        return db.rawQuery(query, null);
    }

}
