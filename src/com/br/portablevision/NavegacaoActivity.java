package com.br.portablevision;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import com.br.portablevision.classes.Endereco;
import com.br.portablevision.persistente.EnderecoDAO;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.sqlite.SQLiteException;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NavegacaoActivity extends Activity implements OnCompletionListener{
	private TextToSpeech tts;
	private SpeechRecognizer sr;
	
	private int falarOnResume = 0;
	private boolean inicio = false;
	private boolean localNaoEncontrado = false;
	private boolean faleNomeLocal = false;
	
	private String tipoNavegacao;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.navegacao);
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		final EditText txtNomeLocal = (EditText)findViewById(R.id.txtNomeLocal);
		
		final Button btnIniciar = (Button)findViewById(R.id.btIniciar);
		btnIniciar.setEnabled(false);
		
		Intent intent = getIntent();
		tipoNavegacao = intent.getStringExtra("navegacao");
		
		if(tipoNavegacao.equals("voz")){
			String nomeLocal = intent.getStringExtra("nomeLocal");
			if(nomeLocal != null){
				Toast.makeText(NavegacaoActivity.this, "Local para navegação: " + nomeLocal, Toast.LENGTH_SHORT).show();
			}
			else{
				MediaPlayer mp = new MediaPlayer();
				AssetFileDescriptor asset;
				try {
					asset = getAssets().openFd("iniciar_navegacao.mp3");
					mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
					asset.close();
					mp.prepare();
					mp.start();
					
					mp.setOnCompletionListener(NavegacaoActivity.this);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				Button btVoltar = (Button) findViewById(R.id.btVoltar);
				Button btProcurar = (Button) findViewById(R.id.btProcurar);
				Button btIniciar = (Button) findViewById(R.id.btIniciar);
				
				btIniciar.setClickable(false);
				btProcurar.setClickable(false);
				btVoltar.setClickable(false);
				
				txtNomeLocal.setEnabled(false);
			}
			
			sr = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
	        MyRecognition listener = new MyRecognition();
	        sr.setRecognitionListener(listener);
			
	        final Locale locale = new Locale("pt", "BR");
	        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
				
				public void onInit(int status) {
					if(status != TextToSpeech.ERROR){
						tts.setLanguage(locale);
					}
				}
			});
		}
		else{
			String nomeLocal = intent.getStringExtra("nomeLocal");
			if(nomeLocal != null){
				txtNomeLocal.setText(nomeLocal);
				
				Button btVoltar = (Button) findViewById(R.id.btVoltar);
				btVoltar.setOnClickListener(new OnClickListener() {
					
					public void onClick(View v) {
						finalizaActivity(NavegacaoActivity.this);
					}
				});
				
				Button btProcurar = (Button)findViewById(R.id.btProcurar);
				btProcurar.setOnClickListener(new OnClickListener() {
					
					public void onClick(View v) {
						if(!txtNomeLocal.getText().toString().equals("")){
							EnderecoDAO dao = new EnderecoDAO(NavegacaoActivity.this);
							
							try{
								Endereco endereco = dao.getNomeLocal(txtNomeLocal.getText().toString().toLowerCase());
							
								if(endereco != null && endereco.getEndereco() != null){
									Toast.makeText(NavegacaoActivity.this, "Nome do local encontrado!", Toast.LENGTH_LONG).show();
								}
								else{
									Toast.makeText(NavegacaoActivity.this, "Não existe este local", Toast.LENGTH_LONG).show();
								}
							}
							catch(SQLiteException e){
								Toast.makeText(NavegacaoActivity.this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
								txtNomeLocal.setText("");
								txtNomeLocal.requestFocus();
							}
							catch(Exception e){
								Toast.makeText(NavegacaoActivity.this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
								txtNomeLocal.setText("");
								txtNomeLocal.requestFocus();
							}
						}
						else{
							txtNomeLocal.requestFocus();
							Toast.makeText(NavegacaoActivity.this, "Digite o nome do local", Toast.LENGTH_LONG).show();
						}
					}
				});
				
				Button btIniciar = (Button) findViewById(R.id.btIniciar);
				btIniciar.setEnabled(true);
				btIniciar.setOnClickListener(new OnClickListener() {
					
					public void onClick(View v) {
						if(!txtNomeLocal.getText().toString().equals("")){
							EnderecoDAO dao = new EnderecoDAO(NavegacaoActivity.this);
							Endereco endereco = dao.getNomeLocal(txtNomeLocal.getText().toString().toLowerCase());
							
							String txtEndereco = endereco.getEndereco().toString();
							String txtCep = endereco.getCep().toString();
							
							Intent intent = new Intent(NavegacaoActivity.this, GpsActivity.class);
							
							if(!txtEndereco.equals("") && !txtCep.equals("")){
								intent.putExtra("endereco", endereco.getEndereco());
								startActivity(intent);
							}
							else if(!txtEndereco.equals("") && txtCep.equals("")){
								intent.putExtra("endereco", endereco.getEndereco());
								startActivity(intent);
							}
							else if(txtEndereco.equals("") && !txtCep.equals("")){
								intent.putExtra("endereco", endereco.getCep());
								startActivity(intent);
							}
							else{
								Toast.makeText(NavegacaoActivity.this, "Endereço inválido!", Toast.LENGTH_SHORT).show();
							}
						}
						else{
							Toast.makeText(NavegacaoActivity.this, "Digite o nome do local e depois pressione o botão procurar", Toast.LENGTH_LONG).show();
						}
					}
				});
			}
			else{
				Button btVoltar = (Button) findViewById(R.id.btVoltar);
				btVoltar.setOnClickListener(new OnClickListener() {
					
					public void onClick(View v) {
						finalizaActivity(NavegacaoActivity.this);
					}
				});
				
				Button btProcurar = (Button)findViewById(R.id.btProcurar);
				btProcurar.setOnClickListener(new OnClickListener() {
					
					public void onClick(View v) {
						if(!txtNomeLocal.getText().toString().equals("")){
							EnderecoDAO dao = new EnderecoDAO(NavegacaoActivity.this);
							
							try{
								Endereco endereco = dao.getNomeLocal(txtNomeLocal.getText().toString().toLowerCase());
							
								if(endereco != null && endereco.getEndereco() != null){
									Toast.makeText(NavegacaoActivity.this, "Nome do local encontrado!", Toast.LENGTH_LONG).show();
									btnIniciar.setEnabled(true);
								}
								else{
									Toast.makeText(NavegacaoActivity.this, "Não existe este local", Toast.LENGTH_LONG).show();
								}
							}
							catch(SQLiteException e){
								Toast.makeText(NavegacaoActivity.this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
								txtNomeLocal.setText("");
								txtNomeLocal.requestFocus();
							}
							catch(Exception e){
								Toast.makeText(NavegacaoActivity.this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
								txtNomeLocal.setText("");
								txtNomeLocal.requestFocus();
							}
						}
						else{
							txtNomeLocal.requestFocus();
							Toast.makeText(NavegacaoActivity.this, "Digite o nome do local", Toast.LENGTH_LONG).show();
						}
					}
				});
				
				Button btIniciar = (Button) findViewById(R.id.btIniciar);
				btIniciar.setOnClickListener(new OnClickListener() {
					
					public void onClick(View v) {
						if(!txtNomeLocal.getText().toString().equals("")){
							EnderecoDAO dao = new EnderecoDAO(NavegacaoActivity.this);
							Endereco endereco = dao.getNomeLocal(txtNomeLocal.getText().toString().toLowerCase());
							
							String txtEndereco = endereco.getEndereco().toString();
							String txtCep = endereco.getCep().toString();
							
							Intent intent = new Intent(NavegacaoActivity.this, GpsActivity.class);
							
							if(!txtEndereco.equals("") && !txtCep.equals("")){
								intent.putExtra("endereco", endereco.getEndereco());
								startActivity(intent);
							}
							else if(!txtEndereco.equals("") && txtCep.equals("")){
								intent.putExtra("endereco", endereco.getEndereco());
								startActivity(intent);
							}
							else if(txtEndereco.equals("") && !txtCep.equals("")){
								intent.putExtra("endereco", endereco.getCep());
								startActivity(intent);
							}
							else{
								Toast.makeText(NavegacaoActivity.this, "Endereço inválido!", Toast.LENGTH_SHORT).show();
							}
						}
						else{
							Toast.makeText(NavegacaoActivity.this, "Digite o nome do local e depois pressione o botão procurar", Toast.LENGTH_LONG).show();
						}
					}
				});
			}
		}
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
					
					mp.setOnCompletionListener(NavegacaoActivity.this);
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
					
					mp.setOnCompletionListener(NavegacaoActivity.this);
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
					
					mp.setOnCompletionListener(NavegacaoActivity.this);
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
					
					mp.setOnCompletionListener(NavegacaoActivity.this);
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
					
					mp.setOnCompletionListener(NavegacaoActivity.this);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else{
				MediaPlayer mp = new MediaPlayer();
				AssetFileDescriptor asset;
				try {
					asset = getAssets().openFd("fale_novamente.mp3");
					mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
					asset.close();
					mp.prepare();
					mp.start();
					
					mp.setOnCompletionListener(NavegacaoActivity.this);
				} catch (IOException e) {
					e.printStackTrace();
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
            
            EditText txtNomeLocal = (EditText)findViewById(R.id.txtNomeLocal);
			
			EnderecoDAO dao = new EnderecoDAO(NavegacaoActivity.this);
			
			if(strlist != null && strlist.get(0) != "" && !strlist.contains("iniciar") && !strlist.contains("corrigir") && !strlist.contains("voltar")){
				try{
					Endereco endereco = dao.getNomeLocal(strlist.get(0));
				
					if(endereco != null && endereco.getEndereco() != null){
						txtNomeLocal.setText(endereco.getNome());
						onResume();
					}
					else{
						localNaoEncontrado = true;
						falarOnResume = 0;
						onResume();
					}
				}
				catch(Exception e){
					Toast.makeText(NavegacaoActivity.this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
				}
				finally{
					dao.close();
				}
			}
			else if(strlist.contains("corrigir")){
				txtNomeLocal.setText("");
				inicio = true;
				falarOnResume = 0;
				onResume();
			}
			else if(strlist.contains("iniciar")){
				if(!txtNomeLocal.getText().toString().equals("")){
					try{
						Endereco endereco = dao.getNomeLocal(txtNomeLocal.getText().toString());
						
						if(endereco != null){
							Intent intent = new Intent(NavegacaoActivity.this, GpsActivity.class);
							
							String txtEndereco = endereco.getEndereco().toString();
							String txtCep = endereco.getCep().toString();
							
							if(!txtEndereco.equals("") && !txtCep.equals("")){
								intent.putExtra("endereco", endereco.getEndereco());
								startActivity(intent);
							}
							else if(!txtEndereco.equals("") && txtCep.equals("")){
								intent.putExtra("endereco", endereco.getEndereco());
								startActivity(intent);
							}
							else if(txtEndereco.equals("") && !txtCep.equals("")){
								intent.putExtra("endereco", endereco.getCep());
								startActivity(intent);
							}
						}
					}
					catch(Exception e){
						Toast.makeText(NavegacaoActivity.this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
					}
					finally{
						dao.close();
					}
				}
			}
			else if(strlist.contains("voltar")){
				finalizaActivity(NavegacaoActivity.this);
			}
		}

		public void onRmsChanged(float rmsdB) {
			
		}
	}
	
	public void onResume(){
		super.onResume();
		
		falarOnResume++;
		
		Log.d("onResume", "onResume");
		
		EditText txtEndereco = (EditText)findViewById(R.id.txtNomeLocal);
		
		if(tipoNavegacao.equals("voz")){
			if(!txtEndereco.getText().toString().equals("") && falarOnResume == 2){
				tts.speak(txtEndereco.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
			}
		}
		
		if(tipoNavegacao.equals("voz") && falarOnResume > 1){
			if(!txtEndereco.getText().toString().equals("")){
				MediaPlayer mp = new MediaPlayer();
				AssetFileDescriptor asset;
				try {
					asset = getAssets().openFd("fale_agora.mp3");
					mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
					asset.close();
					mp.prepare();
					mp.start();
					
					mp.setOnCompletionListener(NavegacaoActivity.this);
				} catch (IOException e) {
					e.printStackTrace();
				}		
			}
		}
		else if(inicio){
			MediaPlayer mp = new MediaPlayer();
			AssetFileDescriptor asset;
			try {
				asset = getAssets().openFd("iniciar_navegacao.mp3");
				mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
				asset.close();
				mp.prepare();
				mp.start();
				
				mp.setOnCompletionListener(NavegacaoActivity.this);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			inicio = false;
		}
		else if(localNaoEncontrado){
			MediaPlayer mp = new MediaPlayer();
			AssetFileDescriptor asset;
			try {
				asset = getAssets().openFd("local_inexistente.mp3");
				mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
				asset.close();
				mp.prepare();
				mp.start();
				
				mp.setOnCompletionListener(new OnCompletionListener() {
					
					public void onCompletion(MediaPlayer mp) {
						localNaoEncontrado = false;
						faleNomeLocal = true;
						falarOnResume = 0;
						onResume();
					}
				});
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if(faleNomeLocal){
			MediaPlayer mp = new MediaPlayer();
			AssetFileDescriptor asset;
			try {
				asset = getAssets().openFd("iniciar_navegacao.mp3");
				mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
				asset.close();
				mp.prepare();
				mp.start();
				
				mp.setOnCompletionListener(new OnCompletionListener() {
					
					public void onCompletion(MediaPlayer mp) {
						faleNomeLocal = false;
						reconheceVoz(mp);
					}
				});
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void onDestroy(){
		if(tts != null){
			tts.stop();
			tts.shutdown();
		}
		super.onDestroy();
	}
	
	public void reconheceVoz(final MediaPlayer mp){
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "com.br.portablevision");
		intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
		sr.startListening(intent);
		
		mp.release();
    }
    
    public void finalizaActivity(Activity ac){
    	ac.finish();
    }

	public void onCompletion(MediaPlayer mp) {
		reconheceVoz(mp);
	}
}
