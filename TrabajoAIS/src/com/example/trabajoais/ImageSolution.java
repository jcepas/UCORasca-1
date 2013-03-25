package com.example.trabajoais;

import android.graphics.Bitmap;
import android.graphics.Point;

public class ImageSolution {

	private Bitmap image;
	private int width;
	private int height;
	private Point point;
	
	
	public ImageSolution( Bitmap i, int an, int al, Point p ){
		this.image = i;
		this.width  = an;
		this.height   = al;
		this.point  = p;
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
	public Point getPoint() {
		return this.point;
	}
	
	/**
     * 	Method which return the width of the interest area from the left upper point
     * 
     * 	@date		20/03/2013
     * 	@version	1.0
     * 	@author		Matias Ramirez Ramirez
     */
	public int getWidth() {
		return this.width;
	}

	/**
     * 	Method which return the height of the interest area from the left upper point
     * 
     * 	@date		20/03/2013
     * 	@version	1.0
     * 	@author		Matias Ramirez Ramirez
     */
	public int getHeight() {
		return this.height;
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
	public void setImage( Bitmap i ){
		this.image = i;
	}
	
	/**
     * 	Method which set the class value of the left upper point param
     * 
     *	@param		p: point to set (Point)
     *
     * 	@date		20/03/2013
     * 	@version	1.0
     * 	@author		Matias Ramirez Ramirez
     */
	public void setPoint (Point p){
		this.point = p;
	}
	
	/**
     * 	Method which set the class value of the width param
     * 
     *	@param		width: width to set (int)
     *
     * 	@date		20/03/2013
     * 	@version	1.0
     * 	@author		Matias Ramirez Ramirez
     */
	public void setWidth( int width ){
		this.width = width;
	}
	
	/**
     * 	Method which set the class value of the height param
     * 
     *	@param		height: height to set (int)
     *
     * 	@date		20/03/2013
     * 	@version	1.0
     * 	@author		Matias Ramirez Ramirez
     */
	public void setHeight( int height ){
		this.height = height;
	}
	
	
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
		point.x = (point.x * width)/image.getWidth();
		point.y = (point.y * height)/image.getHeight();
		
		// Update the dimensions of the interesting zone
		this.width= (this.width * width)/image.getWidth();
		this.height= (this.height * height)/image.getHeight();
		
		// Scale the image
		image= Bitmap.createScaledBitmap(image, width, height, false);		
	}	
}
