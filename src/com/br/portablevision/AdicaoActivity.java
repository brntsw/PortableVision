package com.br.portablevision;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import com.br.portablevision.classes.Endereco;
import com.br.portablevision.classes.Validacao;
import com.br.portablevision.persistente.EnderecoDAO;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AdicaoActivity extends Activity implements OnCompletionListener{
	private Endereco endereco = new Endereco();
	
	private EditText txtNomeLocal;
	private EditText txtEndereco;
	private EditText txtCep;
	
	private int falarOnResume = 0;
	private int numCampo = 0;
	
	private TextToSpeech tts = null;
	
	private SpeechRecognizer sr;
	
	private String tipoNavegacao;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.adicao);
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		buscaComponentes();
		txtNomeLocal.requestFocus();
		
		Intent intent = getIntent();
		tipoNavegacao = intent.getStringExtra("navegacao");
		
		if(tipoNavegacao.equals("voz")){
			final Locale locale = new Locale("pt", "BR");
			tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
				
				public void onInit(int status) {
					if(status != TextToSpeech.ERROR){
						tts.setLanguage(locale);
						tts.setOnUtteranceCompletedListener(new OnUtteranceCompletedListener(){
	
							public void onUtteranceCompleted(String utteranceId) {
								Toast.makeText(AdicaoActivity.this, "Terminou TTS", Toast.LENGTH_LONG).show();
								Log.i("TAG", utteranceId);
							}
							
						});
					}
				}
			});
			
			sr = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
	        MyRecognition listener = new MyRecognition();
	        sr.setRecognitionListener(listener);
			
			MediaPlayer mp = new MediaPlayer();
			AssetFileDescriptor asset;
			try {
				asset = getAssets().openFd("fale_nome_local.mp3");
				mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
				asset.close();
				mp.prepare();
				mp.start();
				
				mp.setOnCompletionListener(AdicaoActivity.this);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			Button btnVoltar = (Button) findViewById(R.id.btVoltar);
			Button btnAdicionar = (Button) findViewById(R.id.btAdicionar);
			
			btnVoltar.setClickable(false);
			btnAdicionar.setClickable(false);
			
			txtNomeLocal.setEnabled(false);
			txtEndereco.setEnabled(false);
			txtCep.setEnabled(false);
		}
		else{
			Button btnVoltar = (Button) findViewById(R.id.btVoltar);
			Button btnAdicionar = (Button) findViewById(R.id.btAdicionar);
			
			btnVoltar.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					finalizaActivity(AdicaoActivity.this);
				}
			});
			
			btnAdicionar.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					String valueNome = txtNomeLocal.getText().toString();
					String valueEndereco = txtEndereco.getText().toString();
					String valueCep = txtCep.getText().toString();
					
					Validacao valida = new Validacao();
					boolean cepCorreto = valida.validaCEP(valueCep);
					
					if(!valueNome.equals("") && (!valueEndereco.equals("") || !valueCep.equals(""))){
						EnderecoDAO dao = new EnderecoDAO(AdicaoActivity.this);
						Endereco nomeLocal = dao.getNomeLocal(valueNome.toLowerCase());
						
						if(nomeLocal == null){
							if(!valueEndereco.equals("") && valueCep.equals("")){
								populaEndereco();
								
								if(endereco.getNome().toString() != "" && (endereco.getEndereco().toString() != "" || endereco.getCep().toString() != "")){
									try{
										dao = new EnderecoDAO(AdicaoActivity.this);
										dao.insere(endereco);
										dao.close();
										
										Toast.makeText(AdicaoActivity.this, "Local cadastrado com sucesso!", Toast.LENGTH_LONG).show();
										
										finish();
									}
									catch(Exception e){
										Toast.makeText(AdicaoActivity.this, "Ocorreu um erro, tente novamente", Toast.LENGTH_LONG).show();
									}
								}
								else{
									Toast.makeText(AdicaoActivity.this, "Vazio", Toast.LENGTH_LONG).show();
								}
							}
							else if(!valueCep.equals("") && valueEndereco.equals("")){
								if(!cepCorreto){
									Toast.makeText(AdicaoActivity.this, "CEP incorreto!", Toast.LENGTH_SHORT).show();
									txtCep.setText("");
									txtCep.requestFocus();
								}
								else{
									populaEndereco();
									
									if(endereco.getNome().toString() != "" && (endereco.getEndereco().toString() != "" || endereco.getCep().toString() != "")){
										try{
											dao = new EnderecoDAO(AdicaoActivity.this);
											dao.insere(endereco);
											dao.close();
											
											Toast.makeText(AdicaoActivity.this, "Local cadastrado com sucesso!", Toast.LENGTH_LONG).show();
											
											finish();
										}
										catch(Exception e){
											Toast.makeText(AdicaoActivity.this, "Ocorreu um erro, tente novamente", Toast.LENGTH_LONG).show();
										}
									}
									else{
										Toast.makeText(AdicaoActivity.this, "Vazio", Toast.LENGTH_LONG).show();
									}
								}
							}
							else{
								if(cepCorreto){
									populaEndereco();
									
									if(endereco.getNome().toString() != "" && endereco.getEndereco().toString() != "" && endereco.getCep().toString() != ""){
										try{
											dao = new EnderecoDAO(AdicaoActivity.this);
											dao.insere(endereco);
											dao.close();
											
											Toast.makeText(AdicaoActivity.this, "Local cadastrado com sucesso!", Toast.LENGTH_LONG).show();
											
											finish();
										}
										catch(Exception e){
											Toast.makeText(AdicaoActivity.this, "Ocorreu um erro, tente novamente", Toast.LENGTH_LONG).show();
										}
									}
									else{
										Toast.makeText(AdicaoActivity.this, "Vazio", Toast.LENGTH_LONG).show();
									}
								}
								else{
									Toast.makeText(AdicaoActivity.this, "CEP incorreto!", Toast.LENGTH_SHORT).show();
									txtCep.setText("");
									txtCep.requestFocus();
								}
							}
						}
						else{
							Toast.makeText(AdicaoActivity.this, "O local já existe!", Toast.LENGTH_SHORT).show();
						}
					}
					else{
						Toast.makeText(AdicaoActivity.this, "Preencha pelo menos o nome e endereço", Toast.LENGTH_LONG).show();
					}
				}
			});
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
					
					mp.setOnCompletionListener(AdicaoActivity.this);
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
					
					mp.setOnCompletionListener(AdicaoActivity.this);
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
					
					mp.setOnCompletionListener(AdicaoActivity.this);
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
					
					mp.setOnCompletionListener(AdicaoActivity.this);
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
					
					mp.setOnCompletionListener(AdicaoActivity.this);
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
					
					mp.setOnCompletionListener(AdicaoActivity.this);
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
			
			if((!strlist.contains("local correto") || !strlist.contains("local certo") || !strlist.contains("local incorreto") || !strlist.contains("local errado")) || (!strlist.contains("endereço correto") || !strlist.contains("endereço certo") || !strlist.contains("endereço incorreto") || !strlist.contains("endereço errado")) || (!strlist.contains("cep correto") || !strlist.contains("cep certo") || !strlist.contains("cep incorreto") || !strlist.contains("cep errado"))){
				numCampo++;
			}
			
			buscaComponentes();
			
			if(strlist != null && numCampo == 1 && !strlist.contains("voltar") && !strlist.contains("local correto") && !strlist.contains("local incorreto")){
				if(strlist.get(0) != ""){
					txtNomeLocal.setText(strlist.get(0));
					onResume();
				}
			}
			else if(results != null && numCampo == 3 && !strlist.contains("voltar") && !strlist.contains("local correto") && !strlist.contains("local incorreto")){
				if(strlist.get(0) != ""){
					txtEndereco.setText(strlist.get(0));
					onResume();
				}
			}
			else if(results != null && numCampo == 5 && !strlist.contains("voltar") && !strlist.contains("local correto") && !strlist.contains("local incorreto")){
				if(strlist.get(0) != ""){
					String cep = strlist.get(0).replace(" ", "");
					txtCep.setText(cep);
					onResume();
				}
			}
			else if(strlist.contains("local correto") || strlist.contains("local certo")){
				if(txtEndereco.hasFocus()){
					numCampo--;
					onResume();
				}
				txtEndereco.requestFocus();
				onResume();
			}
			else if(strlist.contains("local incorreto") || strlist.contains("local errado")){
				numCampo = 0;
				txtNomeLocal.setText("");
				txtNomeLocal.requestFocus();
				onResume();
			}
			else if(strlist.contains("endereço correto") || strlist.contains("endereço certo")){
				txtCep.requestFocus();
				onResume();
			}
			else if(strlist.contains("endereço incorreto") || strlist.contains("endereço errado")){
				numCampo = 2;
				txtEndereco.setText("");
				txtEndereco.requestFocus();
				onResume();
			}
			else if(strlist.contains("cep correto") || strlist.contains("cep certo")){
				onResume();
			}
			else if(strlist.contains("cep incorreto") || strlist.contains("cep errado")){
				numCampo = 4;
				txtCep.setText("");
				txtCep.requestFocus();
				onResume();
			}
			else if(strlist.contains("adicionar")){
				String valueNome = txtNomeLocal.getText().toString();
				String valueEndereco = txtEndereco.getText().toString();
				String valueCep = txtCep.getText().toString();
				
				if(valueNome != "" && valueEndereco != "" || valueCep != ""){
					populaEndereco();
					
					if(endereco.getNome().toString() != "" && endereco.getEndereco().toString() != ""){
						EnderecoDAO dao = new EnderecoDAO(AdicaoActivity.this);
						dao.insere(endereco);
						dao.close();
						
						onResume();
					}
					else{
						Toast.makeText(AdicaoActivity.this, "Vazio", Toast.LENGTH_LONG).show();
					}
				}
				else{
					Toast.makeText(AdicaoActivity.this, "Preencha pelo menos o nome e endereço", Toast.LENGTH_LONG).show();
				}
			}
			else if(strlist.contains("voltar")){
				finalizaActivity(AdicaoActivity.this);
				
				numCampo = 0;
				falarOnResume = 0;
			}
		}

		public void onRmsChanged(float rmsdB) {
			
		}
	}
	
	public void onUtteranceCompleted(String utteranceId){
		Log.i("TAG", utteranceId);
	}
	
	public void onRestart(){
		super.onRestart();
		
		Log.d("onRestart", "onRestart");
	}
	
	public void onResume(){
		super.onResume();
		
		Log.d("onResume", "onResume");
		
		if(tipoNavegacao.equals("voz")){
			if(falarOnResume <= 2){
				falarOnResume++;
			}
			
			Log.d("Valor falarOnResume", "" + falarOnResume);
			
			if(!txtNomeLocal.getText().toString().equals("") && numCampo == 1){
				Thread thread;
				thread = new Thread(){
					public void run(){
						try {
							Thread.sleep(3000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						tts.speak(txtNomeLocal.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
					}
				};
				
				thread.run();
			}
			else if(!txtEndereco.getText().toString().equals("") && numCampo == 3){
				tts.speak(txtEndereco.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
			}
			else if(!txtCep.getText().toString().equals("") && numCampo == 5){
				tts.speak(txtCep.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
			}
			
			if(falarOnResume > 1){
				if(numCampo == 1){
					MediaPlayer mp = new MediaPlayer();
					AssetFileDescriptor asset;
					try {
						asset = getAssets().openFd("fale_agora.mp3");
						mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
						asset.close();
						mp.prepare();
						mp.start();
					
						mp.setOnCompletionListener(AdicaoActivity.this);
					} catch (IOException e) {
						e.printStackTrace();
					}	
				}
				/*if(numCampo == 1){
					MediaPlayer mp = new MediaPlayer();
					AssetFileDescriptor asset;
					try {
						asset = getAssets().openFd("fale_nome_local.mp3");
						mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
						asset.close();
						mp.prepare();
						mp.start();
					
						mp.setOnCompletionListener(AdicaoActivity.this);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}*/
				else if(numCampo == 2){
					MediaPlayer mp = new MediaPlayer();
					AssetFileDescriptor asset;
					try {
						asset = getAssets().openFd("fale_endereco.mp3");
						mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
						asset.close();
						mp.prepare();
						mp.start();
					
						mp.setOnCompletionListener(AdicaoActivity.this);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				else if(numCampo == 3){
					MediaPlayer mp = new MediaPlayer();
					AssetFileDescriptor asset;
					try {
						asset = getAssets().openFd("fale_agora.mp3");
						mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
						asset.close();
						mp.prepare();
						mp.start();
					
						mp.setOnCompletionListener(AdicaoActivity.this);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				else if(numCampo == 4){
					MediaPlayer mp = new MediaPlayer();
					AssetFileDescriptor asset;
					try {
						asset = getAssets().openFd("fale_cep.mp3");
						mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
						asset.close();
						mp.prepare();
						mp.start();
					
						mp.setOnCompletionListener(AdicaoActivity.this);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				else if(numCampo == 5){
					MediaPlayer mp = new MediaPlayer();
					AssetFileDescriptor asset;
					try {
						asset = getAssets().openFd("fale_agora.mp3");
						mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
						asset.close();
						mp.prepare();
						mp.start();
					
						mp.setOnCompletionListener(AdicaoActivity.this);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				else if(numCampo == 0){
					if(!tts.isSpeaking()){
						MediaPlayer mp = new MediaPlayer();
						AssetFileDescriptor asset;
						try {
							asset = getAssets().openFd("fale_nome_local.mp3");
							mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
							asset.close();
							mp.prepare();
							mp.start();
						
							mp.setOnCompletionListener(AdicaoActivity.this);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				else{
					MediaPlayer mp = new MediaPlayer();
					AssetFileDescriptor asset;
					try {
						asset = getAssets().openFd("local_cadastrado.mp3");
						mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
						asset.close();
						mp.prepare();
						mp.start();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					mp.setOnCompletionListener(new OnCompletionListener() {
						
						public void onCompletion(MediaPlayer mp) {
							finish();	
						}
					});
				}
			}
		}
	}
	
	public void onDestroy(){
		Log.d("onPause", "onPause");
		
		if(tts != null){
			tts.stop();
			tts.shutdown();
		}
		
		super.onDestroy();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	}
	
	private void buscaComponentes(){
		this.txtNomeLocal = (EditText) findViewById(R.id.txtNomeLocal);
		this.txtEndereco = (EditText) findViewById(R.id.txtEndereco);
		this.txtCep = (EditText) findViewById(R.id.txtCep);
	}
	
	private void populaEndereco(){
		this.endereco.setNome(this.txtNomeLocal.getText().toString().toLowerCase());
		this.endereco.setEndereco(this.txtEndereco.getText().toString().toLowerCase());
		this.endereco.setCep(this.txtCep.getText().toString());
	}
	
	private void reconheceVoz(final MediaPlayer mp){
		try{
			Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
			intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
			intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "com.br.portablevision");
			intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
			sr.startListening(intent);
		
			mp.release();
		}
		catch(Exception e){
			Toast.makeText(AdicaoActivity.this, "Erro: " + e.getMessage(), Toast.LENGTH_SHORT).show();
		}
    }
	
	public void onCompletion(final MediaPlayer mp) {
    	reconheceVoz(mp);
	}
	
	private void finalizaActivity(Activity ac){
    	ac.finish();
    }
}
