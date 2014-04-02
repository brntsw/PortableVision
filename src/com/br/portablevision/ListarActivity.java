package com.br.portablevision;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.br.portablevision.classes.Endereco;
import com.br.portablevision.persistente.EnderecoDAO;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ListarActivity extends Activity implements OnCreateContextMenuListener, OnInitListener, OnUtteranceCompletedListener, OnCompletionListener{
	private ListView listaEnderecos;
	private List<Endereco> enderecos;
	private Endereco endereco;
	private String tipoNavegacao;
	
	private TextToSpeech tts = null;
	private static final int check = 123;
	
	private SpeechRecognizer sr;
	
	private Runnable runnable;
	
	private boolean possuiLocal = false;
	private boolean naoPossuiLocal = false;
	private Endereco localFalado;
	
	private boolean fezExclusao = false;
	private boolean naoFezExclusao = false;
	
	private boolean falaIncorreta = false;
	
	private boolean encerrarQuandoVoltarDeOutraActivity = false;
	
	public void onCreate(Bundle bundle){
		super.onCreate(bundle);
		setContentView(R.layout.lista_locais);
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		listaEnderecos = (ListView) findViewById(R.id.lista);
		
		Intent intent = getIntent();
		tipoNavegacao = intent.getStringExtra("navegacao");
		
		if(tipoNavegacao.equals("manual")){
			registerForContextMenu(listaEnderecos);
		}
		else if(tipoNavegacao.equals("voz")){
			if(!encerrarQuandoVoltarDeOutraActivity){
				//Inicia o SpeechRecognizer
				sr = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
		        MyRecognition listener = new MyRecognition();
		        sr.setRecognitionListener(listener);
				
				//Cria uma intent para enviar ao startActivityForResult
				Intent checkIntent = new Intent();
				checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
				startActivityForResult(checkIntent, check);
			}
		}
	}
	
	public void onResume(){
		Log.d("onResume", "onResume");
		carregaLista();
		if(possuiLocal){
			MediaPlayer mp = new MediaPlayer();
			AssetFileDescriptor asset;
			try {
				asset = getAssets().openFd("alterar_deletar_navegar.mp3");
				mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
				asset.close();
				mp.prepare();
				mp.start();
			
				mp.setOnCompletionListener(ListarActivity.this);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			possuiLocal = false;
		}
		else if(naoPossuiLocal){
			MediaPlayer mp = new MediaPlayer();
			AssetFileDescriptor asset;
			try {
				asset = getAssets().openFd("local_inexistente.mp3");
				mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
				asset.close();
				mp.prepare();
				mp.start();
			
				mp.setOnCompletionListener(ListarActivity.this);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			naoPossuiLocal = false;
		}
		else if(falaIncorreta){
			MediaPlayer mp = new MediaPlayer();
			AssetFileDescriptor asset;
			try {
				asset = getAssets().openFd("fale_novamente.mp3");
				mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
				asset.close();
				mp.prepare();
				mp.start();
			
				mp.setOnCompletionListener(ListarActivity.this);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			falaIncorreta = false;
		}
		else if(fezExclusao){
			MediaPlayer mp = new MediaPlayer();
			AssetFileDescriptor asset;
			try {
				asset = getAssets().openFd("exclusao_sucesso.mp3");
				mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
				asset.close();
				mp.prepare();
				mp.start();
			
				mp.setOnCompletionListener(new OnCompletionListener() {
					
					public void onCompletion(MediaPlayer mp) {
						finish();	
					}
				});
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			fezExclusao = false;
		}
		else if(naoFezExclusao){
			MediaPlayer mp = new MediaPlayer();
			AssetFileDescriptor asset;
			try {
				asset = getAssets().openFd("exclusao_nao_realizada.mp3");
				mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
				asset.close();
				mp.prepare();
				mp.start();
			
				mp.setOnCompletionListener(ListarActivity.this);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			naoFezExclusao = false;
		}
		else if(encerrarQuandoVoltarDeOutraActivity){
			sr.stopListening();
			sr.cancel();
			finish();
		}
		super.onResume();
	}
	
	public void onRestart(){
		Log.d("OnRestart", "OnRestart");
		super.onRestart();
	}
	
	public void onDestroy(){
		if(tts != null){
			tts.stop();
			tts.shutdown();
		}
		
		if(sr != null){
			sr.stopListening();
			sr.cancel();
		}
		super.onDestroy();
		
		encerrarQuandoVoltarDeOutraActivity = true;
	}
	
	class MyRecognition implements RecognitionListener{

		public void onBeginningOfSpeech() {
			
		}

		public void onBufferReceived(byte[] buffer) {
			
		}

		public void onEndOfSpeech() {
			
		}

		public void onError(int error) {
			if(error == 1){
				MediaPlayer mp = new MediaPlayer();
				AssetFileDescriptor asset;
				try {
					asset = getAssets().openFd("erro_rede.mp3");
					mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
					asset.close();
					mp.prepare();
					mp.start();
					
					mp.setOnCompletionListener(ListarActivity.this);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else if(error == 2){
				MediaPlayer mp = new MediaPlayer();
				AssetFileDescriptor asset;
				try {
					asset = getAssets().openFd("erro_rede.mp3");
					mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
					asset.close();
					mp.prepare();
					mp.start();
					
					mp.setOnCompletionListener(ListarActivity.this);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else if(error == 3){
				MediaPlayer mp = new MediaPlayer();
				AssetFileDescriptor asset;
				try {
					asset = getAssets().openFd("erro_audio.mp3");
					mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
					asset.close();
					mp.prepare();
					mp.start();
					
					mp.setOnCompletionListener(ListarActivity.this);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else if(error == 4){
				MediaPlayer mp = new MediaPlayer();
				AssetFileDescriptor asset;
				try {
					asset = getAssets().openFd("erro_servidor.mp3");
					mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
					asset.close();
					mp.prepare();
					mp.start();
					
					mp.setOnCompletionListener(ListarActivity.this);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else if(error == 7){
				MediaPlayer mp = new MediaPlayer();
				AssetFileDescriptor asset;
				try {
					asset = getAssets().openFd("erro_resultado.mp3");
					mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
					asset.close();
					mp.prepare();
					mp.start();
					
					mp.setOnCompletionListener(ListarActivity.this);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else{
				if(!encerrarQuandoVoltarDeOutraActivity){
					Toast.makeText(ListarActivity.this, "Listar: error", Toast.LENGTH_SHORT).show();
					MediaPlayer mp = new MediaPlayer();
					AssetFileDescriptor asset;
					try {
						asset = getAssets().openFd("fale_novamente.mp3");
						mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
						asset.close();
						mp.prepare();
						mp.start();
						
						mp.setOnCompletionListener(ListarActivity.this);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				else{
					sr.stopListening();
					sr.cancel();
				}
			}
		}

		public void onEvent(int eventType, Bundle params) {
			
		}

		public void onPartialResults(Bundle partialResults) {
			
		}

		public void onReadyForSpeech(Bundle params) {
			
		}

		public void onResults(Bundle results) {
			ArrayList<String> strlist = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
			
			if(strlist != null && !strlist.contains("voltar") && !strlist.contains("alterar") && !strlist.contains("alterar local") && !strlist.contains("excluir") && !strlist.contains("excluir local") && !strlist.contains("navegar") && !strlist.contains("navegação")){
				boolean possui = false;
				for(int i = 0; i < listaEnderecos.getCount(); i++){
					if(strlist.get(0).equals(listaEnderecos.getItemAtPosition(i).toString())){
						possui = true;
					}
				}
				
				EnderecoDAO dao = new EnderecoDAO(ListarActivity.this);
				
				if(possui){
					Endereco endereco = dao.getNomeLocal(strlist.get(0));
					
					localFalado = endereco;
					possuiLocal = true;
					
					onResume();
				}
				else{
					naoPossuiLocal = true;
					onResume();
				}
			}
			else if(strlist.contains("alterar") || strlist.contains("alterar local")){
				if(localFalado != null){
					encerrarQuandoVoltarDeOutraActivity = true;
					Intent intent = new Intent(ListarActivity.this, AlterarActivity.class);
					intent.putExtra("nomeLocal", localFalado.getNome());
					intent.putExtra("navegacao", "voz");
					startActivity(intent);
				}
			}
			else if(strlist.contains("excluir") || strlist.contains("excluir local")){
				if(localFalado != null){
					EnderecoDAO dao = new EnderecoDAO(ListarActivity.this);
					boolean excluiu = dao.excluir(localFalado.getId());
					if(excluiu){
						fezExclusao = true;
						onResume();
					}
					else{
						naoFezExclusao = true;
						onResume();
					}
				}
			}
			else if(strlist.contains("navegar") || strlist.contains("navegação")){
				if(localFalado != null){
					encerrarQuandoVoltarDeOutraActivity = true;
					Intent intent = new Intent(ListarActivity.this, NavegacaoActivity.class);
					intent.putExtra("nomeLocal", localFalado.getNome());
					intent.putExtra("navegacao", "voz");
					startActivity(intent);
				}
			}
			else if(strlist.contains("voltar")){
				finish();
			}
			else{
				falaIncorreta = true;
				onResume();
			}
		}

		public void onRmsChanged(float rmsdB) {
			
		}
	}
	
	private void carregaLista(){
		EnderecoDAO dao = new EnderecoDAO(ListarActivity.this);
		try{
			enderecos = dao.getEnderecos();
			dao.close();
		
			ArrayAdapter<Endereco> adapter = new ArrayAdapter<Endereco>(this, android.R.layout.simple_list_item_1, enderecos);
			listaEnderecos.setAdapter(adapter);
			
			if(tipoNavegacao.equals("manual")){
				if(enderecos.size() == 0){
					Toast.makeText(ListarActivity.this, "Não há nenhum local cadastrado!", Toast.LENGTH_LONG).show();
				}
				else{
					Toast.makeText(ListarActivity.this, "Quantidade de locais cadastrados: " + enderecos.size(), Toast.LENGTH_LONG).show();
				}
				
				listaEnderecos.setOnItemLongClickListener(new OnItemLongClickListener() {

					public boolean onItemLongClick(AdapterView<?> adapter,
							View view, int posicao, long id) {
						endereco = (Endereco) adapter.getItemAtPosition(posicao);
						registerForContextMenu(listaEnderecos);
						return false;
					}
				});
			}
		}
		catch(Exception e){
			Toast.makeText(ListarActivity.this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
	
	//Método para o context menu
	public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo){
		super.onCreateContextMenu(menu, view, menuInfo);
		
		MenuItem alterar = menu.add(0, 0, 0, "Alterar");
		alterar.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			public boolean onMenuItemClick(MenuItem item) {
				Intent intent = new Intent(ListarActivity.this, AlterarActivity.class);
				intent.putExtra("navegacao", "manual");
				intent.putExtra("nomeLocal", endereco.getNome());
				startActivity(intent);
				return false;
			}
		});
		
		MenuItem deletar = menu.add(0, 1, 0, "Deletar");
		deletar.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			public boolean onMenuItemClick(MenuItem item) {
				EnderecoDAO dao = new EnderecoDAO(ListarActivity.this);
				boolean excluiu = dao.excluir(endereco.getId());
				if(excluiu){
					Toast.makeText(ListarActivity.this, "Local excluído com sucesso", Toast.LENGTH_SHORT).show();
				}
				else{
					Toast.makeText(ListarActivity.this, "O local não foi excluído", Toast.LENGTH_SHORT).show();
				}
				
				onResume();
				return false;
			}
		});
		
		MenuItem navegar = menu.add(0, 2, 0, "Navegar");
		navegar.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			public boolean onMenuItemClick(MenuItem item) {
				Intent intent = new Intent(ListarActivity.this, NavegacaoActivity.class);
				intent.putExtra("navegacao", "manual");
				intent.putExtra("nomeLocal", endereco.getNome());
				startActivity(intent);
				return false;
			}
		});
	}

	public void onInit(int status) {
		if(status == TextToSpeech.SUCCESS){	
			final Handler h = new Handler();
	        runnable = new Runnable(){
	        	int valor = 1;
				public void run() {
					if(valor <= listaEnderecos.getCount()){
						if(valor == listaEnderecos.getCount()){
							falarUltimo(listaEnderecos.getItemAtPosition(valor - 1).toString());
							h.postDelayed(runnable, 2500);
							callUterrance();
						}
						else{
							falar(listaEnderecos.getItemAtPosition(valor - 1).toString());
							h.postDelayed(runnable, 2500);
						}
					}
					
					valor++;
				}
	        	
	        };
	        
	        h.postDelayed(runnable, 2500);
		}
	}
	
	public void callUterrance(){
		tts.setOnUtteranceCompletedListener(this);
	}
	
	public void onUtteranceCompleted(String utteranceId){
		Log.d("UtteranceID", utteranceId);
		try{
			if(utteranceId.equals("ok")){
				MediaPlayer mp = new MediaPlayer();
				AssetFileDescriptor asset;
				try {
					asset = getAssets().openFd("fale_agora.mp3");
					mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
					asset.close();
					mp.prepare();
					mp.start();
					
					mp.setOnCompletionListener(ListarActivity.this);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		catch(Exception e){
			Log.e("onUtterance", "Erro: " + e.getMessage());
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		if(requestCode == check){
			if(resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS){
				final Locale locale = new Locale("pt", "BR");
				tts = new TextToSpeech(this, this);
				tts.setLanguage(locale);
			}
			else{
				//Faltando dados, instala
				Intent installIntent = new Intent();
				installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
				startActivity(installIntent);
			}
		}
	}
	
	public void falar(String texto){		
		tts.speak(texto, TextToSpeech.QUEUE_FLUSH, null);
	}
	
	public void falarUltimo(String texto){
		HashMap<String, String> hash = new HashMap<String, String>();
		hash.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "ok");
		
		tts.speak(texto, TextToSpeech.QUEUE_FLUSH, hash);
	}
	
	public void reconheceVoz(MediaPlayer mp){
		try{
			Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
			intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
			intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "com.br.portablevision");
			intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
			sr.startListening(intent);
		
			mp.release();
		}
		catch(Exception e){
			Toast.makeText(ListarActivity.this, "Erro: " + e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	public void onCompletion(MediaPlayer mp) {
		reconheceVoz(mp);
	}
}
