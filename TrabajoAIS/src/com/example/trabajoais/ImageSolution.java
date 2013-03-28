package com.example.trabajoais;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.Log;

public class ImageSolution {

	private Bitmap image;
	
	private Point interestingPoint;
	private int interestingWidth;
	private int interestingHeight;
	
	private int stride;
	private Point interestingLogicPoint;
	private int interestingLogicWidth;
	private int interestingLogicHeight;
	
	
	public ImageSolution(Bitmap image, int stride, Point point, int width, int height){
		
		this.image = image;
		this.stride= stride;
		
		/*interestingPoint= point;
		interestingWidth= width;
		interestingHeight= height;*/
		
		interestingPoint= point;
		interestingPoint.x= image.getWidth()/2;
		interestingPoint.y= image.getHeight()/2;
		
		interestingWidth= image.getWidth()/2;
		interestingHeight= image.getHeight()/2;
		
		interestingLogicPoint= new Point(real2logic(interestingPoint.x), real2logic(interestingPoint.y));
		interestingLogicWidth= real2logic( interestingPoint.y + interestingWidth );
		interestingLogicHeight= real2logic( interestingPoint.x + interestingHeight );
	}
	
	/**
     * 	Method which it returns the logical coordinates of the given number
     * 
     *	@param		number: Number of which we want to get its logic coordinates (int)
     *
     *	@return		int with the logic coordinate of the given number 
     * 
     * 	@date		26/03/2013
     * 	@version	1.0
     * 	@author		Manuel Flores Arribas
     */
	public int real2logic(int number)
	{
		return number/stride;
	}
	
	/**
     * 	Method which return the Bitmap image
     * 
     * 	@date		20/03/2013
     * 	@version	1.0
     * 	@author		Matias Ramirez Ramirez
     */
	public Bitmap getImage() {
		return this.image;
	}
	
	/**
     * 	Method which return the left upper point where interest area starts
     * 
     * 	@date		20/03/2013
     * 	@version	1.0
     * 	@author		Matias Ramirez Ramirez
     */
	public Point getInterestingPoint() {
		return this.interestingPoint;
	}
	
	/**
     * 	Method which return the width of the interest area from the left upper point
     * 
     * 	@date		20/03/2013
     * 	@version	1.0
     * 	@author		Matias Ramirez Ramirez
     */
	public int getInterestingWidth() {
		return this.interestingWidth;
	}

	/**
     * 	Method which return the height of the interest area from the left upper point
     * 
     * 	@date		20/03/2013
     * 	@version	1.0
     * 	@author		Matias Ramirez Ramirez
     */
	public int getInterestingHeight() {
		return this.interestingHeight;
	}
	
	/**
     * 	Method which return the left upper point where interest area starts
     * 
     * 	@date		26/03/2013
     * 	@version	1.0
     * 	@author		Manuel Flores Arribas
     */
	public Point getInterestingLogicPoint() {
		return this.interestingLogicPoint;
	}
	
	/**
     * 	Method which return the width of the interest area from the left upper point
     * 
     * 	@date		26/03/2013
     * 	@version	1.0
     * 	@author		Manuel Flores Arribas
     */
	public int getInterestingLogicWidth() {
		return this.interestingLogicWidth;
	}

	/**
     * 	Method which return the height of the interest area from the left upper point
     * 
     * 	@date		26/03/2013
     * 	@version	1.0
     * 	@author		Manuel Flores Arribas
     */
	public int getInterestingLogicHeight() {
		return this.interestingLogicHeight;
	}
	
	/**
     * 	Method which set the class value of the image param 
     * 
     *	@param		i: image to set (Bitmap)
     *
     * 	@date		20/03/2013
     * 	@version	1.0
     * 	@author		Matias Ramirez Ramirez
     */
	/*public void setImage( Bitmap i ){
		this.image = i;
	}*/
	
	/**
     * 	Method which set the class value of the left upper point param
     * 
     *	@param		p: point to set (Point)
     *
     * 	@date		20/03/2013
     * 	@version	1.0
     * 	@author		Matias Ramirez Ramirez
     */
	/*public void setPoint (Point p){
		this.point = p;
	}*/
	
	/**
     * 	Method which set the class value of the width param
     * 
     *	@param		width: width to set (int)
     *
     * 	@date		20/03/2013
     * 	@version	1.0
     * 	@author		Matias Ramirez Ramirez
     */
	/*public void setWidth( int width ){
		this.width = width;
	}*/
	
	/**
     * 	Method which set the class value of the height param
     * 
     *	@param		height: height to set (int)
     *
     * 	@date		20/03/2013
     * 	@version	1.0
     * 	@author		Matias Ramirez Ramirez
     */
	/*public void setHeight( int height ){
		this.height = height;
	}*/
	
	
	/**
     * 	Method which scale the image to a new width and height. 
     *  It also modify the upper left to point to the new scaled
     * 
     *	@param		width: width of the new image (int)
     *  @param      height: height of the new image (int)
     *
     * 	@date		25/03/2013
     * 	@version	1.0
     * 	@author		Manuel Flores Arribas
     */
	public void scale(int width, int height){
		
		// Update the coordinates of the interesting point
		interestingPoint.x = (interestingPoint.x * width)/image.getWidth();
		interestingPoint.y = (interestingPoint.y * height)/image.getHeight();
		
		// Update the dimensions of the interesting zone
		interestingWidth= (interestingWidth * width)/image.getWidth();
		interestingHeight= (interestingHeight * height)/image.getHeight();
		
		// Update the logic information
		interestingLogicPoint.x= real2logic(interestingPoint.x);
		interestingLogicPoint.y= real2logic(interestingPoint.y);
		interestingLogicWidth= real2logic(interestingWidth);
		interestingLogicHeight= real2logic(interestingHeight);
		
		// Scale the image
		image= Bitmap.createScaledBitmap(image, width, height, false);
		
		Log.d("IMAGESOLUTION ZONE", "[" + interestingWidth + ", " + interestingHeight + "]");
		Log.d("IMAGESOLUTION POINT", "[" + interestingPoint.x + ", " + interestingPoint.y + "]");
		Log.d("IMAGESOLUTION LOGIC POINT", "[" + interestingLogicPoint.x + ", " + interestingLogicPoint.y + "]");
	}	
}
