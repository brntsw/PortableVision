package com.br.portablevision;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;
import com.google.android.maps.MapActivity;


public class GpsActivity extends MapActivity{
	private Geocoder geo;
	
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gps);
		
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		geo = new Geocoder(getApplicationContext(), Locale.getDefault());
		Address address = null;
		try {
			Intent intent = getIntent();
			String endereco = intent.getStringExtra("endereco");
			List<Address> enderecos = geo.getFromLocationName(endereco, 1);
			if(enderecos.size() > 0){
				address = enderecos.get(0);
				if(address != null){
					Double lat = address.getLatitude();
					Double lng = address.getLongitude();
					
					Log.d("Endereço", "" + address);
					
					Intent intentNavegacao = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("google.navigation:ll=" + lat.floatValue() + "," + lng.floatValue() + "&mode=w"));
					startActivity(intentNavegacao);
				}
				else{
					Toast.makeText(GpsActivity.this, "O endereço não é válido", Toast.LENGTH_LONG).show();
				}
			}
		} catch (IOException e) {
			Toast.makeText(GpsActivity.this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		return true;
	}
}
