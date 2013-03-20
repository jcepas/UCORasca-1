package com.example.trabajoais;

import java.io.File;
import java.util.ArrayList;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.hardware.Camera.Size;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Toast;

public class Inicio extends Activity implements OnTouchListener, Runnable {

	// UI attributes	
	public static ImageView drawingArea;
	
	// Layer attributes
	private ArrayList<Bitmap> layers; // Pila de Bitmaps que seran las capas de la app siendo la ultima la resolucion
	private int numLayers;
	private int[][] logicLayer;
	private int limitX;
	private int limitY;
	private int logicX;
	private int logicY;
	private int lastX;
	private int lastY;
	
	// Scratch attributes
	private int[] bufferPixels;
	public static int pixelGray;
	private Timer timer;
	
	private int stride= 8;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		requestWindowFeature(Window.FEATURE_NO_TITLE); // FullScreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_inicio);
		
		// Get the reference to the UI elements
		drawingArea= (ImageView)findViewById(R.id.viewDraw);
		drawingArea.post(this);
		
		// Initialize all the attributes of the layers
		layers= new ArrayList<Bitmap>();
		numLayers= getIntent().getExtras().getInt(MainActivity.N_LAYERS_KEY);
		Log.d("NUM LAYER", String.valueOf(numLayers));
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
		//f( timer.hasMessages(1) )
		//	timer.removeMessages(1);
	}

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.inicio, menu);
		return true;
	}*/
	
	public void initialize(boolean releaseMemory)
	{
		// Check if it is necessary to release memory
		if( releaseMemory )
		{
			// Destroy all the layers
			for(int i=0; i<layers.size(); ++i)
				layers.get(i).recycle();
			layers.clear();
		}
		
		// Create the stack of layers requested
		for(int i=0; i<numLayers; ++i)
		{
			layers.add(Bitmap.createBitmap(drawingArea.getWidth(), drawingArea.getHeight(), Bitmap.Config.RGB_565));
			if( i%2 == 0)
				layers.get(i).eraseColor(Color.GRAY);
			else
				layers.get(i).eraseColor(Color.CYAN);
		}
		
		// Get the image which will be shown after the scratched
		layers.add( BitmapFactory.decodeResource(getResources(), R.drawable.ganar) );
		layers.set(layers.size()-1, Bitmap.createScaledBitmap(layers.get(layers.size()-1), drawingArea.getWidth(), drawingArea.getHeight(), false));
		
		// Set the first grey image to scratch
		drawingArea.setImageBitmap(layers.get(0));
		drawingArea.setOnTouchListener(this);
		
		// Initialize the logical matrix which it tells us the order of the layers
		lastX= -1;
		lastY= -1;
		
		limitX= drawingArea.getWidth()/stride;
		limitY= drawingArea.getHeight()/stride;
		
		logicLayer= new int[limitX][limitY];
		for(int i=0; i<limitX; ++i)
			for(int j=0; j<limitY; ++j)
				logicLayer[i][j]= 1;
		
		// Create the buffer which it will be used to do the scratch effect
		bufferPixels= new int[drawingArea.getWidth() * drawingArea.getHeight()];
	}
	
	public void scratch(int x, int y, int radius)
	{
		// Get the logic coordinates 
		logicX= x/stride;
		logicY= y/stride;
		
		
		// Check if the user are scratching the same zone
		if( ((lastX == -1) && (lastY == -1)) || ((lastX != logicX) && (lastY != logicY)) )
		{
			// Update the last logic coordinates
			lastX= logicX;
			lastY= logicY;
			
			/*if( logicLayer[logicX][logicY] != layers.size() )
			{
				layers.get(logicLayer[logicX][logicY]).getPixels(bufferPixels, 0, layers.get(0).getWidth(), logicX*stride, logicY*stride, stride, stride);
				layers.get(0).setPixels(bufferPixels, 0, layers.get(0).getWidth(), logicX*stride, logicY*stride, stride, stride);
				drawingArea.setImageBitmap(layers.get(0));
				
				logicLayer[logicX][logicY]+=1;
			}*/
			
			// Generate the indicated scratch effect
			for(int i=logicX-radius; i<logicX+radius; ++i)
				for(int j=logicY-radius; j<logicY+radius; ++j)
				{
					try
					{
						if( ((i == logicX) && (j == logicY)) || (logicLayer[i][j] == logicLayer[logicX][logicY]) )
							if( logicLayer[i][j] != layers.size() )
							{
								layers.get(logicLayer[i][j]).getPixels(bufferPixels, 0, layers.get(0).getWidth(), i*stride, j*stride, stride, stride);
								layers.get(0).setPixels(bufferPixels, 0, layers.get(0).getWidth(), i*stride, j*stride, stride, stride);
								drawingArea.setImageBitmap(layers.get(0));
								
								logicLayer[i][j]+=1;
							}
					}catch(Exception e){}
				}
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		// Check if there is only one finger
		if( event.getPointerCount() == 1 )
		{
			// Check the correct action
			if( (event.getAction() == MotionEvent.ACTION_MOVE) || (event.getAction() == MotionEvent.ACTION_DOWN) )
				scratch((int)event.getX(), (int)event.getY(), 1);
			else
			{
				lastX= -1;
				lastY= -1;
			}
		}
			
		return true;			
	}

	@Override
	public void run() {

		Log.d("IMAGEVIEW", "[" + drawingArea.getWidth() + ", " + drawingArea.getHeight() + "]");		
		initialize(false);
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
				Inicio.drawingArea.setOnTouchListener(null);
			}
			else
				this.sendEmptyMessageDelayed(1, 2000);
		}
	};
}
