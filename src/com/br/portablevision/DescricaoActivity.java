package com.br.portablevision;

import java.io.IOException;
import java.util.ArrayList;

import com.br.portablevision.R;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;

public class DescricaoActivity extends Activity implements OnCompletionListener{
	private SpeechRecognizer sr;
	private String tipoNavegacao;
	
	private int falarOnCreate = 0;
	private int falarOnResume = 0;
	
	private Button btnDescricaoAdicionar;
	private Button btnDescricaoAlterar;
	private Button btnDescricaoDeletar;
	private Button btnDescricaoProcurar;
	private Button btnDescricaoListar;
	private Button btnDescricaoNavegacao;
	private Button btnVoltar;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.descricao);
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		//Carrega os botões pelo xml
		carregaBotoes();
		
		Intent intent = getIntent();
		tipoNavegacao = intent.getStringExtra("navegacao");
		
		falarOnCreate++;
		
		if(tipoNavegacao.equals("voz")){
			sr = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
	        MyRecognition listener = new MyRecognition();
	        sr.setRecognitionListener(listener);
	        
	        MediaPlayer mp = new MediaPlayer();
			AssetFileDescriptor asset;
			try {
				asset = getAssets().openFd("opcao_desejada.mp3");
				mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
				asset.close();
				mp.prepare();
				mp.start();
				
				mp.setOnCompletionListener(DescricaoActivity.this);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			btnDescricaoAdicionar.setClickable(false);
	    	btnDescricaoAlterar.setClickable(false);
	    	btnDescricaoDeletar.setClickable(false);
	    	btnDescricaoListar.setClickable(false);
	    	btnDescricaoProcurar.setClickable(false);
	    	btnDescricaoNavegacao.setClickable(false);
	    	btnVoltar.setClickable(false);
		}
		else{
			btnDescricaoAdicionar.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					Intent intent = new Intent(DescricaoActivity.this, DescricaoAdicionarActivity.class);
					intent.putExtra("navegacao", "manual");
					startActivity(intent);
				}
			});
			
			btnDescricaoAlterar.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					Intent intent = new Intent(DescricaoActivity.this, DescricaoAlterarActivity.class);
					intent.putExtra("navegacao", "manual");
					startActivity(intent);
				}
			});
			
			btnDescricaoDeletar.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					Intent intent = new Intent(DescricaoActivity.this, DescricaoDeletarActivity.class);
					intent.putExtra("navegacao", "manual");
					startActivity(intent);
				}
			});
			
			btnDescricaoProcurar.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					Intent intent = new Intent(DescricaoActivity.this, DescricaoProcurarActivity.class);
					intent.putExtra("navegacao", "manual");
					startActivity(intent);
				}
			});
			
			btnDescricaoListar.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					Intent intent = new Intent(DescricaoActivity.this, DescricaoListarActivity.class);
					intent.putExtra("navegacao", "manual");
					startActivity(intent);
				}
			});
			
			btnDescricaoNavegacao.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					Intent intent = new Intent(DescricaoActivity.this, DescricaoNavegacaoActivity.class);
					intent.putExtra("navegacao", "manual");
					startActivity(intent);
				}
			});
			
			btnVoltar.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					finalizaActivity(DescricaoActivity.this);
				}
			});
		}
	}
	
	class MyRecognition implements RecognitionListener{

		public void onBeginningOfSpeech() {
			// TODO Auto-generated method stub
			
		}

		public void onBufferReceived(byte[] buffer) {
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
					
					mp.setOnCompletionListener(DescricaoActivity.this);
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
					
					mp.setOnCompletionListener(DescricaoActivity.this);
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
					
					mp.setOnCompletionListener(DescricaoActivity.this);
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
					
					mp.setOnCompletionListener(DescricaoActivity.this);
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
					
					mp.setOnCompletionListener(DescricaoActivity.this);
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
					
					mp.setOnCompletionListener(DescricaoActivity.this);
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
            
            if(strlist.contains("descrição adicionar") || strlist.contains("adicionar")){
				Intent intent = new Intent(DescricaoActivity.this, DescricaoAdicionarActivity.class);
				intent.putExtra("navegacao", "voz");
				startActivity(intent);
			}
            else if(strlist.contains("descrição alterar") || strlist.contains("alterar")){
            	Intent intent = new Intent(DescricaoActivity.this, DescricaoAlterarActivity.class);
            	intent.putExtra("navegacao", "voz");
            	startActivity(intent);
            }
            else if(strlist.contains("descrição excluir") || strlist.contains("excluir") || strlist.contains("deletar")){
            	Intent intent = new Intent(DescricaoActivity.this, DescricaoDeletarActivity.class);
            	intent.putExtra("navegacao", "voz");
            	startActivity(intent);
            }
			else if(strlist.contains("descrição procurar") || strlist.contains("procurar")){
				Intent intent = new Intent(DescricaoActivity.this, DescricaoProcurarActivity.class);
				intent.putExtra("navegacao", "voz");
				startActivity(intent);
			}
			else if(strlist.contains("descrição listar") || strlist.contains("listar")){
				Intent intent = new Intent(DescricaoActivity.this, DescricaoListarActivity.class);
				intent.putExtra("navegacao", "voz");
				startActivity(intent);
			}
			else if(strlist.contains("descrição navegação") || strlist.contains("navegação") || strlist.contains("navegar")){
				Intent intent = new Intent(DescricaoActivity.this, DescricaoNavegacaoActivity.class);
				intent.putExtra("navegacao", "voz");
				startActivity(intent);
			}
			else if(strlist.contains("voltar")){
				finalizaActivity(DescricaoActivity.this);
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
					
					mp.setOnCompletionListener(DescricaoActivity.this);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		public void onRmsChanged(float rmsdB) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public void onResume(){
		super.onResume();
		
		falarOnResume++;
		
		if(tipoNavegacao.equals("voz") && falarOnResume > 1 && falarOnCreate <= 1){
			MediaPlayer mp = new MediaPlayer();
			AssetFileDescriptor asset;
			try {
				asset = getAssets().openFd("opcao_desejada.mp3");
				mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
				asset.close();
				mp.prepare();
				mp.start();
				
				mp.setOnCompletionListener(DescricaoActivity.this);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void finalizaActivity(Activity ac){
		ac.finish();
	}

	private void reconheceVoz(final MediaPlayer mp){
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
	
	public void carregaBotoes(){
		btnDescricaoAdicionar = (Button)findViewById(R.id.btDescricaoAdicionar);
		btnDescricaoAlterar = (Button)findViewById(R.id.btDescricaoAlterar);
		btnDescricaoDeletar = (Button)findViewById(R.id.btDescricaoDeletar);
		btnDescricaoListar = (Button)findViewById(R.id.btDescricaoListar);
		btnDescricaoProcurar = (Button)findViewById(R.id.btDescricaoProcurar);
		btnDescricaoNavegacao = (Button)findViewById(R.id.btDescricaoNavegacao);
		btnVoltar = (Button)findViewById(R.id.btVoltar);
	}
}
