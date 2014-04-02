package com.br.portablevision.classes;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.location.Address;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class OverlayClass extends Overlay{
	private Address address = null;
	private Bitmap bitmap = null;
	
	public OverlayClass(Address address, Bitmap bitmap) {
		this.address = address;
		this.bitmap = bitmap;
	}
	
	public void draw(Canvas canvas, MapView mapView, boolean shadow){
		Projection projection = mapView.getProjection();
		
		if(!shadow){
			Double lat = address.getLatitude()*1E6;
			Double lng = address.getLongitude()*1E6;
			GeoPoint geoPint = new GeoPoint(lat.intValue(), lng.intValue());
			
			Point point = new Point();
			projection.toPixels(geoPint, point);
			
			Paint paint = new Paint();
			paint.setARGB(250, 255, 255, 255);
			paint.setAntiAlias(true);
			paint.setFakeBoldText(true);
			
			canvas.drawBitmap(bitmap, point.x, point.y, paint);
		}
		super.draw(canvas, mapView, shadow);
	}
	
	public boolean onTap(GeoPoint p, MapView mapView){
		return false;
	}
}
