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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ExcluirActivity extends Activity implements OnCompletionListener{
	private TextToSpeech tts;
	private SpeechRecognizer sr;
	
	private int falarOnResume = 0;
	private boolean inicio = false;
	private boolean localNaoEncontrado = false;
	private boolean faleNomeLocal = false;
	
	private boolean excluido = false;
	private boolean naoExcluido = false;
	
	private String tipoNavegacao;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.remocao);
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		final EditText txtNomeLocal = (EditText)findViewById(R.id.txtNomeLocal);
		
		final Button btnExcluir = (Button)findViewById(R.id.btExcluir);
		btnExcluir.setEnabled(false);
		
		Intent intent = getIntent();
		tipoNavegacao = intent.getStringExtra("navegacao");
		
		if(tipoNavegacao.equals("voz")){
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
			
			MediaPlayer mp = new MediaPlayer();
			AssetFileDescriptor asset;
			try {
				asset = getAssets().openFd("iniciar_navegacao.mp3"); //substituir pelo som 'fale o nome do local para exclusão'
				mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
				asset.close();
				mp.prepare();
				mp.start();
				
				mp.setOnCompletionListener(ExcluirActivity.this);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			Button btVoltar = (Button) findViewById(R.id.btVoltar);
			Button btProcurar = (Button) findViewById(R.id.btProcurar);
			
			btnExcluir.setClickable(false);
			btProcurar.setClickable(false);
			btVoltar.setClickable(false);
			
			txtNomeLocal.setEnabled(false);
		}
		else{
			Button btnVoltar = (Button)findViewById(R.id.btVoltar);
			btnVoltar.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					finalizaActivity(ExcluirActivity.this);	
				}
			});
			
			Button btnProcurar = (Button)findViewById(R.id.btProcurar);
			btnProcurar.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					if(!txtNomeLocal.getText().toString().equals("")){
						EnderecoDAO dao = new EnderecoDAO(ExcluirActivity.this);
						
						try{
							Endereco endereco = dao.getNomeLocal(txtNomeLocal.getText().toString().toLowerCase());
						
							if(endereco != null && endereco.getEndereco() != null){
								Toast.makeText(ExcluirActivity.this, "Nome do local encontrado!", Toast.LENGTH_LONG).show();
								btnExcluir.setEnabled(true);
							}
							else{
								Toast.makeText(ExcluirActivity.this, "Não existe este local", Toast.LENGTH_LONG).show();
							}
						}
						catch(SQLiteException e){
							Toast.makeText(ExcluirActivity.this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
							txtNomeLocal.setText("");
							txtNomeLocal.requestFocus();
						}
						catch(Exception e){
							Toast.makeText(ExcluirActivity.this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
							txtNomeLocal.setText("");
							txtNomeLocal.requestFocus();
						}
					}
					else{
						txtNomeLocal.requestFocus();
						Toast.makeText(ExcluirActivity.this, "Digite o nome do local", Toast.LENGTH_LONG).show();
					}	
				}
			});
			
			btnExcluir.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					if(!txtNomeLocal.getText().toString().equals("")){
						EnderecoDAO dao = new EnderecoDAO(ExcluirActivity.this);
						Endereco endereco = dao.getNomeLocal(txtNomeLocal.getText().toString().toLowerCase());
						
						if(endereco != null && endereco.getEndereco() != null){
							try{
								boolean excluiu = dao.excluir(endereco.getId());
								if(excluiu){
									Toast.makeText(ExcluirActivity.this, "Local excluído com sucesso!", Toast.LENGTH_LONG).show();
								}
								else{
									Toast.makeText(ExcluirActivity.this, "Local não excluído", Toast.LENGTH_LONG).show();
								}
							}
							catch(SQLiteException e){
								Toast.makeText(ExcluirActivity.this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
							}
						}
						else{
							Toast.makeText(ExcluirActivity.this, "Local inválido!", Toast.LENGTH_LONG).show();
						}
					}
					else{
						Toast.makeText(ExcluirActivity.this, "Digite o nome do local e depois pressione o botão procurar", Toast.LENGTH_LONG).show();
					}	
				}
			});
		}
	}
	
	class MyRecognition implements RecognitionListener{

		public void onBeginningOfSpeech() {
			// TODO Auto-generated method stub
			
		}

		public void onBufferReceived(byte[] arg0) {
			// TODO Auto-generated method stub
			
		}

		public void onEndOfSpeech() {
			// TODO Auto-generated method stub
			
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
					
					mp.setOnCompletionListener(ExcluirActivity.this);
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
					
					mp.setOnCompletionListener(ExcluirActivity.this);
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
					
					mp.setOnCompletionListener(ExcluirActivity.this);
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
					
					mp.setOnCompletionListener(ExcluirActivity.this);
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
					
					mp.setOnCompletionListener(ExcluirActivity.this);
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
					
					mp.setOnCompletionListener(ExcluirActivity.this);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		public void onEvent(int eventType, Bundle params) {
			// TODO Auto-generated method stub
			
		}

		public void onPartialResults(Bundle partialResults) {
			// TODO Auto-generated method stub
			
		}

		public void onReadyForSpeech(Bundle params) {
			// TODO Auto-generated method stub
			
		}

		public void onResults(Bundle results) {
			ArrayList<String> strlist = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            
            EditText txtNomeLocal = (EditText)findViewById(R.id.txtNomeLocal);
			
			EnderecoDAO dao = new EnderecoDAO(ExcluirActivity.this);
			
			if(strlist != null && strlist.get(0) != "" && !strlist.contains("excluir") && !strlist.contains("corrigir") && !strlist.contains("voltar")){
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
					Toast.makeText(ExcluirActivity.this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
			else if(strlist.contains("excluir")){
				if(!txtNomeLocal.getText().toString().equals("")){
					try{
						Endereco endereco = dao.getNomeLocal(txtNomeLocal.getText().toString());
						
						if(endereco != null){
							try{
								boolean excluiu = dao.excluir(endereco.getId());
								if(excluiu){
									excluido = true;
									onResume();
								}
								else{
									naoExcluido = true;
									onResume();
								}
							}
							catch(SQLiteException e){
								//MediaPlayer 'Ocorreu um erro na exclusão'
							}
						}
					}
					catch(Exception e){
						Toast.makeText(ExcluirActivity.this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
					}
					finally{
						dao.close();
					}
				}
			}
			else if(strlist.contains("voltar")){
				finalizaActivity(ExcluirActivity.this);
			}
		}

		public void onRmsChanged(float rmsdB) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public void onResume(){
		super.onResume();
		
		falarOnResume++;
		
		EditText txtNomeLocal = (EditText)findViewById(R.id.txtNomeLocal);
		
		if(!txtNomeLocal.getText().toString().equals("") && falarOnResume == 2){
			tts.speak(txtNomeLocal.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
		}
		
		if(tipoNavegacao.equals("voz") && falarOnResume > 1){
			if(!txtNomeLocal.getText().toString().equals("")){
				if(excluido){
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
				}
				else if(naoExcluido){
					MediaPlayer mp = new MediaPlayer();
					AssetFileDescriptor asset;
					try {
						asset = getAssets().openFd("exclusao_nao_realizada.mp3");
						mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
						asset.close();
						mp.prepare();
						mp.start();
						
						mp.setOnCompletionListener(ExcluirActivity.this);
					} catch (IOException e) {
						e.printStackTrace();
					}		
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
						
						mp.setOnCompletionListener(ExcluirActivity.this);
					} catch (IOException e) {
						e.printStackTrace();
					}		
				}
			}
		}
		else if(inicio){
			MediaPlayer mp = new MediaPlayer();
			AssetFileDescriptor asset;
			try {
				asset = getAssets().openFd("iniciar_navegacao.mp3"); //substituir pelo som 'fale o nome do local para exclusão'
				mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
				asset.close();
				mp.prepare();
				mp.start();
				
				mp.setOnCompletionListener(ExcluirActivity.this);
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
	
	public void finalizaActivity(Activity ac){
		ac.finish();
	}
	
	public void reconheceVoz(final MediaPlayer mp){
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "com.br.portablevision");
		intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
		sr.startListening(intent);
		
		mp.release();
    }

	public void onCompletion(MediaPlayer mp) {
		reconheceVoz(mp);
	}
}
