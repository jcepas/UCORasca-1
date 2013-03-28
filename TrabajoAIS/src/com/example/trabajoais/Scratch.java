package com.example.trabajoais;

import java.util.ArrayList;
import java.util.Vector;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;
//import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.Toast;


public class Scratch extends Activity implements OnTouchListener, Runnable {

	// UI attributes	
	public static ImageView drawingArea;
	
	// Layer attributes
	private ArrayList<Bitmap> layers;
	public static ImageSolution solution;
	public static int numLayers;
	
	private Vector<Integer> dividers;
	private int stride;
	public static int[][] logicLayer;
	private int limitX;
	private int limitY;
	private int logicX;
	private int logicY;
	private int lastX;
	private int lastY;
	
	// Scratch attributes
	private int[] bufferPixels;
	private Timer timer;
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		requestWindowFeature(Window.FEATURE_NO_TITLE); // FullScreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_scratch);
		
		// Get the reference to the UI elements
		drawingArea= (ImageView)findViewById(R.id.viewDraw);
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
		//f( timer.hasMessages(1) )
		//	timer.removeMessages(1);
	}

	
	
	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.inicio, menu);
		return true;
	}*/
	
	
	
	/**
	 * Method which calculates the divisors of a number
	 * 
	 * @param n: Number to calculate its divisors
	 * 
	 * @return div: Vector containing all integer divisors of the number
	 * 
	 * @date 19/03/2013
	 * @version 1.0
	 * @author Alberto Jimenez Lopez
	 */
	public Vector<Integer> dividers(int n) {
		Vector<Integer> div = new Vector<Integer>();

		for (int i = 2; i <= n; i++) {
			if (n % i == 0)
				div.addElement(i);
		}

		return div;
	}

	
	
	/**
	 * Method which calculates the minimum Common divisors from two arrays of dividers
	 * 
	 * @param a: Number (int)
	 * @param b: Number (int)
	 * 
	 * @return min: Integer containing the minimum common divider
	 * 
	 * @date 19/03/2013
	 * @version 1.0
	 * @author Alberto Jimenez Lopez (Modified by Manuel Flores Arribas on 25/03/2013)
	 */
	public Vector<Integer> vectorCommonDivider(int number1, int number2) {
		
		Vector<Integer> a= dividers(number1);
		Vector<Integer> b= dividers(number2);
		Vector<Integer> common = new Vector<Integer>();

		if (a.size() < b.size()) {
			for (int i = 0; i < a.size(); i++)
				for (int j = 0; j < b.size(); j++)
					if (a.elementAt(i) == b.elementAt(j))
						common.addElement(a.elementAt(i));
		} else {
			for (int i = 0; i < b.size(); i++)
				for (int j = 0; j < a.size(); j++)
					if (b.elementAt(i) == a.elementAt(j))
						common.addElement(b.elementAt(i));
		}

		//Log.d(""+number1, a.toString());
		//Log.d(""+number2, b.toString());
		Log.d("COMUN", common.toString());
		return common;
	}
	
	
	
	/**
     * 	Method which it initializes all the layers that the user has indicated
     * 
     *	@param		releaseMemory: Flag which it indicates if it is needed release the memory or not (boolean)
     * 
     * 	@date		18/03/2013
     * 	@version	1.0
     * 	@author		Manuel Flores Arribas
     */
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
		dividers= vectorCommonDivider(drawingArea.getWidth(), drawingArea.getHeight());
		stride= dividers.get( dividers.size()/2 );
		Log.d("STRIDE", ""+stride);
		
		solution= new ImageSolution(BitmapFactory.decodeResource(getResources(), R.drawable.ganar), stride, new Point(0,0), 690, 650);
		solution.scale(drawingArea.getWidth(), drawingArea.getHeight());
		layers.add(solution.getImage());
		
		// Set the first grey image to scratch
		drawingArea.setImageBitmap(layers.get(0));
		drawingArea.setOnTouchListener(this);
		
		// Initialize the logical matrix which it tells us the order of the layers
		lastX= -1;
		lastY= -1;
		
		limitX= drawingArea.getWidth()/stride;
		limitY= drawingArea.getHeight()/stride;
		
		//limitX= drawingArea.getHeight()/stride;
		//limitY= drawingArea.getWidth()/stride;
		
		Log.d("LOGIC MATRIX", "[" + limitX + ", " + limitY + "]");
		Log.d("SCREEN", "[" + drawingArea.getHeight() + ", " + drawingArea.getWidth() + "]");
		
		logicLayer= new int[limitX][limitY];
		for(int i=0; i<limitX; ++i)
			for(int j=0; j<limitY; ++j)
				logicLayer[i][j]= 1;
		
		// Create the buffer which it will be used to do the scratch effect
		bufferPixels= new int[drawingArea.getWidth() * drawingArea.getHeight()];
		
		timer= new Timer();
		//timer.sendEmptyMessageDelayed(1, 2 * 1000);
	}
	
	
	
	/**
     * 	Method which it generates the scratch effect
     * 
     *	@param		x: Coordinate X of the image pixel to scratch (int)
     *	@param		y: Coordinate Y of the image pixel to scratch (int)
     *	@param		radius: Radius of the scratch effect (int)
     * 
     * 	@date		18/03/2013
     * 	@version	1.0
     * 	@author		Manuel Flores Arribas
     */
	public void scratch(int x, int y, int radius)
	{
		// Get the logic coordinates 
		logicX= x/stride;
		logicY= y/stride;
		
		if( (lastX == -1) && (lastY == -1) )
		{
			lastX= logicX;
			lastY= logicY;
			
			for(int i=logicX-radius; i<logicX+radius; ++i)
			{
				for(int j=logicY-radius; j<logicY+radius; ++j)
				{
					try
					{
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
		else
		{
			int distancia;
			
			distancia= (int) Math.sqrt( Math.pow((logicX-lastX), 2) + Math.pow((logicY-lastY), 2) );
			
			if( distancia >= ((radius)*2) )
			{
				lastX= logicX;
				lastY= logicY;
				
				for(int i=logicX-radius; i<logicX+radius; ++i)
				{
					for(int j=logicY-radius; j<logicY+radius; ++j)
					{
						try
						{
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
		}
		
		// Check if the user are scratching the same zone
		/*if( ((lastX == -1) && (lastY == -1)) || ((lastX != logicX) && (lastY != logicY)) )
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
			}*
			
			int numLayer= logicLayer[logicX][logicY];
			
			// Generate the indicated scratch effect
			for(int i=logicX-radius; i<logicX+radius; ++i)
			{
				for(int j=logicY-radius; j<logicY+radius; ++j)
				{
					try
					{
						if( ((i == logicX) && (j == logicY)) || (logicLayer[i][j] == logicLayer[logicX][logicY]) )
						{
							if( logicLayer[i][j] != layers.size() )
							{
								layers.get(logicLayer[i][j]).getPixels(bufferPixels, 0, layers.get(0).getWidth(), i*stride, j*stride, stride, stride);
								layers.get(0).setPixels(bufferPixels, 0, layers.get(0).getWidth(), i*stride, j*stride, stride, stride);
								drawingArea.setImageBitmap(layers.get(0));
								
								logicLayer[i][j]+=1;
							}
							else
								Log.d("SCRATCH", "NO ENTRA 2�");
						}
						else
							Log.d("SCRATCH", "NO ENTRA 1�");
					}catch(Exception e){e.printStackTrace();}
				}
			}
		}*/
	}
	
	
	
	/**
     * 	Method which it returns the logical coordinates of the given point at the same given object
     * 
     *	@param		p: Point represented by the Point class of which we want to get its logic coordinates (Point)
     * 
     * 	@date		25/03/2013
     * 	@version	1.0
     * 	@author		Manuel Flores Arribas
     */
	/*public void real2logic(Point p)
	{
		p.x= p.x/stride;
		p.y= p.y/stride;
		//return new Point(p.x/stride, p.y/stride);
	}*/
	

	
	/**
     * 	Overwrite the method onTouch of the Android library to indicate the actions to do when the user touches the screen
     * 
     * 	@param		v: View object which it sends the signal
     *  @param		event: MotionEvent object with all the information about the signal
     *  
     *  @return		true because we want to pick the signal
     * 
     * 	@date		10/03/2013
     * 	@version	1.0
     * 	@author		Manuel Flores Arribas
     */
	public boolean onTouch(View v, MotionEvent event)
	{
		// Check if there is only one finger
		if( event.getPointerCount() == 1 )
		{			
			// Check the correct action
			if( (event.getAction() == MotionEvent.ACTION_MOVE) || (event.getAction() == MotionEvent.ACTION_DOWN) )
			{
				scratch((int)event.getX(), (int)event.getY(), 3);
				
				//Log.d("POINTER", "" + (int)event.getX() + ", " + (int)event.getY());
			}
			else
			{
				lastX= -1;
				lastY= -1;
			}
		}
			
		return true;			
	}

	
	
	/**
     * 	Overwrite the method run of the Runnable class to indicate the actions to do when the scratch UI is shown
     * 
     * 	@date		20/03/2013
     * 	@version	1.0
     * 	@author		Manuel Flores Arribas
     */
	public void run() {

		// Initialize the user interface		
		initialize(false);
	}
	
	
	/** Class to check the percentage completed of interest area
	 * 
	 *  @date		25/03/2013
     * 	@version	1.0
     * 	@author		Manuel Navarro Perez, Francisco Aranda Garcia
	 */
	static class Timer extends Handler
	{
		private Point logicPoint;
		private int logicWidth;
		private int logicHeight;
		
		private float count;
		private float percent;
		
		public Timer()
		{
			logicPoint= Scratch.solution.getInterestingLogicPoint();
			logicWidth= Scratch.solution.getInterestingLogicWidth();
			logicHeight= Scratch.solution.getInterestingLogicHeight();
		}
		
		/**
	     * 	Method which check the interest area
	     * 
	     *	@param		msg: Defines a message containing a description and arbitrary data object that can be sent to a Handler (Message)
	     * 
	     * 	@date		25/03/2013
	     * 	@version	1.0
	     * 	@author		Manuel Navarro Perez, Francisco Aranda Garcia
	     */
		public void handleMessage(Message msg) // Metodo a sobrecargar
		{
			super.handleMessage(msg);
			
			count = 0;
			
			//Check the completed percentage
			for (int i = logicPoint.x; i<(logicWidth + logicPoint.x); i++)
				for (int j = logicPoint.y; j<(logicHeight + logicPoint.y); j++)
					if(Scratch.logicLayer[i][j] == (numLayers + 1))
						count += 1;
			
			//Log.d("TIMER", "[" + logicPoint.x + " -> " + (logicHeight + logicPoint.x) + "]");
			//Log.d("TIMER", "[" + logicPoint.y + " -> " + (logicWidth + logicPoint.y) + "]");

			percent = (count * 100)/(logicWidth*logicHeight);

			//Show a screen notification
			Log.d("PORCENTAJE", "" + percent + "%");

			if(percent > 70)
			{
				//Release the background image
				//Scratch.drawingArea.setImageBitmap(Scratch.solution.getImage());
				//Scratch.drawingArea.setOnTouchListener(null);
				
				Log.d("PORCENTAJE", "FIN!");
			}
			else
				this.sendEmptyMessageDelayed(1, 2 * 1000);
		}
	};
}
