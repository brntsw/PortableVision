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
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ProcurarActivity extends Activity implements OnCompletionListener {
	private TextToSpeech tts = null;
	private EditText txtNomeLocal;
	
	private int falarOnResume = 0;
	private boolean encontrou = false;
	private boolean inicio = false;
	private boolean jaFalouLocal = false;
	
	private SpeechRecognizer sr;
	
	private String tipoNavegacao;
	
	public void onCreate(Bundle bundle){
		super.onCreate(bundle);
		setContentView(R.layout.procura);
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		txtNomeLocal = (EditText)findViewById(R.id.txtNomeLocal);
		
		final Button btnNavegar = (Button)findViewById(R.id.btNavegar);
		btnNavegar.setEnabled(false);
		
		Intent intent = getIntent();
		tipoNavegacao = intent.getStringExtra("navegacao");
		
		if(tipoNavegacao.equals("voz")){
			final Locale locale = new Locale("pt", "BR");
			tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
				
				public void onInit(int status) {
					if(status != TextToSpeech.ERROR){
						tts.setLanguage(locale);
						tts.setOnUtteranceCompletedListener(new OnUtteranceCompletedListener() {
							
							public void onUtteranceCompleted(String utteranceId) {
								Toast.makeText(ProcurarActivity.this, "Terminou TTS", Toast.LENGTH_LONG).show();
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
				
				mp.setOnCompletionListener(ProcurarActivity.this);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			Button btVoltar = (Button) findViewById(R.id.btVoltar);
			Button btProcurar = (Button) findViewById(R.id.btProcurar);
			
			btVoltar.setClickable(false);
			btProcurar.setClickable(false);
			
			txtNomeLocal.setEnabled(false);
		}
		else{
			Button btVoltar = (Button) findViewById(R.id.btVoltar);
			Button btProcurar = (Button) findViewById(R.id.btProcurar);
			
			btVoltar.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					finalizaActivity(ProcurarActivity.this);
				}
			});
			
			btnNavegar.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					if(!txtNomeLocal.getText().toString().equals("")){
						try{
							EnderecoDAO dao = new EnderecoDAO(ProcurarActivity.this);
							Endereco endereco = dao.getNomeLocal(txtNomeLocal.getText().toString().toLowerCase());
							
							if(endereco != null){
								Intent intent = new Intent(ProcurarActivity.this, GpsActivity.class);
								intent.putExtra("endereco", endereco.getEndereco());
								startActivity(intent);
							}
							else{
								Toast.makeText(ProcurarActivity.this, "Local não encontrado!", Toast.LENGTH_LONG).show();
								txtNomeLocal.setText("");
								txtNomeLocal.requestFocus();
							}
						}
						catch(SQLiteException e){
							Toast.makeText(ProcurarActivity.this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
						}
						catch(Exception e){
							Toast.makeText(ProcurarActivity.this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
						}
					}
					else{
						Toast.makeText(ProcurarActivity.this, "Digite o nome do local", Toast.LENGTH_LONG).show();
					}
				}
			});
			
			btProcurar.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					String local = txtNomeLocal.getText().toString().toLowerCase();
					
					if(!local.equals("")){
						EnderecoDAO dao = new EnderecoDAO(ProcurarActivity.this);
						Endereco endereco;
						
						endereco = dao.getNomeLocal(local);
						if(endereco != null){
							Toast.makeText(ProcurarActivity.this, "Local encontrado com sucesso!", Toast.LENGTH_LONG).show();
							
							btnNavegar.setEnabled(true);
						}
						else{
							Toast.makeText(ProcurarActivity.this, "Local não encontrado!", Toast.LENGTH_LONG).show();
							txtNomeLocal.setText("");
							txtNomeLocal.requestFocus();
						}
					}
					else{
						Toast.makeText(ProcurarActivity.this, "Digite o nome do local", Toast.LENGTH_LONG).show();
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
					
					mp.setOnCompletionListener(ProcurarActivity.this);
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
					
					mp.setOnCompletionListener(ProcurarActivity.this);
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
					
					mp.setOnCompletionListener(ProcurarActivity.this);
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
					
					mp.setOnCompletionListener(ProcurarActivity.this);
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
					
					mp.setOnCompletionListener(ProcurarActivity.this);
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
					
					mp.setOnCompletionListener(ProcurarActivity.this);
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
            if(strlist != null && strlist.get(0) != "" && !strlist.contains("procurar") && !strlist.contains("procurar local") && !strlist.contains("voltar") && !strlist.contains("corrigir") && !strlist.contains("navegar") && !strlist.contains("navegação") && !strlist.contains("iniciar navegação")){
            	txtNomeLocal.setText(strlist.get(0));
            	onResume();
            }
            else if(results != null && strlist.contains("procurar") || strlist.contains("procurar local")){
            	String strLocal = txtNomeLocal.getText().toString();
				
				EnderecoDAO dao = new EnderecoDAO(ProcurarActivity.this);
				Endereco endereco;
				
				if(strLocal.toString() != ""){
					endereco = dao.getNomeLocal(strLocal);
					if(endereco != null && endereco.getNome() != null){
						encontrou = true;
						onResume();
					}
					else{
						txtNomeLocal.setText("");
						
						MediaPlayer media = new MediaPlayer();
						AssetFileDescriptor asset;
						try {
							asset = getAssets().openFd("endereco_nao_encontrado.mp3");
							media.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
							asset.close();
							media.prepare();
							media.start();
						
							media.setOnCompletionListener(ProcurarActivity.this);
						} catch (IOException e) {
							e.printStackTrace();
						}
						
						jaFalouLocal = false;
					}
				}
				
				dao.close();
            }
            else if(results != null && strlist.contains("navegar") || strlist.contains("navegação") || strlist.contains("iniciar navegação")){
            	String nomeLocal = txtNomeLocal.getText().toString();
				
				EnderecoDAO dao = new EnderecoDAO(ProcurarActivity.this);
				Endereco endereco;
				
				if(nomeLocal != ""){
					endereco = dao.getNomeLocal(nomeLocal);
					if(endereco.getEndereco() != null){
						Intent intent = new Intent(ProcurarActivity.this, GpsActivity.class);
						intent.putExtra("endereco", endereco.getEndereco());
						startActivity(intent);
						falarOnResume = 0;
					
						finalizaActivity(ProcurarActivity.this);
					}
					else{
						MediaPlayer media = new MediaPlayer();
						AssetFileDescriptor asset;
						try {
							asset = getAssets().openFd("endereco_nao_encontrado.mp3");
							media.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
							asset.close();
							media.prepare();
							media.start();
						
							media.setOnCompletionListener(ProcurarActivity.this);
						} catch (IOException e) {
							e.printStackTrace();
						}
						
						jaFalouLocal = false;
					}
				}
            }
            else if(results != null && strlist.contains("corrigir")){
            	txtNomeLocal.setText("");
            	inicio = true;
            	onResume();
            }
            else if(results != null && strlist.contains("voltar")){
            	finalizaActivity(ProcurarActivity.this);
            }
		}

		public void onRmsChanged(float rmsdB) {
			
		}
	}
	
	public void onResume(){
		if(tipoNavegacao.equals("voz")){
			if(falarOnResume <= 2){
				falarOnResume++;
			}
			
			if(!txtNomeLocal.getText().toString().equals("") && !jaFalouLocal){
				tts.speak(txtNomeLocal.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
				jaFalouLocal = true;
			}
			
			if(falarOnResume > 1){
				if(inicio){
					MediaPlayer mp = new MediaPlayer();
					AssetFileDescriptor asset;
					try {
						asset = getAssets().openFd("fale_nome_local.mp3");
						mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
						asset.close();
						mp.prepare();
						mp.start();
						
						mp.setOnCompletionListener(ProcurarActivity.this);
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					inicio = false;
				}
				else if(encontrou){
					MediaPlayer mp = new MediaPlayer();
					AssetFileDescriptor asset;
					try {
						asset = getAssets().openFd("local_encontrado.mp3");
						mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
						asset.close();
						mp.prepare();
						mp.start();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					mp.setOnCompletionListener(new OnCompletionListener() {
						
						public void onCompletion(MediaPlayer mp) {
							MediaPlayer media = new MediaPlayer();
							AssetFileDescriptor asset;
							try {
								asset = getAssets().openFd("fale_agora.mp3");
								media.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
								asset.close();
								media.prepare();
								media.start();
							
								media.setOnCompletionListener(ProcurarActivity.this);
							} catch (IOException e) {
								e.printStackTrace();
							}		
						}
					});
				}
				else{
					MediaPlayer mp = new MediaPlayer();
					AssetFileDescriptor asset;
					try {
						asset = getAssets().openFd("fale_agora.mp3");
						mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
						asset.close();
						mp.prepare();
						mp.start();
						
						mp.setOnCompletionListener(ProcurarActivity.this);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		super.onResume();
	}
	
	public void onDestroy(){
		if(tts != null){
			tts.stop();
			tts.shutdown();
		}
		super.onDestroy();
	}
	
	public void finalizaActivity(Activity ac){
    	ac.finish();
    }

	public void onCompletion(MediaPlayer mp) {
		reconheceVoz(mp);
	}
	
	public void reconheceVoz(MediaPlayer mp){
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "com.br.portablevision");
		intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
		sr.startListening(intent);
		
		mp.release();
	}
}
