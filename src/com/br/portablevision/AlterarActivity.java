package com.br.portablevision;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import com.br.portablevision.classes.Endereco;
import com.br.portablevision.persistente.EnderecoDAO;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
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

public class AlterarActivity extends Activity implements OnCompletionListener, OnUtteranceCompletedListener {
	private Endereco endereco = new Endereco();
	
	private String nomeOriginalEndereco;
	
	private EditText txtNomeLocal;
	private EditText txtEndereco;
	private EditText txtCep;
	
	private TextToSpeech tts = null;
	
	private SpeechRecognizer sr;
	
	private int falarOnResume = 0;
	private int numCampo = 0;
	
	private boolean alteracaoLocal = false;
	
	private int idLocal;
	
	private String tipoNavegacao;
	private String nomeLocal;
	
	//Variáveis para quando vier do ListaActivity
	private boolean falaEndereco = false;
	private boolean falaCep = false;
	private boolean listaNomeLocal = false;
	private boolean falaLocalCorrigido = false;
	private int vezesAudioFalado = 0;
	private boolean listaEndereco = false;
	private boolean falaEnderecoCorrigido = false;
	
	public void onCreate(Bundle bundle){
		super.onCreate(bundle);
		setContentView(R.layout.alteracao);
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		buscaComponentes();
		
		txtNomeLocal.requestFocus();
		
		final Button btnAlterar = (Button) findViewById(R.id.btAlterar);
		btnAlterar.setEnabled(false);
		
		Intent intent = getIntent();
		tipoNavegacao = intent.getStringExtra("navegacao");
		
		if(tipoNavegacao.equals("voz")){
			nomeLocal = intent.getStringExtra("nomeLocal");
			if(nomeLocal != null){
				EnderecoDAO dao = new EnderecoDAO(AlterarActivity.this);
				endereco = dao.getNomeLocal(nomeLocal);
				idLocal = endereco.getId();
				txtNomeLocal.setText(endereco.getNome());
				MediaPlayer mp = new MediaPlayer();
				AssetFileDescriptor asset;
				try {
					asset = getAssets().openFd("local.mp3");
					mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
					asset.close();
					mp.prepare();
					mp.start();
					
					mp.setOnCompletionListener(new OnCompletionListener() {
						
						public void onCompletion(MediaPlayer mp) {
							HashMap<String, String> hash = new HashMap<String, String>();
							hash.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "local");
							tts.speak(txtNomeLocal.getText().toString(), TextToSpeech.QUEUE_FLUSH, hash);
							callOnUtterance();
						}
					});
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else{
				MediaPlayer mp = new MediaPlayer();
				AssetFileDescriptor asset;
				try {
					asset = getAssets().openFd("fale_nome_local.mp3");
					mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
					asset.close();
					mp.prepare();
					mp.start();
					
					mp.setOnCompletionListener(AlterarActivity.this);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			final Locale locale = new Locale("pt", "BR");
			tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
				
				public void onInit(int status) {
					if(status != TextToSpeech.ERROR){
						tts.setLanguage(locale);
					}
				}
			});
			
			sr = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
	        MyRecognition listener = new MyRecognition();
	        sr.setRecognitionListener(listener);
		
	        Button btnVoltar = (Button) findViewById(R.id.btVoltar);
			Button btnProcurar = (Button) findViewById(R.id.btProcurar);
			
			btnVoltar.setClickable(false);
			btnProcurar.setClickable(false);
			btnAlterar.setClickable(false);
			
			txtNomeLocal.setEnabled(false);
			txtEndereco.setEnabled(false);
			txtCep.setEnabled(false);
		}
		else{
			nomeLocal = intent.getStringExtra("nomeLocal");
			if(nomeLocal != null){
				Endereco endereco;
				EnderecoDAO dao = new EnderecoDAO(AlterarActivity.this);
				endereco = dao.getNomeLocal(nomeLocal);
				
				txtNomeLocal.setText(endereco.getNome());
				txtEndereco.setText(endereco.getEndereco());
				txtCep.setText(endereco.getCep());
				
				txtNomeLocal.requestFocus();
				
				Button btnVoltar = (Button) findViewById(R.id.btVoltar);
				Button btnProcurar = (Button)findViewById(R.id.btProcurar);
				btnAlterar.setEnabled(true);
				
				btnVoltar.setOnClickListener(new OnClickListener() {
					
					public void onClick(View v) {
						finalizaActivity(AlterarActivity.this);
					}
				});
				
				btnProcurar.setOnClickListener(new OnClickListener() {
					
					public void onClick(View v) {
						if(!txtNomeLocal.getText().toString().equals("")){
							if(!txtEndereco.getText().toString().equals("")){
								Toast.makeText(AlterarActivity.this, "O local já foi encontrado!", Toast.LENGTH_LONG).show();
							}
							else{
								EnderecoDAO dao = new EnderecoDAO(AlterarActivity.this);
								Endereco endereco = dao.getNomeLocal(txtNomeLocal.getText().toString().toLowerCase());
								
								if(endereco != null && endereco.getNome() != null){
									Toast.makeText(AlterarActivity.this, "Local encontrado!", Toast.LENGTH_LONG).show();
									
									txtEndereco.setText(endereco.getEndereco());
									txtCep.setText(endereco.getCep());
								}
								else{
									Toast.makeText(AlterarActivity.this, "Local não encontrado!", Toast.LENGTH_LONG).show();
								}
							}
						}
						else{
							Toast.makeText(AlterarActivity.this, "Digite o nome do local", Toast.LENGTH_LONG).show();
						}
					}
				});
				
				btnAlterar.setOnClickListener(new OnClickListener() {
					
					public void onClick(View v) {
						String valueNome = txtNomeLocal.getText().toString();
						String valueEndereco = txtEndereco.getText().toString();
						String valueCep = txtCep.getText().toString();
						
						int alterou = 0;
						
						if(!valueNome.equals("") && !valueEndereco.equals("") || !valueCep.equals("")){					
							try{
								EnderecoDAO dao = new EnderecoDAO(AlterarActivity.this);
								Endereco endereco = dao.getNomeLocal(valueNome.toLowerCase());
								alterou = dao.alterar(valueNome.toLowerCase(), valueEndereco, valueCep, endereco.getId());
								dao.close();
							}
							catch(Exception e){
								Toast.makeText(AlterarActivity.this, "Erro: " + e.getMessage(), Toast.LENGTH_SHORT).show();
							}
							
							if(alterou == 1){
								Toast.makeText(AlterarActivity.this, "Alteração realizada com sucesso!", Toast.LENGTH_LONG).show();
							}
							else{
								Toast.makeText(AlterarActivity.this, "Não foi possível realizar a alteração", Toast.LENGTH_LONG).show();
							}
						}
						else{
							Toast.makeText(AlterarActivity.this, "Preencha pelo menos o nome do local e o endereço", Toast.LENGTH_LONG).show();
						}	
					}
				});
			}
			
			Button btnVoltar = (Button) findViewById(R.id.btVoltar);
			Button btnProcurar = (Button)findViewById(R.id.btProcurar);
			
			btnVoltar.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					finalizaActivity(AlterarActivity.this);
				}
			});
			
			btnProcurar.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					if(!txtNomeLocal.getText().toString().equals("")){
						if(!txtEndereco.getText().toString().equals("")){
							Toast.makeText(AlterarActivity.this, "O local já foi encontrado!", Toast.LENGTH_LONG).show();
						}
						else{
							EnderecoDAO dao = new EnderecoDAO(AlterarActivity.this);
							Endereco endereco = dao.getNomeLocal(txtNomeLocal.getText().toString().toLowerCase());
							
							if(endereco != null && endereco.getNome() != null){
								Toast.makeText(AlterarActivity.this, "Local encontrado!", Toast.LENGTH_LONG).show();
								
								txtEndereco.setText(endereco.getEndereco());
								txtCep.setText(endereco.getCep());
								
								btnAlterar.setEnabled(true);
							}
							else{
								Toast.makeText(AlterarActivity.this, "Local não encontrado!", Toast.LENGTH_LONG).show();
							}
						}
					}
					else{
						Toast.makeText(AlterarActivity.this, "Digite o nome do local", Toast.LENGTH_LONG).show();
					}
				}
			});
			
			btnAlterar.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					String valueNome = txtNomeLocal.getText().toString();
					String valueEndereco = txtEndereco.getText().toString();
					String valueCep = txtCep.getText().toString();
					
					int alterou = 0;
					
					if(!valueNome.equals("") && !valueEndereco.equals("") || !valueCep.equals("")){					
						try{
							EnderecoDAO dao = new EnderecoDAO(AlterarActivity.this);
							Endereco endereco = dao.getNomeLocal(valueNome.toLowerCase());
							alterou = dao.alterar(valueNome.toLowerCase(), valueEndereco, valueCep, endereco.getId());
							dao.close();
						}
						catch(Exception e){
							Toast.makeText(AlterarActivity.this, "Erro: " + e.getMessage(), Toast.LENGTH_SHORT).show();
						}
						
						if(alterou == 1){
							Toast.makeText(AlterarActivity.this, "Alteração realizada com sucesso!", Toast.LENGTH_LONG).show();
						}
						else{
							Toast.makeText(AlterarActivity.this, "Não foi possível realizar a alteração", Toast.LENGTH_LONG).show();
						}
					}
					else{
						Toast.makeText(AlterarActivity.this, "Preencha pelo menos o nome do local e o endereço", Toast.LENGTH_LONG).show();
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
					
					mp.setOnCompletionListener(AlterarActivity.this);
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
					
					mp.setOnCompletionListener(AlterarActivity.this);
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
					
					mp.setOnCompletionListener(AlterarActivity.this);
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
					
					mp.setOnCompletionListener(AlterarActivity.this);
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
					
					mp.setOnCompletionListener(AlterarActivity.this);
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
					
					mp.setOnCompletionListener(AlterarActivity.this);
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
			
			if(!strlist.contains("local correto") && !strlist.contains("local certo") && !strlist.contains("corrigir local") && !strlist.contains("local errado") && !strlist.contains("endereço correto") && !strlist.contains("endereço certo") && !strlist.contains("corrigir endereço") && !strlist.contains("endereço errado") && !strlist.contains("cep correto") && !strlist.contains("cep certo") && !strlist.contains("cep incorreto") && !strlist.contains("cep errado") && !strlist.contains("voltar")){
				nomeLocal = getIntent().getStringExtra("nomeLocal");
				if(nomeLocal != null){
					if(txtNomeLocal.hasFocus()){
						txtNomeLocal.setText(strlist.get(0));
						falaLocalCorrigido = true;
						onResume();
					}
					else if(txtEndereco.hasFocus()){
						txtEndereco.setText(strlist.get(0));
						falaEnderecoCorrigido = true;
						onResume();
					}
				}
				numCampo++;
			}
			
			buscaComponentes();
			
			if(nomeLocal == null){
				if(strlist != null && numCampo == 1 && !strlist.contains("voltar") && !strlist.contains("local correto") && !strlist.contains("corrigir local") && !strlist.contains("corrigir endereço")){
					if(!alteracaoLocal){
						Endereco nomeLocal = null;
						try{
							EnderecoDAO dao = new EnderecoDAO(AlterarActivity.this);
							nomeLocal = dao.getNomeLocal(strlist.get(0));
							dao.close();
							idLocal = nomeLocal.getId();
						}
						catch(Exception e){
							Toast.makeText(AlterarActivity.this, "Erro: " + e.getMessage(), Toast.LENGTH_SHORT).show();
						}
						
						if(strlist.get(0) != null && nomeLocal.getNome() != null){
							txtNomeLocal.setText(nomeLocal.getNome());
							nomeOriginalEndereco = nomeLocal.getNome();
							onResume();
						}
						else{
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
										numCampo = 0;
										onResume();
									}
								});
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
				else{
					txtNomeLocal.setText(strlist.get(0));
					alteracaoLocal = false;
					
					numCampo = 1;
					onResume();
				}
			}
			else if(results != null && numCampo == 3 && !strlist.contains("voltar") && !strlist.contains("local correto") && !strlist.contains("corrigir local")){
				if(strlist.get(0) != ""){
					txtEndereco.setText(strlist.get(0));
					onResume();
				}
			}
			else if(results != null && numCampo == 5 && !strlist.contains("voltar") && !strlist.contains("local correto") && !strlist.contains("corrigir local")){
				if(strlist.get(0) != ""){
					String cep = strlist.get(0).replace(" ", "");
					txtCep.setText(cep);
					onResume();
				}
			}
			else if(strlist.contains("local correto") || strlist.contains("local certo")){
				if(txtEndereco.hasFocus()){
					onResume();
				}
				txtEndereco.requestFocus();
				falaEndereco = true;
				onResume();
			}
			else if(strlist.contains("corrigir local") || strlist.contains("local errado")){
				nomeLocal = getIntent().getStringExtra("nomeLocal");
				if(nomeLocal != null){
					listaNomeLocal = true;
				}
				else{
					numCampo = 0;
				}
				txtNomeLocal.setText("");
				txtNomeLocal.requestFocus();
				onResume();
			}
			else if(strlist.contains("endereço correto") || strlist.contains("endereço certo")){
				txtCep.requestFocus();
				falaCep = true;
				falaEndereco = false;
				onResume();
			}
			else if(strlist.contains("corrigir endereço") || strlist.contains("endereço errado")){
				nomeLocal = getIntent().getStringExtra("nomeLocal");
				if(nomeLocal != null){
					falaEndereco = false;
					listaEndereco = true;
				}
				else{
					numCampo = 2;
				}
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
			else if(strlist.get(0).contains("alterar local") || strlist.contains("alterar nome")){
				txtNomeLocal.setText("");
				numCampo = 0;
				alteracaoLocal = true;
				onResume();
			}
			else if(strlist.contains("alterar")){
				String valueNome = txtNomeLocal.getText().toString();
				String valueEndereco = txtEndereco.getText().toString();
				String valueCep = txtCep.getText().toString();
				
				int alterou = 0;
				
				if(valueNome != "" && valueEndereco != "" || valueCep != ""){
					populaEndereco();
					
					if(endereco.getNome().toString() != "" && endereco.getEndereco().toString() != ""){
						try{
							EnderecoDAO dao = new EnderecoDAO(AlterarActivity.this);
							alterou = dao.alterarById(endereco, idLocal);
							dao.close();
						}
						catch(Exception e){
							Toast.makeText(AlterarActivity.this, "Erro: " + e.getMessage(), Toast.LENGTH_SHORT).show();
						}
						
						if(alterou == 1){
							MediaPlayer mp = new MediaPlayer();
							AssetFileDescriptor asset;
							try {
								asset = getAssets().openFd("alteracao_sucesso.mp3");
								mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
								asset.close();
								mp.prepare();
								mp.start();
								
								mp.setOnCompletionListener(new OnCompletionListener() {
									
									public void onCompletion(MediaPlayer mp) {
										txtNomeLocal.setText("");
										txtEndereco.setText("");
										txtCep.setText("");
										
										txtNomeLocal.requestFocus();
										
										MediaPlayer media = new MediaPlayer();
										AssetFileDescriptor asset;
										try {
											asset = getAssets().openFd("fale_agora.mp3");
											media.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
											asset.close();
											media.prepare();
											media.start();
										
											media.setOnCompletionListener(AlterarActivity.this);
										} catch (IOException e) {
											e.printStackTrace();
										}
									}
								});
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						else{
							MediaPlayer mp = new MediaPlayer();
							AssetFileDescriptor asset;
							try {
								asset = getAssets().openFd("alteracao_erro.mp3");
								mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
								asset.close();
								mp.prepare();
								mp.start();
							
								mp.setOnCompletionListener(new OnCompletionListener() {
									
									public void onCompletion(MediaPlayer mp) {
										numCampo = 0;
										
										txtNomeLocal.setText("");
										txtEndereco.setText("");
										txtCep.setText("");
										
										txtNomeLocal.requestFocus();
										
										onResume();
									}
								});
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
					else{
						Toast.makeText(AlterarActivity.this, "Vazio", Toast.LENGTH_LONG).show();
					}
				}
				else{
					Toast.makeText(AlterarActivity.this, "Preencha pelo menos o nome e endereço", Toast.LENGTH_LONG).show();
				}
			}
			else if(strlist.contains("voltar")){
				finalizaActivity(AlterarActivity.this);
				
				numCampo = 0;
				falarOnResume = 0;
			}
		}

		public void onRmsChanged(float rmsdB) {
			
		}
	}
	
	public void onResume(){
		Log.d("OnResume", "OnResume");
		
		if(tipoNavegacao.equals("voz")){
			Intent intent = getIntent();
			nomeLocal = intent.getStringExtra("nomeLocal");
			if(nomeLocal != null){
				if(falaEndereco){
					txtEndereco.setText(endereco.getEndereco());
					MediaPlayer mp = new MediaPlayer();
					AssetFileDescriptor asset;
					try {
						asset = getAssets().openFd("endereco.mp3");
						mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
						asset.close();
						mp.prepare();
						mp.start();
						
						mp.setOnCompletionListener(new OnCompletionListener() {
							
							public void onCompletion(MediaPlayer mp) {
								HashMap<String, String> hash = new HashMap<String, String>();
								hash.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "endereço");
								tts.speak(txtEndereco.getText().toString(), TextToSpeech.QUEUE_FLUSH, hash);
								callOnUtterance();
							}
						});
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				else if(falaCep){
					txtCep.setText(endereco.getCep());
					MediaPlayer mp = new MediaPlayer();
					AssetFileDescriptor asset;
					try {
						asset = getAssets().openFd("cep.mp3");
						mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
						asset.close();
						mp.prepare();
						mp.start();
						
						mp.setOnCompletionListener(new OnCompletionListener() {
							
							public void onCompletion(MediaPlayer mp) {
								HashMap<String, String> hash = new HashMap<String, String>();
								hash.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "cep");
								tts.speak(txtCep.getText().toString(), TextToSpeech.QUEUE_FLUSH, hash);
								callOnUtterance();
							}
						});
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					falaCep = false;
				}
				else if(listaNomeLocal){
					MediaPlayer mp = new MediaPlayer();
					AssetFileDescriptor asset;
					try {
						asset = getAssets().openFd("fale_nome_local.mp3");
						mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
						asset.close();
						mp.prepare();
						mp.start();
						
						mp.setOnCompletionListener(this);
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					listaNomeLocal = false;
				}
				else if(listaEndereco){
					MediaPlayer mp = new MediaPlayer();
					AssetFileDescriptor asset;
					try {
						asset = getAssets().openFd("fale_endereco.mp3");
						mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
						asset.close();
						mp.prepare();
						mp.start();
						
						mp.setOnCompletionListener(this);
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					listaEndereco = false;
				}
				else if(falaLocalCorrigido){
					if(txtNomeLocal.getText().toString() != null){
						MediaPlayer mp = new MediaPlayer();
						AssetFileDescriptor asset;
						try {
							asset = getAssets().openFd("fala_local_corrigido.mp3");
							mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
							asset.close();
							mp.prepare();
							mp.start();
							
							mp.setOnCompletionListener(new OnCompletionListener() {
								
								public void onCompletion(MediaPlayer mp) {
									HashMap<String, String> hash = new HashMap<String, String>();
									hash.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "localCorrigido");
									tts.speak(txtNomeLocal.getText().toString(), TextToSpeech.QUEUE_FLUSH, hash);
									callOnUtterance();
									
									falaLocalCorrigido = false;
								}
							});
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				else if(falaEnderecoCorrigido){
					if(txtEndereco.getText().toString() != null){
						MediaPlayer mp = new MediaPlayer();
						AssetFileDescriptor asset;
						try {
							asset = getAssets().openFd("fala_endereco_corrigido.mp3");
							mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
							asset.close();
							mp.prepare();
							mp.start();
							
							mp.setOnCompletionListener(new OnCompletionListener() {
								
								public void onCompletion(MediaPlayer mp) {
									HashMap<String, String> hash = new HashMap<String, String>();
									hash.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "enderecoCorrigido");
									tts.speak(txtEndereco.getText().toString(), TextToSpeech.QUEUE_FLUSH, hash);
									callOnUtterance();
									
									falaEnderecoCorrigido = false;
								}
							});
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
			else{
				if(falarOnResume <= 2){
					falarOnResume++;
				}
				
				if(!txtNomeLocal.getText().toString().equals("") && numCampo == 1){
					tts.speak(txtNomeLocal.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
				}
				
				if(falarOnResume > 1){
					if(numCampo == 0 && !alteracaoLocal){
						MediaPlayer mp = new MediaPlayer();
						AssetFileDescriptor asset;
						try {
							asset = getAssets().openFd("fale_nome_local.mp3");
							mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
							asset.close();
							mp.prepare();
							mp.start();
							
							mp.setOnCompletionListener(AlterarActivity.this);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					else if(numCampo == 0 && alteracaoLocal){
						MediaPlayer mp = new MediaPlayer();
						AssetFileDescriptor asset;
						try {
							asset = getAssets().openFd("local_alteracao.mp3");
							mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
							asset.close();
							mp.prepare();
							mp.start();
							
							mp.setOnCompletionListener(AlterarActivity.this);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					else if(numCampo == 1){
						MediaPlayer mp = new MediaPlayer();
						AssetFileDescriptor asset;
						try {
							asset = getAssets().openFd("fale_agora.mp3");
							mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
							asset.close();
							mp.prepare();
							mp.start();
						
							mp.setOnCompletionListener(AlterarActivity.this);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					else if(numCampo == 2){
						EnderecoDAO dao = new EnderecoDAO(AlterarActivity.this);
						final Endereco campoEndereco;
						
						campoEndereco = dao.getNomeLocal(nomeOriginalEndereco);
						dao.close();
						txtEndereco.setText(campoEndereco.getEndereco());
						
						MediaPlayer mp = new MediaPlayer();
						AssetFileDescriptor asset;
						try {
							asset = getAssets().openFd("endereco.mp3");
							mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
							asset.close();
							mp.prepare();
							mp.start();
						
							mp.setOnCompletionListener(new OnCompletionListener() {
								
								public void onCompletion(MediaPlayer mp) {
									if(!txtEndereco.getText().toString().equals("") && campoEndereco.getEndereco() != null){
										tts.speak(txtEndereco.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
										numCampo++;
										onResume();
									}
								}
							});
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
						
							mp.setOnCompletionListener(AlterarActivity.this);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					else if(numCampo == 4){
						EnderecoDAO dao = new EnderecoDAO(AlterarActivity.this);
						final Endereco enderecoCep;
						
						enderecoCep = dao.getNomeLocal(nomeOriginalEndereco);
						txtCep.setText(enderecoCep.getCep());
						
						dao.close();
						
						MediaPlayer mp = new MediaPlayer();
						AssetFileDescriptor asset;
						try {
							asset = getAssets().openFd("cep.mp3");
							mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
							asset.close();
							mp.prepare();
							mp.start();
						
							mp.setOnCompletionListener(new OnCompletionListener() {
								
								public void onCompletion(MediaPlayer mp) {
									if(!txtCep.getText().toString().equals("") && enderecoCep.getCep() != null){
										tts.speak(txtCep.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
										numCampo++;
										onResume();
									}
									else{
										MediaPlayer media = new MediaPlayer();
										AssetFileDescriptor asset;
										try {
											asset = getAssets().openFd("sem_cep.mp3");
											media.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
											asset.close();
											media.prepare();
											media.start();
										
											media.setOnCompletionListener(new OnCompletionListener() {
												
												public void onCompletion(MediaPlayer mp) {
													onResume();	
												}
											});
										} catch (IOException e) {
											e.printStackTrace();
										}	
									}
								}
							});
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
						
							mp.setOnCompletionListener(AlterarActivity.this);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		
		super.onResume();
	}
	
	public void onDestroy(){
		Log.d("onPause", "onPause");
		
		if(tts != null){
			tts.stop();
			tts.shutdown();
		}
		
		super.onPause();
	}
	
	public void buscaComponentes(){
		this.txtNomeLocal = (EditText) findViewById(R.id.txtNomeLocal);
		this.txtEndereco = (EditText) findViewById(R.id.txtEndereco);
		this.txtCep = (EditText) findViewById(R.id.txtCep);
	}
	
	public void populaEndereco(){
		this.endereco.setNome(this.txtNomeLocal.getText().toString().toLowerCase());
		this.endereco.setEndereco(this.txtEndereco.getText().toString());
		this.endereco.setCep(this.txtCep.getText().toString());
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
	
	private void finalizaActivity(Activity ac){
    	ac.finish();
    }

	public void onUtteranceCompleted(String utteranceId) {
		if(utteranceId.equals("local")){
			MediaPlayer media = new MediaPlayer();
			AssetFileDescriptor asset;
			try {
				asset = getAssets().openFd("fale_agora.mp3");
				media.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
				asset.close();
				media.prepare();
				media.start();
			
				media.setOnCompletionListener(AlterarActivity.this);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if(utteranceId.equals("endereço")){
			MediaPlayer media = new MediaPlayer();
			AssetFileDescriptor asset;
			try {
				asset = getAssets().openFd("fale_agora.mp3");
				media.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
				asset.close();
				media.prepare();
				media.start();
			
				media.setOnCompletionListener(AlterarActivity.this);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if(utteranceId.equals("cep")){
			MediaPlayer media = new MediaPlayer();
			AssetFileDescriptor asset;
			try {
				asset = getAssets().openFd("fale_agora.mp3");
				media.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
				asset.close();
				media.prepare();
				media.start();
			
				media.setOnCompletionListener(AlterarActivity.this);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if(utteranceId.equals("localCorrigido") && vezesAudioFalado == 0){
			MediaPlayer media = new MediaPlayer();
			AssetFileDescriptor asset;
			try {
				asset = getAssets().openFd("fale_agora.mp3");
				media.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
				asset.close();
				media.prepare();
				media.start();
			
				media.setOnCompletionListener(AlterarActivity.this);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			vezesAudioFalado++;
		}
		else if(utteranceId.equals("enderecoCorrigido")){
			Log.d("Utterance", "endereço corrigido");
			MediaPlayer media = new MediaPlayer();
			AssetFileDescriptor asset;
			try {
				asset = getAssets().openFd("fale_agora.mp3");
				media.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
				asset.close();
				media.prepare();
				media.start();
			
				media.setOnCompletionListener(AlterarActivity.this);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void callOnUtterance(){
		tts.setOnUtteranceCompletedListener(this);
	}
}
