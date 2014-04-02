package com.br.portablevision;

import java.io.IOException;
import java.util.ArrayList;

import com.br.portablevision.classes.Player;
import com.br.portablevision.classes.Recognizer;
import com.br.portablevision.persistente.DescricaoDAO;


import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class PortableVisionActivity extends Activity implements OnCompletionListener{
	private boolean isAtivo = false;
	private boolean repetir = false;
	private boolean continuar = false;
	private boolean faleOpcao = false;
	
	private int falarOnResume = 0;
    
    Recognizer recognizer = new Recognizer();
    Player player = new Player();
    
    private SpeechRecognizer sr;
    private SpeechRecognizer speechAtivo;
    
    private boolean falarError = false;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        sr = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
        MyRecognition listener = new MyRecognition();
        sr.setRecognitionListener(listener);
        
        DescricaoDAO dao = new DescricaoDAO(PortableVisionActivity.this);
        int valor = dao.consultarAtivo();
        /*Com o valor retornado, se for verdade, tocar o áudio da descrição completa do sistema, quando finalizar, continua normal
        o áudio 'Tela inicial. Fale a opção desejada' */
        if(valor == 0){
        	dao.cadastrarAtivo();
        	//Colocar o áudio da descrição completa do sistema, no final, perguntar o que deseja fazer (repetir ou continuar)
        	MediaPlayer mp = new MediaPlayer();
			AssetFileDescriptor asset;
			try {
				asset = getAssets().openFd("alteracao_sucesso.mp3");
				mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
				asset.close();
				mp.prepare();
				mp.start();
				
				mp.setOnCompletionListener(PortableVisionActivity.this);
			} catch (IOException e) {
				e.printStackTrace();
			}
        	
			int alterou = dao.alterarAtivo(); //altera o valor para 1
			Toast.makeText(PortableVisionActivity.this, "Alterou: " + alterou, Toast.LENGTH_LONG).show();
        }
        else{
        	if(!isAtivo && falarOnResume <= 1){
    	        MediaPlayer mp = new MediaPlayer();
    			AssetFileDescriptor asset;
    			try {
    				asset = getAssets().openFd("opcao_desejada.mp3");
    				mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
    				asset.close();
    				mp.prepare();
    				mp.start();
    				
    				mp.setOnCompletionListener(PortableVisionActivity.this);
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
            }
        }
    }
    
    class MyRecognition implements RecognitionListener{

		public void onBeginningOfSpeech() {
			
		}

		public void onBufferReceived(byte[] buffer) {
			
		}

		public void onEndOfSpeech() {
			if(isAtivo){
				sr.stopListening();
				sr.cancel();
			}
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
					
					mp.setOnCompletionListener(PortableVisionActivity.this);
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
					
					mp.setOnCompletionListener(PortableVisionActivity.this);
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
					
					mp.setOnCompletionListener(PortableVisionActivity.this);
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
					
					mp.setOnCompletionListener(PortableVisionActivity.this);
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
					
					mp.setOnCompletionListener(PortableVisionActivity.this);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else{
				if(!falarError){
					Toast.makeText(PortableVisionActivity.this, "PortableVision: error", Toast.LENGTH_SHORT).show();
					MediaPlayer mp = new MediaPlayer();
					AssetFileDescriptor asset;
					try {
						asset = getAssets().openFd("fale_novamente.mp3");
						mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
						asset.close();
						mp.prepare();
						mp.start();
						
						mp.setOnCompletionListener(PortableVisionActivity.this);
					} catch (IOException e) {
						e.printStackTrace();
					}
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
            
            if(strlist.contains("adicionar") || strlist.contains("adiciona")){
				falarError = true;
            	Intent intent = new Intent(PortableVisionActivity.this, AdicaoActivity.class);
				intent.putExtra("navegacao", "voz");
				startActivity(intent);
			}
            else if(strlist.contains("alterar") || strlist.contains("altera")){
            	falarError = true;
            	Intent intent = new Intent(PortableVisionActivity.this, AlterarActivity.class);
            	intent.putExtra("navegacao", "voz");
            	startActivity(intent);
            }
            else if(strlist.contains("excluir") || strlist.contains("excluir local")){
            	falarError = true;
            	Intent intent = new Intent(PortableVisionActivity.this, ExcluirActivity.class);
            	intent.putExtra("navegacao", "voz");
            	startActivity(intent);
            }
			else if(strlist.contains("procurar") || strlist.contains("procurar local")){
				falarError = true;
				Intent intent = new Intent(PortableVisionActivity.this, ProcurarActivity.class);
				intent.putExtra("navegacao", "voz");
				startActivity(intent);
			}
			else if(strlist.contains("listar") || strlist.contains("listar locais")){
				falarError = true;
				Intent intent = new Intent(PortableVisionActivity.this, ListarActivity.class);
				intent.putExtra("navegacao", "voz");
				startActivity(intent);
			}
			else if(strlist.contains("navegação") || strlist.contains("navegar") || strlist.contains("iniciar")){
				falarError = true;
				Intent intent = new Intent(PortableVisionActivity.this, NavegacaoActivity.class);
				intent.putExtra("navegacao", "voz");
				startActivity(intent);
			}
			else if(strlist.contains("descrição") || strlist.contains("descrição sistema") || strlist.contains("descreve")){
				falarError = true;
				Intent intent = new Intent(PortableVisionActivity.this, DescricaoActivity.class);
				intent.putExtra("navegacao", "voz");
				startActivity(intent);
			}
			else if(strlist.contains("cancelar")){
				isAtivo = true;
				final ImageButton btnInteracao = (ImageButton) findViewById(R.id.btInteracao);
				btnInteracao.setImageDrawable(getResources().getDrawable(R.drawable.interacao_ativa));
				onResume();
			}
			else if(strlist.contains("sim")){
				isAtivo = true;
				ImageButton btnInteracao = (ImageButton) findViewById(R.id.btInteracao);
				btnInteracao.setImageDrawable(getResources().getDrawable(R.drawable.interacao_ativa));
				Toast.makeText(PortableVisionActivity.this, "A interação está ativa", Toast.LENGTH_SHORT).show();
				
				onResume();
			}
			else if(strlist.contains("não")){
				isAtivo = false;
				ImageButton btnInteracao = (ImageButton) findViewById(R.id.btInteracao);
				btnInteracao.setImageDrawable(getResources().getDrawable(R.drawable.interacao_inativa));
				MyRecognition myRecog = new MyRecognition();
				myRecog.onEndOfSpeech();
				
				onRestart();
			}
			else if(strlist.contains("continuar")){
				continuar = true;
				repetir = false;
				onResume();
			}
			else if(strlist.contains("repetir")){
				repetir = true;
				onResume();
			}
			else if(strlist.contains("sair")){
				finalizaActivity(PortableVisionActivity.this);
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
					
					mp.setOnCompletionListener(PortableVisionActivity.this);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		public void onRmsChanged(float rmsdB) {
			
		}
	}
    
    private void reconheceVoz(final MediaPlayer mp){
    	Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "com.br.portablevision");
		intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
		
		if(sr == null){
			Toast.makeText(PortableVisionActivity.this, "PortableVision: reconhece sr", Toast.LENGTH_SHORT).show();
			speechAtivo.startListening(intent);
		}
		else{
			Toast.makeText(PortableVisionActivity.this, "PortableVision: reconhece speech", Toast.LENGTH_SHORT).show();
			sr.startListening(intent);
		}
		
		
		mp.release();
    }
    
    public void finalizaActivity(Activity ac){
    	ac.finish();
    }
    
    public void onResume(){
    	super.onResume();
    	
    	falarOnResume++;
    	
    	Log.d("OnResume", "OnResume");
    	
    	Button btnAdicionar = (Button)findViewById(R.id.btAdicionar);
    	Button btnAlterar = (Button)findViewById(R.id.btAlterar);
    	Button btnExcluir = (Button)findViewById(R.id.btExcluir);
    	Button btnListar = (Button)findViewById(R.id.btListar);
    	Button btnProcurar = (Button)findViewById(R.id.btProcurar);
    	Button btnNavegacao = (Button)findViewById(R.id.btNavegacao);
    	Button btnDescricao = (Button)findViewById(R.id.btDescricao);
    	
    	carregaBotaoInteracao();
    	
    	if(repetir){
    		repetir = false;
    		
    		MediaPlayer mp = new MediaPlayer();
			AssetFileDescriptor asset;
			try {
				asset = getAssets().openFd("alteracao_sucesso.mp3");
				mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
				asset.close();
				mp.prepare();
				mp.start();
				
				mp.setOnCompletionListener(PortableVisionActivity.this);
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	else if(continuar){
    		continuar = false;
    		
    		MediaPlayer mp = new MediaPlayer();
			AssetFileDescriptor asset;
			try {
				asset = getAssets().openFd("opcao_desejada.mp3");
				mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
				asset.close();
				mp.prepare();
				mp.start();
				
				mp.setOnCompletionListener(PortableVisionActivity.this);
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	
    	if(isAtivo){
    		ativaBotoes();
    		
	    	btnAdicionar.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					Intent intent = new Intent(PortableVisionActivity.this, AdicaoActivity.class);
					intent.putExtra("navegacao", "manual");
					startActivity(intent);	
				}
			});
	    	
	    	btnAlterar.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					Intent intent = new Intent(PortableVisionActivity.this, AlterarActivity.class);
					intent.putExtra("navegacao", "manual");
					startActivity(intent);
				}
			});
	    	
	    	btnExcluir.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					Intent intent = new Intent(PortableVisionActivity.this, ExcluirActivity.class);
					intent.putExtra("navegacao", "manual");
					startActivity(intent);
				}
			});
	    	
	    	btnListar.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					Intent intent = new Intent(PortableVisionActivity.this, ListarActivity.class);
					intent.putExtra("navegacao", "manual");
					startActivity(intent);
				}
			});
	    	
	    	btnProcurar.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					Intent intent = new Intent(PortableVisionActivity.this, ProcurarActivity.class);
					intent.putExtra("navegacao", "manual");
					startActivity(intent);
				}
			});
	    	
	    	btnNavegacao.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					Intent intent = new Intent(PortableVisionActivity.this, NavegacaoActivity.class);
					intent.putExtra("navegacao", "manual");
					startActivity(intent);
				}
			});
	    	
	    	btnDescricao.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					Intent intent = new Intent(PortableVisionActivity.this, DescricaoActivity.class);
					intent.putExtra("navegacao", "manual");
					startActivity(intent);
				}
			});
    	}
    	else{
    		desativaBotoes();
    		
    		if(faleOpcao && falarOnResume > 1){
    			MediaPlayer mp = new MediaPlayer();
    			AssetFileDescriptor asset;
    			try {
    				asset = getAssets().openFd("opcao_desejada.mp3");
    				mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
    				asset.close();
    				mp.prepare();
    				mp.start();
    				
    				mp.setOnCompletionListener(PortableVisionActivity.this);
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    		}
    	}
    }
    
    public void onRestart(){
    	super.onRestart();
    	
    	Log.d("OnRestart", "OnRestart");
    	
    	carregaBotaoInteracao();
    	
    	if(!isAtivo){
    		desativaBotoes();
    		
	    	MediaPlayer mp = new MediaPlayer();
			AssetFileDescriptor asset;
			try {
				asset = getAssets().openFd("opcao_desejada.mp3");
				mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
				asset.close();
				mp.prepare();
				mp.start();
				
				mp.setOnCompletionListener(PortableVisionActivity.this);
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	else{
    		ativaBotoes();
    	}
    }
    
    public void onDestroy(){
    	Log.d("OnDestroy", "OnDestroy");
    	if(sr != null){
    		sr.stopListening();
    		sr.cancel();
    		
    		sr = null;
    	}
    	
    	if(speechAtivo != null){
    		speechAtivo.stopListening();
    		speechAtivo.cancel();
    		
    		speechAtivo = null;
    	}
    	super.onDestroy();
    }
    
    public void onCompletion(final MediaPlayer mp) {
    	reconheceVoz(mp);
	}
    
    private void carregaBotaoInteracao(){
    	final ImageButton btnInteracao = (ImageButton) findViewById(R.id.btInteracao);
    	btnInteracao.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				if(!isAtivo){
					falarError = true;
					
					speechAtivo = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
			        MyRecognition listener = new MyRecognition();
			        speechAtivo.setRecognitionListener(listener);
					
					if(sr != null){
						sr.stopListening();
				        sr.cancel();
				        sr = null;
					}
			        
			        MediaPlayer mp = new MediaPlayer();
					AssetFileDescriptor asset;
					try {
						asset = getAssets().openFd("navegacao_toque.mp3");
						mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
						asset.close();
						mp.prepare();
						mp.start();
						
						mp.setOnCompletionListener(PortableVisionActivity.this);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				else{
					isAtivo = false;
					btnInteracao.setImageDrawable(getResources().getDrawable(R.drawable.interacao_inativa));
					Toast.makeText(PortableVisionActivity.this, "A interação está inativa", Toast.LENGTH_SHORT).show();
					MyRecognition myRecog = new MyRecognition();
					myRecog.onEndOfSpeech();
					
					onRestart();
				}
			}
		});
    }
    
    private void desativaBotoes(){
    	Button btnAdicionar = (Button)findViewById(R.id.btAdicionar);
    	Button btnAlterar = (Button)findViewById(R.id.btAlterar);
    	Button btnListar = (Button)findViewById(R.id.btListar);
    	Button btnProcurar = (Button)findViewById(R.id.btProcurar);
    	Button btnNavegacao = (Button)findViewById(R.id.btNavegacao);
    	Button btnDescricao = (Button)findViewById(R.id.btDescricao);
    	
    	btnAdicionar.setClickable(false);
    	btnAlterar.setClickable(false);
    	btnListar.setClickable(false);
    	btnProcurar.setClickable(false);
    	btnNavegacao.setClickable(false);
    	btnDescricao.setClickable(false);
    }
    
    private void ativaBotoes(){
    	Button btnAdicionar = (Button)findViewById(R.id.btAdicionar);
    	Button btnAlterar = (Button)findViewById(R.id.btAlterar);
    	Button btnListar = (Button)findViewById(R.id.btListar);
    	Button btnProcurar = (Button)findViewById(R.id.btProcurar);
    	Button btnNavegacao = (Button)findViewById(R.id.btNavegacao);
    	Button btnDescricao = (Button)findViewById(R.id.btDescricao);
    	
    	btnAdicionar.setClickable(true);
    	btnAlterar.setClickable(true);
    	btnListar.setClickable(true);
    	btnProcurar.setClickable(true);
    	btnNavegacao.setClickable(true);
    	btnDescricao.setClickable(true);
    }
}