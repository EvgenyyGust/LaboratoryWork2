/*
 * File: Breakout.java
 * -------------------
 * Name:
 * Section Leader:
 * 
 * This file will eventually implement the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

//для ідей можна використати https://share.google/auQqUWlG9Fmfc3bor (Block Breaker - Google)

//TODO:
//1. Установка цеглин (RED, ORANGE, YELLOW, GREEN, CYAN) (ст. 2) //done
//2. Створення ракетки (ст. 3) //done
//3. Створення м’яча та його рух (ст. 3) //done
//4. Перевірка колізій (private GObject getCollidingObject()) (ст. 4) //done
//5. Придумати, що робити, коли трапляється колізія (ст. 5) //done
//6. Додати звуки (ст. 6)
//7. Додати рахунок очок (ст. 6)
//8. Додати “kicker” (Якщо гра стає нудною, можна підняти її швидкість)  (ст. 6)
//9. Прикрасити гру (все, що тут написано не є обов'язковим):
//	 1)додати background
//	 2)додати більше кольорів (або замінити цеглинки на рисунки, з цим Євген може допомогти)
//   3)додати ефект, коли м'ячик відбивається від чогось
//   4)додати рамки гри (використовуючи WIDTH та HEIGHT)
//
//10. Додати ще щось (за бажанням, тут тільки ідеї):
//   1)кирпичик, який дає додаткове життя
//   2)кирпичик, який додає додатковий м'яч іншого кольору (буде декілька м'ячів одночасно)
//   3)збільшувати швидкість після кожної колізії з цеглинкою (та додати максимальну швидкість)
//   4)додати рахунок життя
//   5)різні рівні складності (можна, наприклад збільшити початкову та максимальну швидкість та зменжити кількість життів)
//   6)додати паузу (клавішу чи кнопку яка зупиняє гру, а потім продовжує її)
//   7)зробити декілька рівнів (можна зробити разом із ідеєю 5)

public class Breakout extends GraphicsProgram {

/** Width and height of application window in pixels */
	public static final int APPLICATION_WIDTH = 1000; //400
	public static final int APPLICATION_HEIGHT = 600;

/** Dimensions of game board (usually the same) */
	private static final int WIDTH = APPLICATION_WIDTH;
	private static final int HEIGHT = APPLICATION_HEIGHT;

/** Dimensions of the paddle */
	private static final int PADDLE_WIDTH = 80; //60
	private static final int PADDLE_HEIGHT = 10;

/** Offset of the paddle up from the bottom */
	private static final int PADDLE_Y_OFFSET = 30;

/** Number of bricks per row */
	private static final int NBRICKS_PER_ROW = 10;

/** Number of rows of bricks */
	private static final int NBRICK_ROWS = 10;

/** Separation between bricks (both x and y)*/
	private static final int BRICK_SEP = 8; //4

/** Width of a brick */
	private static final int BRICK_WIDTH =
	  (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

/** Height of a brick */
	private static final int BRICK_HEIGHT = 16; //8

/** Radius of the ball in pixels */
	private static final int BALL_RADIUS = 10;

/** Offset of the top brick row from the top 
 * (distance from the top of application to top brick row)*/
	private static final int BRICK_Y_OFFSET = 70;

/** Number of turns */
	private static final int NTURNS = 3;

/** Constants that are not a part of the lab work */
	private static final int DELAY = 10;
	
	private static final double CENTER_X = WIDTH / 2;
	private static final double HALF_PADDLE_WIDTH =  PADDLE_WIDTH / 2; //for mouseMoved method
	private static final double PADDLE_Y = HEIGHT - PADDLE_Y_OFFSET - PADDLE_HEIGHT; //for placePaddle and mouseMoved methods
	private static final int BALL_Y_OFFSET = 100; //for placeBall method
	private static final int BALL_DIAMETER = BALL_RADIUS * 2;
	
	
/* Method: run() */
/** Runs the Breakout program. */
	public void run() {
		this.setSize(APPLICATION_WIDTH, APPLICATION_HEIGHT);
		addMouseListeners();
		setupGame();
		runGame();
		
		/* You fill this in, along with any subsidiary methods */
		
	}
	
	/**
	 * initializes everything that is needed for the game
	 */
	private void setupGame(){
		placeBricks();
		placePaddle();
		placeBall();
		initializeBallSpeed();
	}
	
	/**
	 * runs all methods that are responsible for the game until gameOver is true
	 */
	private void runGame(){
		while(!gameOver){
			moveBall();
			pause(DELAY);
		}
	}
	
	/**
	 * places bricks using BrickColors.java (enum BrickColors) to choose the color of every 2 rows
	 * uses constants BRICK_Y_OFFSET, NBRICK_ROWS, NBRICKS_PER_ROW and BRICK_HEIGHT (BRICK_WIDTH)
	 * to determine placement of the first row 
	 */
	private void placeBricks(){
		BrickColors currentColor = BrickColors.RED;
		
		double rowWidth = BRICK_WIDTH * NBRICKS_PER_ROW + BRICK_SEP * (NBRICKS_PER_ROW - 1);
		double brickStartX = (WIDTH - rowWidth) / 2.0;
		
		double brickY = BRICK_Y_OFFSET;
		for(int i = 0; i < NBRICK_ROWS; i++){
			if(i > 0 && i % 2 == 0){
				currentColor = currentColor.nextColor();
			}
			
			double brickX = brickStartX;
			for(int j = 0; j < NBRICKS_PER_ROW; j++){
				GRect brick = new GRect(brickX, brickY, BRICK_WIDTH, BRICK_HEIGHT);
				brick.setColor(currentColor.toColor());
				brick.setFillColor(currentColor.toColor());
				brick.setFilled(true);
				add(brick);
				brickX += BRICK_WIDTH + BRICK_SEP;
			}
			brickY += BRICK_HEIGHT + BRICK_SEP;
		}
	}
	
	/**
	 * places the paddle at the left screen edge,
	 * to make the middle align with mouse movement
	 */
	private void placePaddle(){
//		double paddleStartingX = centerX - PADDLE_WIDTH / 2;
		double centerPaddleForMouseX =  -PADDLE_WIDTH / 2;
		paddle = new GRect(centerPaddleForMouseX, PADDLE_Y, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setColor(Color.BLACK);
		paddle.setFillColor(Color.BLACK);
		paddle.setFilled(true);
		add(paddle);
//		paddle.move(centerX, 0);
	}
	
	/**
	 * places a black ball using ballX, ballY (as starting coords) 
	 * and BALL_DIAMETER for size
	 */
	private void placeBall(){
		ball = new GOval(ballX,ballY,BALL_DIAMETER,BALL_DIAMETER);
		ball.setColor(Color.BLACK);
		ball.setFillColor(Color.BLACK);
		ball.setFilled(true);
		add(ball);
	}
	
	private void initializeBallSpeed(){
		vx = rgen.nextDouble(1.0, 3.0); 
		if (rgen.nextBoolean(0.5)){
			vx = -vx;
		}
		vy = 3;
	}
	
	/**
	 * moves the ball while checking for collisions (with the ball)
	 */
	private void moveBall(){
		ball.move(vx, vy);
		checkBallCollision();
	}
	
	/**
	 * checks if ball collides with wall or object (using getCollidedObject),
	 * and changes speed accordingly
	 */
	private void checkBallCollision(){
		//checks collision with walls
		if (ball.getX() < 0 || ball.getX() + BALL_DIAMETER > WIDTH){
			vx = -vx;
		}
		if(ball.getY() < 0){
			vy = -vy;
		}
		//checks collision with objects
		GObject collidedObject = getCollidedObject();
		if(collidedObject != null){
			if(collidedObject == paddle){
				vy = -Math.abs(vy);
			}
			else{
				remove(collidedObject);
				vy = -vy;
			}
		}
	}
	
	/**
	 * checks if ball collided and returns an object
	 * @return either null (if no collision) or collided object
	 */
	private GObject getCollidedObject(){
		if(getElementAt(ball.getX(), ball.getY()) != null){ //upper left corner
			return getElementAt(ball.getX(), ball.getY());
		}
		else if(getElementAt(ball.getX() + BALL_DIAMETER, ball.getY()) != null){ //upper right corner
			return getElementAt(ball.getX() + BALL_DIAMETER, ball.getY());
		}
		else if(getElementAt(ball.getX(), ball.getY() + BALL_DIAMETER) != null){ //lower left corner
			return getElementAt(ball.getX(), ball.getY() + BALL_DIAMETER);
		}
		else if(getElementAt(ball.getX() + BALL_DIAMETER, ball.getY() + BALL_DIAMETER) != null){ //lower right corner
			return getElementAt(ball.getX() + BALL_DIAMETER, ball.getY() + BALL_DIAMETER);
		}
		return null;
	}
	
	/**
	 * for moving the paddle
	 */
	public void mouseMoved(MouseEvent e) {
		if (paddle != null && e.getX() > HALF_PADDLE_WIDTH && e.getX() < WIDTH - HALF_PADDLE_WIDTH){
			double dx = e.getX() - last;
			paddle.move(dx, 0);
			last = e.getX();
		}
	}

	
	//ball speed
	private double vx; 
	private double vy;
	
	//starting ball coords
	double ballX = CENTER_X - BALL_RADIUS;
	double ballY = PADDLE_Y - BALL_Y_OFFSET;
	
	boolean gameOver = false;
	
	//for mouseMoved method
	private double last = 0;
	
	//objects
	private GRect paddle;
	private GOval ball;
	
	private RandomGenerator rgen = RandomGenerator.getInstance();
}
