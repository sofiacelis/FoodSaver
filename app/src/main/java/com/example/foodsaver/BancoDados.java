package com.example.foodsaver;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class BancoDados extends SQLiteOpenHelper {

    private static final String NOME_BANCO = "BancoApp.db";
    private static final int VERSAO_BANCO = 4;

    // Tabela de usuários
    public static final String TABELA_USUARIOS = "usuarios";
    public static final String COL_ID_USUARIO = "id";
    public static final String COL_NOME_USUARIO = "nome";
    public static final String COL_EMAIL_USUARIO = "email";
    public static final String COL_SENHA_USUARIO = "senha";

    // Tabela de alimentos
    public static final String TABELA_ALIMENTOS = "alimentos";
    public static final String COL_ID_ALIMENTO = "id_alimento";
    public static final String COL_NOME_ALIMENTO = "nome_alimento";
    public static final String COL_GRUPO_ALIMENTO = "grupoAlimentar";
    public static final String COL_QUANTIDADE_ALIMENTO = "quantidade";
    public static final String COL_VALIDADE_ALIMENTO = "validade";
    public static final String COL_LOCAL_ALIMENTO = "localArmazenamento";
    public static final String COL_EMAIL_PROPRIETARIO_ALIMENTO = "email_proprietario";

    public BancoDados(Context context) {
        super(context, NOME_BANCO, null, VERSAO_BANCO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlUsuarios = "CREATE TABLE " + TABELA_USUARIOS + " (" +
                COL_ID_USUARIO + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NOME_USUARIO + " TEXT NOT NULL, " +
                COL_EMAIL_USUARIO + " TEXT NOT NULL UNIQUE, " +
                COL_SENHA_USUARIO + " TEXT NOT NULL)";

        String sqlAlimentos = "CREATE TABLE " + TABELA_ALIMENTOS + " (" +
                COL_ID_ALIMENTO + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NOME_ALIMENTO + " TEXT NOT NULL, " +
                COL_GRUPO_ALIMENTO + " TEXT, " +
                COL_QUANTIDADE_ALIMENTO + " TEXT, " +
                COL_VALIDADE_ALIMENTO + " TEXT, " +
                COL_LOCAL_ALIMENTO + " TEXT, " +
                COL_EMAIL_PROPRIETARIO_ALIMENTO + " TEXT NOT NULL, " +
                "FOREIGN KEY(" + COL_EMAIL_PROPRIETARIO_ALIMENTO + ") REFERENCES " +
                TABELA_USUARIOS + "(" + COL_EMAIL_USUARIO + ") ON DELETE CASCADE)";

        db.execSQL(sqlUsuarios);
        db.execSQL(sqlAlimentos);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_USUARIOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABELA_ALIMENTOS);
        onCreate(db);
    }

    // Cadastrar usuários:
    public boolean cadastrarUsuario(String nome, String email, String senha) {
        if (emailExiste(email)) {
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put(COL_NOME_USUARIO, nome);
        valores.put(COL_EMAIL_USUARIO, email);
        valores.put(COL_SENHA_USUARIO, senha);

        long resultado = db.insert(TABELA_USUARIOS, null, valores);
        return resultado != -1;
    }

    public boolean emailExiste(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_ID_USUARIO + " FROM " + TABELA_USUARIOS + " WHERE " + COL_EMAIL_USUARIO + " = ?", new String[]{email});
        boolean existe = cursor.getCount() > 0;
        cursor.close();
        return existe;
    }

    public boolean verificarLogin(String email, String senha) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + COL_ID_USUARIO + " FROM " + TABELA_USUARIOS + " WHERE " + COL_EMAIL_USUARIO + " = ? AND " + COL_SENHA_USUARIO + " = ?",
                new String[]{email, senha});
        boolean valido = cursor.getCount() > 0;
        cursor.close();
        return valido;
    }

    public boolean atualizarSenha(String email, String novaSenha) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put(COL_SENHA_USUARIO, novaSenha);

        int linhasAfetadas = db.update(TABELA_USUARIOS, valores, COL_EMAIL_USUARIO + " = ?", new String[]{email});
        // db.close();
        return linhasAfetadas > 0;
    }

    public boolean excluirUsuario(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        int linhasAfetadas = db.delete(TABELA_USUARIOS, COL_EMAIL_USUARIO + " = ?", new String[]{email});
        Log.d("BancoDados", "Tentativa de excluir usuário: " + email + ". Linhas afetadas: " + linhasAfetadas);
        return linhasAfetadas > 0;
    }
    // Cadastrar alimentos:

    public boolean cadastrarAlimento(String nome, String grupoAlimentar, String quantidade, String validade, String local, String emailProprietario) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();

        valores.put(COL_NOME_ALIMENTO, nome);
        valores.put(COL_GRUPO_ALIMENTO, grupoAlimentar);
        valores.put(COL_QUANTIDADE_ALIMENTO, quantidade);
        valores.put(COL_VALIDADE_ALIMENTO, validade);
        valores.put(COL_LOCAL_ALIMENTO, local);
        valores.put(COL_EMAIL_PROPRIETARIO_ALIMENTO, emailProprietario);
        long resultado = db.insert(TABELA_ALIMENTOS, null, valores);
        return resultado != -1;
    }

    // Listar os alimentos filtrados:
    public Cursor listarAlimentosFiltrados(String emailProprietario, String grupo, String local) {
        SQLiteDatabase db = this.getReadableDatabase();
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT * FROM ").append(TABELA_ALIMENTOS);
        queryBuilder.append(" WHERE ").append(COL_EMAIL_PROPRIETARIO_ALIMENTO).append(" = ?");

        List<String> selectionArgsList = new ArrayList<>();
        selectionArgsList.add(emailProprietario);

        if (grupo != null && !grupo.equalsIgnoreCase("Todos") && !grupo.isEmpty()) {
            queryBuilder.append(" AND ").append(COL_GRUPO_ALIMENTO).append(" = ?");
            selectionArgsList.add(grupo);
        }

        if (local != null && !local.equalsIgnoreCase("Todos") && !local.isEmpty()) {
            queryBuilder.append(" AND ").append(COL_LOCAL_ALIMENTO).append(" = ?");
            selectionArgsList.add(local);
        }

        queryBuilder.append(" ORDER BY date(").append(COL_VALIDADE_ALIMENTO).append(") ASC");

        String[] selectionArgs = selectionArgsList.toArray(new String[0]);
        return db.rawQuery(queryBuilder.toString(), selectionArgs);
    }

    // Listar os alimentos próximos do vencimento:
    public Cursor listarAlimentosProximosDoVencimento(String emailProprietario) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABELA_ALIMENTOS +
                " WHERE " + COL_EMAIL_PROPRIETARIO_ALIMENTO + " = ? AND date(" + COL_VALIDADE_ALIMENTO + ") <= date('now', '+5 days')" +
                " ORDER BY date(" + COL_VALIDADE_ALIMENTO + ") ASC";
        return db.rawQuery(query, new String[]{emailProprietario});
    }
}