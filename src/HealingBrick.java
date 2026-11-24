import java.awt.Color;

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

public class HealingBrick extends GCompound {
	 
    public HealingBrick(double x, double y, int width, int height, BrickColors currentColor) {
    	GRect healingBrick = new GRect(x, y, width, height);
    	healingBrick.setFilled(true);
    	setColor(Color.BLACK);
		healingBrick.setFillColor(currentColor.toColor());
		
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
		
		GImage plus = new GImage("plus.jpg");
		plus.setSize(plusSize, plusSize);
		plus.setLocation(centeredPlusX, centeredPlusY);
		
        add(healingBrick);
        add(plus);
    }
}
