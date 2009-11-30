package com.twidroidplugins.maps;


import java.util.List;

import android.net.Uri;
import android.os.Handler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

public class LocationDialog extends MapActivity { 

	Context ctx;
	MapView mapView;
	MyLocationOverlay myLocationOverlay;
	Handler mHandler;


	@Override 
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		ctx=getBaseContext();
		setContentView(R.layout.dialog_map); 
		setTitle(ctx.getText(R.string.activity_title)); 
		this.setTitleColor(R.color.text_color);

		mHandler = new Handler();

		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);

		final MapController mapController =  mapView.getController();

		mapController.setZoom(15);
		List<Overlay> overlays = mapView.getOverlays();
		myLocationOverlay = new 
		MyLocationOverlay(ctx,mapView);
		overlays.add(myLocationOverlay);

		Button button1 = (Button) findViewById(R.id.buttonRefresh);
		button1.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				myLocationOverlay.enableMyLocation();
			}});


		Button insertMapLink = (Button) findViewById(R.id.buttonInsertMaplink);
		insertMapLink.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				if (myLocationOverlay.getLastFix()!=null)
				{
					myLocationOverlay.disableMyLocation();
					Intent returnData = new Intent();
					
					/* 
					 * Send data back to Twidroid					
					 *
				     *  Supported:
					 *	Intent.EXTRA_TEXT use to return text to Twidroid
					 *	setData to return a URL
					 *	Extra Strings: "latitude", "longitude" to return location information				
					 *
					 */
					
					returnData.setData(Uri.parse("http://maps.google.com?q=" + myLocationOverlay.getLastFix().getLatitude()+ "," +myLocationOverlay.getLastFix().getLongitude() ));

					returnData.putExtra("latitude", myLocationOverlay.getLastFix().getLatitude());
					returnData.putExtra("longitude", myLocationOverlay.getLastFix().getLongitude());

					setResult(RESULT_OK, returnData);
					finish();
				}
				else
				{
					Toast.makeText(LocationDialog.this, getText(R.string.error_nolocationfix), Toast.LENGTH_LONG);
				}
			}});

		Button annotateTweet = (Button) findViewById(R.id.buttonAnnotate);
		annotateTweet.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				if (myLocationOverlay.getLastFix()!=null)
				{
					myLocationOverlay.disableMyLocation();
					Intent returnData = new Intent();

					returnData.putExtra("latitude", myLocationOverlay.getLastFix().getLatitude());
					returnData.putExtra("longitude", myLocationOverlay.getLastFix().getLongitude());

					setResult(RESULT_OK, returnData);
					finish();
				}
				else
				{
					Toast.makeText(LocationDialog.this, getText(R.string.error_nolocationfix), Toast.LENGTH_LONG);
				}
			}});



		myLocationOverlay.runOnFirstFix(new Runnable() {
			public void run() {
				mHandler.post(new Runnable(){

					public void run() {
						mapController.animateTo(myLocationOverlay.getMyLocation());
					}

				});	

			}
		});
	}

	@Override 
	public void onStart() { 

		super.onStart();
	}

	public void onPause() {
		super.onPause();
		myLocationOverlay.disableMyLocation();
	}

	public void onStop() {
		super.onStop();
	}

	public void onResume() {
		super.onResume();
		myLocationOverlay.enableMyLocation();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}



}
