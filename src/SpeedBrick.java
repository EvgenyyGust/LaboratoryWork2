/*
 * File: SpeedBrick.java
 * -------------------
 * 
 * This file is for creating speedBricks in Breakout.java file
 */

import java.awt.Color;

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

public class SpeedBrick extends GCompound {
	 /**
	  * Places a brick with a "boost" sign
	  * @param x
	  * @param y
	  * @param width
	  * @param height
	  * @param currentColor
	  */
    public SpeedBrick(double x, double y, int width, int height, BrickColors currentColor) {
    	GRect speedBrick = new GRect(x, y, width, height);
    	speedBrick.setFilled(true);
    	setColor(Color.DARK_GRAY);
    	speedBrick.setFillColor(currentColor.toColor());
		
		int plusSize = 0;
		if(width > height){
			plusSize = height - 2;
		}
		else{
			plusSize = width - 2;
		}
		
		double middleBrickX = x + width / 2;
		double middleBrickY = y + height / 2;
		
		double centeredPlusX = middleBrickX - plusSize / 2;
		double centeredPlusY = middleBrickY - plusSize / 2;
		
		GImage boost = new GImage("boost.jpg");
		boost.setSize(plusSize, plusSize);
		boost.setLocation(centeredPlusX, centeredPlusY);
		
        add(speedBrick);
        add(boost);
    }
}
