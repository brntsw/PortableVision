package com.br.portablevision;

import java.io.IOException;
import java.util.ArrayList;

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
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;

public class DescricaoAlterarActivity extends Activity implements OnCompletionListener{
	private String tipoNavegacao;
	private SpeechRecognizer sr;
	private boolean jaFalouDescricao = false;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.descricao_alterar);
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		Button btnVoltar = (Button)findViewById(R.id.btVoltar);
		
		Intent intent = getIntent();
		tipoNavegacao = intent.getStringExtra("navegacao");
		
		if(tipoNavegacao.equals("voz")){
			sr = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());
	        MyRecognition listener = new MyRecognition();
	        sr.setRecognitionListener(listener);
	        
	        if(!jaFalouDescricao){
	        	MediaPlayer mp = new MediaPlayer();
				AssetFileDescriptor asset;
				try {
					asset = getAssets().openFd("fale_novamente.mp3"); //Substituir pelo áudio da descrição alterar
					mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
					asset.close();
					mp.prepare();
					mp.start();
					
					mp.setOnCompletionListener(DescricaoAlterarActivity.this);
				} catch (IOException e) {
					e.printStackTrace();
				}
	        }
			
			btnVoltar.setEnabled(false);
		}
		else{
			btnVoltar.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					finalizaActivity(DescricaoAlterarActivity.this);
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
					
					mp.setOnCompletionListener(DescricaoAlterarActivity.this);
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
					
					mp.setOnCompletionListener(DescricaoAlterarActivity.this);
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
					
					mp.setOnCompletionListener(DescricaoAlterarActivity.this);
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
					
					mp.setOnCompletionListener(DescricaoAlterarActivity.this);
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
					
					mp.setOnCompletionListener(DescricaoAlterarActivity.this);
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
					
					mp.setOnCompletionListener(DescricaoAlterarActivity.this);
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
            
            if(strlist.contains("repetir")){
            	jaFalouDescricao = true;
            	onResume();
            }
            else if(strlist.contains("voltar")){
            	finalizaActivity(DescricaoAlterarActivity.this);
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
					
					mp.setOnCompletionListener(DescricaoAlterarActivity.this);
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
		}

		public void onRmsChanged(float rmsdB) {
			
		}
	}
	
	public void onResume(){
		Log.d("OnResume", "OnResume");
		
		if(jaFalouDescricao){
			MediaPlayer mp = new MediaPlayer();
			AssetFileDescriptor asset;
			try {
				asset = getAssets().openFd("fale_novamente.mp3"); //Substituir pelo áudio da descrição alterar
				mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
				asset.close();
				mp.prepare();
				mp.start();
				
				mp.setOnCompletionListener(DescricaoAlterarActivity.this);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			jaFalouDescricao = false;
		}
		
		super.onResume();
	}
	
	public void onDestroy(){
		super.onDestroy();
	}
	
	private void reconheceVoz(MediaPlayer mp){
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