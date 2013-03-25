package com.example.trabajoais;

import java.util.ArrayList;

import com.example.trabajoais.Scratch.Timer;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class Investigacion extends Activity implements OnTouchListener, Runnable {

	// UI attributes	
		public static ImageView drawingArea;
		
		// Layer attributes
		private ArrayList<Bitmap> layers; // Pila de Bitmaps que seran las capas de la app siendo la ultima la resolucion
		private int numLayers;
		private int[][] logicLayer;
		
		// Scratch attributes
		private int[] bufferPixels;
		public static int pixelGray;
		private Timer timer;
		
		//public static Bitmap imageToErase;
		//public static Bitmap imageBackground;
		//private int stride= 30;
		
		
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);		
			requestWindowFeature(Window.FEATURE_NO_TITLE); // FullScreen
	        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
			setContentView(R.layout.activity_investigacion);
			
			// Get the reference to the UI elements
			drawingArea= (ImageView)findViewById(R.id.viewDrawInvestigacion);
			drawingArea.post(this);
			
			// Initialize all the attributes of the layers
			layers= new ArrayList<Bitmap>();
			numLayers= getIntent().getExtras().getInt(MainActivity.N_LAYERS_KEY);
		}
		
		
		protected void onDestroy()
		{
			super.onDestroy();
			
			// Destroy all the used bitmaps
			for(int i=0; i<layers.size(); ++i)
				layers.get(i).recycle();
			
			// Clean the ArrayList object
			//layers.clear();
			
			// Check if the timer is still working to stop it
			//if( timer.hasMessages(1) )
				//timer.removeMessages(1);
		}

		/*@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			// Inflate the menu; this adds items to the action bar if it is present.
			getMenuInflater().inflate(R.menu.inicio, menu);
			return true;
		}*/

		@Override
		public boolean onTouch(View v, MotionEvent event)
		{
			// Check if there is only one finger
			if( event.getPointerCount() == 1 )
			{
				// Check the correct action
				if( (event.getAction() == MotionEvent.ACTION_MOVE) || (event.getAction() == MotionEvent.ACTION_DOWN) )
				{
					// Make the scratch effect 
					try
					{
						//imageBackground.getPixels(pixels, 0, imageBackground.getWidth(), (int)event.getX(), (int)event.getY(), stride, stride);				
						//imageToErase.setPixels(pixels, 0, imageToErase.getWidth(), (int)event.getX(), (int)event.getY(), stride, stride);				
						//drawingArea.setImageBitmap(imageToErase);
						
					}catch(Exception ex){}
				}
			}
				
			return true;
					
					
					
		}

		@Override
		public void run() {

			Log.d("IMAGEVIEW", "[" + drawingArea.getWidth() + ", " + drawingArea.getHeight() + "]");
			
			// Create the stack of layers
			for(int i=0; i<numLayers; ++i)
			{
				layers.add(Bitmap.createBitmap(drawingArea.getWidth(), drawingArea.getHeight(), Bitmap.Config.RGB_565));
				if( i%2 == 0)
					layers.get(i).eraseColor(Color.GRAY);
				else
					layers.get(i).eraseColor(Color.CYAN);
			}
			
			// Initialize the logical matrix which it tells us the order of the layers
			logicLayer= new int[drawingArea.getWidth()][drawingArea.getHeight()];
			for(int i=0; i<drawingArea.getWidth(); ++i)
				for(int j=0; j<drawingArea.getHeight(); ++j)
					logicLayer[i][j]= 0;
			
			// Set the first grey image to scratch
			drawingArea.setImageBitmap(layers.get(0));
			drawingArea.setOnTouchListener(this);
			//pixelGray= imageToErase.getPixel(0, 0);
			
			// Get the image which will be shown after the scratched image
			layers.add( BitmapFactory.decodeResource(getResources(), R.drawable.ganar) );
			layers.set(layers.size()-1, Bitmap.createScaledBitmap(layers.get(layers.size()-1), drawingArea.getWidth(), drawingArea.getHeight(), false));
			
			// Create the buffer which it will be used to do the scratch effect
			bufferPixels= new int[drawingArea.getWidth() * drawingArea.getHeight()];
			
			// Initialize the timer
			//timer= new Timer();
			//timer.sendEmptyMessageDelayed(1, 2000);
		}
		
		
		static class Timer extends Handler
		{
			private float totalPixel;
			private float count;
			private float percent;
			
			public Timer()
			{
				//totalPixel= Inicio.imageToErase.getWidth() * Inicio.imageToErase.getHeight();
			}
			
			public void handleMessage(Message msg)
			{
				super.handleMessage(msg);
				
				count= 0;
				//for(int i=0; i<Inicio.imageToErase.getWidth(); ++i)
				//	for(int j=0; j<Inicio.imageToErase.getHeight(); ++j)
					//	if( Inicio.imageToErase.getPixel(i, j) == Inicio.pixelGray )
						//	++count;
				
				percent= (count/totalPixel)*100;
				
				if( (percent > 0) && (percent <= 50) )
				{
					// Release the background image
					//Inicio.drawingArea.setImageBitmap(imageBackground);
					Scratch.drawingArea.setOnTouchListener(null);
				}
				else
					this.sendEmptyMessageDelayed(1, 2000);
			}
		};
}
