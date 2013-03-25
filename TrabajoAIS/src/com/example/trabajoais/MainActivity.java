package com.example.trabajoais;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;

public class MainActivity extends Activity {
	
	private Intent startSection;
	private boolean launchDialog = true;
	private int n_layers = 1;
	public static String N_LAYERS_KEY= "n_layers";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (launchDialog) {
			launchDialog = false;
			showLayerDialog();
		}
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch( item.getItemId() )
		{
			case R.id.action_begin:
				startSection = new Intent(this, Investigacion.class);
				startSection.putExtra(N_LAYERS_KEY, 1);
				startActivity(startSection);
				break;
									
			case R.id.action_about: 
				startSection = new Intent(this, About.class);
				startActivity(startSection);
				break;
									
			case R.id.action_exit:
				finish();
				break;
		}
		
		return true;
	}
	
	public boolean showLayerDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.layer_selection);
		builder.setPositiveButton(R.string.alert_ok, new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	   launchDialog = true;
	        	   dialog.dismiss();
	               startGame();
	           }
	    });
		builder.setNegativeButton(R.string.alert_cancel, new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	   launchDialog = true;
	               dialog.cancel();
	           }
	    });
		builder.setSingleChoiceItems(R.array.layers, 0, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				n_layers = which + 1;
			}
		});
		
		builder.create().show();
		return true;
	}
	
	public boolean startGame() {
		startSection = new Intent(this, Scratch.class);
		startSection.putExtra(N_LAYERS_KEY, n_layers);
		startActivity(startSection);
		return true;
	}
}
