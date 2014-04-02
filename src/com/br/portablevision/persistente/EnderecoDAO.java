package com.br.portablevision.persistente;

import java.util.ArrayList;
import java.util.List;

import com.br.portablevision.classes.Endereco;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class EnderecoDAO extends SQLiteOpenHelper implements IEnderecoDAO{
	private static final int VERSAO = 1;
	private static final String TABELA = "endereco";
	private static final String DATABASE = "portablevision";
	private static final String COLS[] = {"id", "nome", "endereco", "cep"};
	
	public EnderecoDAO(Context context) {
		super(context, DATABASE, null, VERSAO);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "CREATE TABLE " + TABELA + " (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
					"nome TEXT, endereco TEXT, cep TEXT);";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String sql = "DROP TABLE IF EXISTS " + TABELA;
		db.execSQL(sql);
		onCreate(db);
	}
	
	public void insere(Endereco endereco){
		ContentValues values = new ContentValues();
		values.put("endereco", endereco.getEndereco());
		values.put("nome", endereco.getNome());
		values.put("cep", endereco.getCep());
		getWritableDatabase().insert(TABELA, null, values);
	}
	
	public int alterar(String nomeLocal, String endereco, String cep, int id){
		ContentValues values = new ContentValues();
		values.put("nome", nomeLocal);
		values.put("endereco", endereco);
		values.put("cep", cep);
		
		return getWritableDatabase().update(TABELA, values, "id=?", new String[]{String.valueOf(id)});
	}
	
	public int alterarById(Endereco endereco, int id){
		ContentValues values = new ContentValues();
		values.put("nome", endereco.getNome());
		values.put("endereco", endereco.getEndereco());
		values.put("cep", endereco.getCep());
		
		return getWritableDatabase().update(TABELA, values, "id=?", new String[]{String.valueOf(id)});
	}
	
	public boolean excluir(int id){
		return getWritableDatabase().delete(TABELA, "id=" + id, null) > 0;
	}
	
	public Endereco getEnderecoById(Integer id){
		Cursor c = null;
		
		Endereco endereco = new Endereco();
		
		try{
			c = getReadableDatabase().query(TABELA, COLS, "id=?", new String[]{id.toString()}, null, null, null);
			c.moveToFirst();
			
			endereco.setId(c.getInt(0));
			endereco.setNome(c.getString(1));
			endereco.setEndereco(c.getString(2));
			endereco.setCep(c.getString(3));
		}
		catch(SQLiteException e){
			Log.i("Erro", e.getMessage());
		}
		catch(Exception e){
			Log.i("Erro", e.getMessage());
		}
		finally{
			c.close();
		}
		
		return endereco;
	}
	
	public Endereco getEndereco(String strEndereco){
		Cursor c = null;
		
		Endereco endereco = new Endereco();
		
		try{
			c = getReadableDatabase().query(TABELA, COLS, "endereco=?", new String[]{strEndereco.toString()}, null, null, null);
			c.moveToFirst();
			
			endereco.setId(c.getInt(0));
			endereco.setNome(c.getString(1));
			endereco.setEndereco(c.getString(2));
			endereco.setCep(c.getString(3));
		}
		catch(SQLiteException e){
			e.printStackTrace();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			c.close();
		}
		
		return endereco;
	}
	
	public List<Endereco> getEnderecos(){
		List<Endereco> enderecos = new ArrayList<Endereco>();
		
		Cursor c = null;
		try{
			c = getReadableDatabase().query(TABELA, COLS, null, null, null, null, "nome");
			
			while(c.moveToNext()){
				Endereco endereco = new Endereco();
				endereco.setId(c.getInt(0));
				endereco.setNome(c.getString(1));
				endereco.setEndereco(c.getString(2));
				endereco.setCep(c.getString(3));
				
				enderecos.add(endereco);
			}
		}
		catch(SQLiteException e){
			Log.i("Erro", e.getMessage());
		}
		catch(Exception e){
			Log.i("Erro", e.getMessage());
		}
		finally{
			c.close();
		}
		
		return enderecos;
	}
	
	public Endereco getNomeLocal(String local){
		Cursor c = null;
		
		Endereco endereco = new Endereco();
		
		try{
			c = getReadableDatabase().query(TABELA, COLS, "nome=?", new String[]{local.toString()}, null, null, null);
			if(c != null && c.moveToFirst()){
				c.moveToFirst();
				
				endereco.setId(c.getInt(0));
				endereco.setNome(c.getString(1));
				endereco.setEndereco(c.getString(2));
				endereco.setCep(c.getString(3));
			}
			else{
				endereco = null;
			}
		}
		catch(SQLiteException e){
			e.printStackTrace();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			c.close();
		}
		
		return endereco;
	}
	
	public Endereco getNomeEndereco(String strLocal, String strEndereco){
		Cursor c = null;
		
		Endereco endereco = new Endereco();
		
		try{
			c = getReadableDatabase().query(TABELA, COLS, "nome=?, endereco=?", new String[]{strLocal.toString(), strEndereco.toString()}, null, null, null);
			c.moveToFirst();
			
			endereco.setId(c.getInt(0));
			endereco.setNome(c.getString(1));
			endereco.setEndereco(c.getString(2));
			endereco.setCep(c.getString(3));
		}
		catch(SQLiteException e){
			e.printStackTrace();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			c.close();
		}
		
		return endereco;
	}
}
