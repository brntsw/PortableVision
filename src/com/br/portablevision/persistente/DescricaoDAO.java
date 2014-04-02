package com.br.portablevision.persistente;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DescricaoDAO extends SQLiteOpenHelper implements IDescricaoDAO{
	private static final int VERSAO = 1;
	private static final String TABELA = "descricao";
	private static final String DATABASE = "portablevision";
	private static final String COLS[] = {"descricaoAtiva"};
	
	public DescricaoDAO(Context context){
		super(context, DATABASE, null, VERSAO);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "CREATE TABLE " + TABELA + " (descricaoAtiva TINYINT DEFAULT 0);";
	db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String sql = "DROP TABLE IF EXISTS " + TABELA;
		db.execSQL(sql);
		onCreate(db);
	}
	
	public void cadastrarAtivo(){
		ContentValues values = new ContentValues();
		values.put("descricaoAtiva", 0);
		getWritableDatabase().insert(TABELA, null, values);
	}
	
	public int alterarAtivo(){
		ContentValues values = new ContentValues();
		values.put("descricaoAtiva", 1);
		return getWritableDatabase().update(TABELA, values, null, null);
	}
	
	public int consultarAtivo(){
		Cursor c = null;
		
		int retorno = 0;
		
		try{
			c = getReadableDatabase().query(TABELA, COLS, "descricaoAtiva=1", null, null, null, null);
			
			if(c.moveToFirst()){
				retorno = c.getInt(0);
			}
			
			c.close();
		}
		catch(SQLiteException e){
			Log.e("Erro", e.getMessage());
		}
		catch(Exception e){
			Log.e("Erro", e.getMessage());
		}
		
		c.close();
		
		return retorno;
	}
}
