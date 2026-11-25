/*
 * File: BrickColors.java
 * -------------------
 * 
 * This file is used for giving bricks different colors in Breakout.java file
 */

import java.awt.Color;

public enum BrickColors {
	RED, ORANGE, YELLOW, GREEN, CYAN;
	/**
	 * Accepts a string and returns a Color
	 * @return Color
	 */
	public Color toColor(){
		switch(this){
		case RED: return Color.RED;
		case ORANGE: return Color.ORANGE;
		case YELLOW: return Color.YELLOW;
		case GREEN: return Color.GREEN;
		case CYAN: return Color.CYAN;
		default: return Color.RED;
		}
	}
	/**
	 * Accepts a color and returns a color that is after it
	 * @return color string
	 */
	public BrickColors nextColor(){
		switch(this){
		case RED: return ORANGE;
		case ORANGE: return YELLOW;
		case YELLOW: return GREEN;
		case GREEN: return CYAN;
		default: return RED;
		}
	}
}
