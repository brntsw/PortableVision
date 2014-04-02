package com.br.portablevision.classes;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

public class Player extends Activity{
	public Player(){}
	
	public MediaPlayer iniciarPlayer(String nome, Context ctx){
		MediaPlayer mp = new MediaPlayer();
		AssetFileDescriptor asset;
		try {
			asset = getAssets().openFd(nome);
			mp.setDataSource(asset.getFileDescriptor(), asset.getStartOffset(), asset.getLength());
			asset.close();
			mp.prepare();
			mp.start();
			
			mp.setOnCompletionListener((OnCompletionListener) ctx);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return mp;
	}
}
