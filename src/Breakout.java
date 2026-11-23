/*
 * File: Breakout.java
 * -------------------
 * Name: Breakout
 * Section Leader: Yevhen, Fedir
 * 
 * This file implements the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import javax.sound.sampled.*;
import java.applet.*;
import java.awt.*;
import java.awt.event.*;

import java.io.File;

//для ідей можна використати https://share.google/auQqUWlG9Fmfc3bor (Block Breaker - Google)

//TODO:
//1. Установка цеглин (RED, ORANGE, YELLOW, GREEN, CYAN) (ст. 2) //done
//2. Створення ракетки (ст. 3) //done
//3. Створення м’яча та його рух (ст. 3) //done
//4. Перевірка колізій (private GObject getCollidingObject()) (ст. 4) //done
//5. Придумати, що робити, коли трапляється колізія (ст. 5) //done
//6. Додати звуки (ст. 6) (можна використати "bounce.au" в bin)//done
//7. Додати рахунок очок (ст. 6)//done
//8. Додати “kicker” (Якщо гра стає нудною, можна підняти її швидкість)  (ст. 6)//done
//9. Прикрасити гру (все, що тут написано не є обов'язковим):
//	 1)додати background
//	 2)додати більше кольорів (або замінити цеглинки на рисунки, з цим Євген може допомогти)
//   3)додати ефект, коли м'ячик відбивається від чогось
//   4)додати рамки гри (використовуючи WIDTH та HEIGHT)
//
//10. Додати ще щось (за бажанням, тут тільки ідеї):
//   1)кирпичик, який дає додаткове життя
//   2)кирпичик, який додає додатковий м'яч іншого кольору (буде декілька м'ячів одночасно)
//   3)збільшувати швидкість після кожної колізії з цеглинкою (та додати максимальну швидкість
//   4)додати рахунок життя
//   5)різні рівні складності (можна, наприклад збільшити початкову та максимальну швидкість та зменжити кількість життів)//done
//   6)додати паузу (клавішу чи кнопку яка зупиняє гру, а потім продовжує її) //done, pause is "space"
//   7)зробити декілька рівнів (можна зробити разом із ідеєю 5)// done
//
//11. Можна придумати іншу назву для цієї гри (замість "Breakout", наприклад "BrickBreaker")

//якщо треба допомога, пиши Єременку Євгену
//зробив коментарі англійською мовою, сподіваюсь це не проблема

//TODO №2:
//ask the user which difficulty (or level) they want (run) //done
//ask if user wants to try again (or pick a level) (run) //done
//write a victory message (gameOver)//DONE
//add different amount of lives depending on difficulty or level (setLives)//done
//change starting speed depending on the difficulty (initializeBallSpeed)//done

/**
 * @author Yeremenko Yevhen Yevhenovych, Bolgar Fedir Vasylovych
 * 
 */
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

/** Number of turns 
 *  (amount of lives by default)*/
	private static final int NTURNS = 3;
	private int selectedLives = NTURNS - 1;
	private int SPEED = 1;

/** Constants that are not a part of the lab work */
	private static final int DELAY = 10;
	private static final double CENTER_X = WIDTH / 2;
	private static final double HALF_PADDLE_WIDTH =  PADDLE_WIDTH / 2; //for mouseMoved method
	private static final double PADDLE_Y = HEIGHT - PADDLE_Y_OFFSET - PADDLE_HEIGHT; //for placePaddle and mouseMoved methods
	private static final int BALL_Y_OFFSET = 100; //for placeBall method
	private static final int BALL_DIAMETER = BALL_RADIUS * 2;
	private static final int BRICK_AMOUNT = NBRICK_ROWS * NBRICKS_PER_ROW;

/**музика */
	private AudioClip music;
	private static final String SND = "sound/";

/** меню */
	//основні кнопки
	private GRect startButton;
	private GLabel startLabel;
	private GRect musicButton;
	private GLabel musicLabel;
	private GRect levelButton;
	private GLabel levelLabel;

	private boolean musicON = true;

	//кнопки вибору рівня
	private GRect levelEasyButton;
	private GLabel levelEasyLabel;
	private GRect levelMediumButton;
	private GLabel levelMediumLabel;
	private GRect levelHardButton;
	private GLabel levelHardLabel;

/** Меню програшу */
	private GRect restartButton;
	private GLabel restartLabel;
	private GLabel loseLabel;

/** Меню перемоги */
	private GLabel winLabel;

/** Рахунок */
	private GRect scoreButton;
	private GLabel scoreLabel;
	private int score;

/* Method: run() */
/** Runs the Breakout program. */
	public void run() {
		this.setSize(APPLICATION_WIDTH, APPLICATION_HEIGHT);
		addMouseListeners();
		addKeyListeners();
		music = MediaTools.loadAudioClip("nathanielthomasbrack-8-bit-loop-189494.wav");

		gameState = 0;
		while(true) {
			if(gameState == 0) {
				showMenu();

				while(gameState == 0) {// чекає поки кнопка запуску не буде натиснена
					pause(DELAY);
				}
			}
			setupGame();
			runGame();

			while(gameState == 3) {
				pause(DELAY);
			}
		}

	}
	
	/**
	 * initializes everything that is needed for the game
	 */
	private void setupGame(){
		//Для рестарту гри потрібно збрасувати усі змінні
		score = 0;
		bricksLeft = BRICK_AMOUNT;
		lives = selectedLives;
		pause = false;


		ball = null;
		paddle = null;

		last = 0;//без цього працює дуже криво


		placeBricks();
		placePaddle();
		placeBall();
		initializeBallSpeed();

		pause(1500);
	}
	
	/**
	 * runs all methods that are responsible for the game until gameOver is true
	 */
	private void runGame(){
		gameState = 2;
		count();
		while(!gameOver()){
			if(!pause){
				moveBall();
				checkBall();
			}
			pause(DELAY);
		}
	}
	
	/**
	 * for gameState == 2, checks if there are no bricks and if user no lives
	 * @return either true or false
	 */
	private boolean gameOver(){
		if(bricksLeft == 0){
			if(music != null){
				music.stop();
			}
			gameState = 3;
			showWin();
			return true;
		}
		if(ball == null){
			if(music != null){
				music.stop();
			}
			playSound("freesound_community-videogame-death-sound-43894.wav");
			gameState = 3;
			showLose();

			return true;
		}
		return false;
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
		double centerPaddleForMouseX = -PADDLE_WIDTH / 2;
		paddle = new GRect(centerPaddleForMouseX, PADDLE_Y, PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setColor(Color.BLACK);
		paddle.setFillColor(Color.BLACK);
		paddle.setFilled(true);
		add(paddle);
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
	
	/**
	 * makes the ball move at start
	 */
	private void initializeBallSpeed(){
		//maybe change starting speed depending on the difficulty
		
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
		ball.move(vx * SPEED, vy * SPEED);
		checkBallCollision();
	}
	
	/**
	 * check if ball exists and adds a new ball if lives != 0
	 */
	private void checkBall(){
		if(ball == null && lives > 0){
			pause(500);
			placeBall();
			lives--;
		}
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
		//checks collision with roof
		if(ball.getY() < 0){
			vy = -vy;
		}
		//checks collision with floor
		if(ball.getY() + BALL_DIAMETER > HEIGHT){
			remove(ball);
			ball = null;
			return;
		}
		//checks collision with objects
		GObject collidedObject = getCollidedObject();
		if(collidedObject != null){
			if(collidedObject == paddle){
				vy = -Math.abs(vy);
			}
			else{//removes brick
				Color color = collidedObject.getColor();
				remove(collidedObject);
				playSound("freesound_community-gameboy-pluck-41265.wav");
				bricksLeft--;
				vy = -vy;
				addScore(color);
			}
		}
	}
	
	/**
	 * checks if ball collided and returns an object
	 * @return either null (if no collision) or collided object
	 */
	private GObject getCollidedObject(){
		//upper left corner
		if(getElementAt(ball.getX(), ball.getY()) != null){
			return getElementAt(ball.getX(), ball.getY());
		}
		//upper right corner
		else if(getElementAt(ball.getX() + BALL_DIAMETER, ball.getY()) != null){
			return getElementAt(ball.getX() + BALL_DIAMETER, ball.getY());
		}
		//lower left corner
		else if(getElementAt(ball.getX(), ball.getY() + BALL_DIAMETER) != null){
			return getElementAt(ball.getX(), ball.getY() + BALL_DIAMETER);
		}
		//lower right corner
		else if(getElementAt(ball.getX() + BALL_DIAMETER, ball.getY() + BALL_DIAMETER) != null){
			return getElementAt(ball.getX() + BALL_DIAMETER, ball.getY() + BALL_DIAMETER);
		}
		return null;
	}
	
	/**
	 * for moving the paddle
	 */
	public void mouseMoved(MouseEvent e){
		if (paddle != null && e.getX() > HALF_PADDLE_WIDTH && e.getX() < WIDTH - HALF_PADDLE_WIDTH && !pause){
			double dx = e.getX() - last;
			paddle.move(dx, 0);
			last = e.getX();
		}
	}

	/**
	 * pauses the game when space is pressed (pause == true or pause == false)
	 */
	public void keyPressed(KeyEvent e) {
		//maybe remove the ability to pause on hardest difficulty
		
		if(gameState == 2){
			if(e.getKeyCode() == KeyEvent.VK_SPACE){
				if(pause == false){
					pause = true;
				}
				else if(pause == true){
					pause = false;
				}
			}
		}
	}

	private void createButton(GRect button, GLabel label, Color color){ //робить кнопки
		button.setColor(color);
		button.setFilled(true);
		button.setFillColor(Color.BLACK);
		label.setFont("Monospaced-30");
		label.setColor(color);
		add(button);
		add(label);
		centerLabel(label, button);
	}

	private void centerLabel(GLabel label, GRect button){ //центрує текст
		double x = button.getX() + (button.getWidth() - label.getWidth()) / 2;
		double y = button.getY() + (button.getHeight() + label.getAscent()) / 2;
		label.setLocation(x, y);
	}

	private void updateSettingsText(){//змінює стан кнопок
		if(musicON){
			musicLabel.setLabel("MUSIC: ON");
		}
		else{
			musicLabel.setLabel("MUSIC: OFF");
		}
		centerLabel(musicLabel, musicButton);
	}

	//робить та показує меню початку
	private void showMenu(){
		setBackground(Color.BLACK);

		GLabel title = new GLabel("BREAKOUT");
		title.setFont("Monospaced-90");
		title.setColor(Color.YELLOW);
		title.setLocation((WIDTH - title.getWidth()) / 2, 150);
		add(title);

		startButton = new GRect((WIDTH - 300) / 2, 220, 300, 80);
		startLabel = new GLabel("START GAME");
		createButton(startButton, startLabel, Color.GREEN);

		musicButton = new GRect((WIDTH - 240) / 2, 350, 240, 50);
		musicLabel = new GLabel("MUSIC: ON");
		createButton(musicButton, musicLabel, Color.CYAN);
		updateSettingsText();

		levelButton = new GRect((WIDTH - 240) / 2, 420, 240, 50);
		levelLabel = new GLabel("CHOOSE LEVEL");
		createButton(levelButton, levelLabel, Color.ORANGE);
	}
//слідкує за мишею і кліком кнопок
	public void mouseClicked(MouseEvent e){
		GObject clicked = getElementAt(e.getX(), e.getY());
		if(clicked == null){
			return;
		}
		// Головне меню
		if(gameState == 0){
			if(clicked == startButton || clicked == startLabel){
				startGame();
			}
			else if(clicked == musicButton || clicked == musicLabel){
				toggleMusic();
			}
			else if(clicked == levelButton || clicked == levelLabel){
				levelChoise();
			}
			else if(clicked == levelEasyButton || clicked == levelEasyLabel){
				SPEED = 1;
				selectedLives = NTURNS - 1;
				removeAll();
				showMenu();
			}
			else if(clicked == levelMediumButton || clicked == levelMediumLabel){
				SPEED = 2;
				selectedLives = NTURNS - 2;
				removeAll();
				showMenu();
			}
			else if(clicked == levelHardButton || clicked == levelHardLabel){
				SPEED = 3;
				selectedLives = NTURNS - 3;
				removeAll();
				showMenu();
			}
		}
		// Меню після програшу
		else if(gameState == 3){
			if(clicked == restartButton || clicked == restartLabel){
				startGame();
			}
			else if(clicked == musicButton || clicked == musicLabel){
				toggleMusic();
			}
			else if(clicked == levelButton || clicked == levelLabel){
				levelChoise();
			}
			else if(clicked == levelEasyButton || clicked == levelEasyLabel){
				SPEED = 1;
				selectedLives = NTURNS - 1;
				removeAll();
				showMenu();
				gameState = 0;
			}
			else if(clicked == levelMediumButton || clicked == levelMediumLabel){
				SPEED = 2;
				selectedLives = NTURNS - 2;
				removeAll();
				showMenu();
				gameState = 0;
			}
			else if(clicked == levelHardButton || clicked == levelHardLabel){
				SPEED = 3;
				selectedLives = NTURNS - 3;
				removeAll();
				showMenu();
				gameState = 0;
			}
		}
	}
	//підготовлює середовище до і після меню
	private void startGame(){
		if(gameState != 0 && gameState != 3){
			return;
		}

		removeAll();
		setBackground(Color.WHITE);
		if(musicON && music != null){
			music.loop();
		}
		gameState = 2;
	}

	private void toggleMusic(){
		musicON = !musicON;
		updateSettingsText();
	}
	private void levelChoise(){ //кнопка вибору рівня
		removeAll();
		levelEasyButton = new GRect((WIDTH - 300) / 2, 150, 300, 80);
		levelEasyLabel = new GLabel("EASY");
		createButton(levelEasyButton, levelEasyLabel, Color.GREEN);

		levelMediumButton = new GRect((WIDTH - 300) / 2, 240, 300, 80);
		levelMediumLabel = new GLabel("Medium");
		createButton(levelMediumButton, levelMediumLabel, Color.ORANGE);

		levelHardButton = new GRect((WIDTH - 300) / 2, 330, 300, 80);
		levelHardLabel = new GLabel("HARD");
		createButton(levelHardButton, levelHardLabel, Color.RED);
	}
	//меню програшу
	private void showLose(){
		removeAll();

		setBackground(Color.BLACK);

		loseLabel = new GLabel("Defeat");
		loseLabel.setFont("Monospaced-90");
		loseLabel.setColor(Color.RED);
		loseLabel.setLocation((WIDTH - loseLabel.getWidth()) / 2, 150);
		add(loseLabel);

		restartButton = new GRect((WIDTH - 300) / 2, 200, 300, 80);
		restartLabel = new GLabel("RESTART GAME");
		createButton(restartButton, restartLabel, Color.GREEN);

		scoreButton = new GRect((WIDTH - 300) / 2, 300, 300, 80);
		scoreLabel = new GLabel("Score:" + score);
		createButton(scoreButton, scoreLabel, Color.WHITE);

		musicButton = new GRect((WIDTH - 240) / 2, 400, 240, 50);
		musicLabel = new GLabel("MUSIC: ON");
		createButton(musicButton, musicLabel, Color.CYAN);
		updateSettingsText();

		levelButton = new GRect((WIDTH - 240) / 2, 470, 240, 50);
		levelLabel = new GLabel("CHOOSE LEVEL");
		createButton(levelButton, levelLabel, Color.ORANGE);
	}
	//меню перемоги
	private void showWin(){
		removeAll();

		setBackground(Color.BLACK);

		winLabel = new GLabel("Victory!");
		winLabel.setFont("Monospaced-90");
		winLabel.setColor(Color.GREEN);
		winLabel.setLocation((WIDTH - loseLabel.getWidth()) / 2, 150);
		add(winLabel);

		restartButton = new GRect((WIDTH - 300) / 2, 200, 300, 80);
		restartLabel = new GLabel("RESTART GAME");
		createButton(restartButton, restartLabel, Color.GREEN);

		scoreButton = new GRect((WIDTH - 300) / 2, 300, 300, 80);
		scoreLabel = new GLabel("Score:" + score);
		createButton(scoreButton, scoreLabel, Color.WHITE);

		musicButton = new GRect((WIDTH - 240) / 2, 400, 240, 50);
		musicLabel = new GLabel("MUSIC: ON");
		createButton(musicButton, musicLabel, Color.CYAN);
		updateSettingsText();

		levelButton = new GRect((WIDTH - 240) / 2, 470, 240, 50);
		levelLabel = new GLabel("CHOOSE LEVEL");
		createButton(levelButton, levelLabel, Color.ORANGE);
	}



	//музика при діях
	private void playSound(String fileName) {
		try {
			File file = new File(SND + fileName);
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(file));
			clip.addLineListener(event -> {
				if (event.getType() == LineEvent.Type.STOP) {
					clip.close();
				}
			});
			clip.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//кількість балів
	private void count(){
		scoreButton = new GRect(20, 10, 200, 50);
		scoreLabel = new GLabel("" + score);

		scoreButton.setColor(Color.CYAN);
		scoreButton.setFilled(true);
		scoreLabel.setFont("Monospaced-20");
		add(scoreButton);
		add(scoreLabel);
		centerLabel(scoreLabel, scoreButton);
	}

	private void updateScore(){ //оновлює значення балів
		scoreLabel.setLabel(score + " ");
	}

	private void addScore(Color color){//перевіряє який колір у цеглини та дає бали за нього
		if(color.equals(Color.CYAN)){
			score += 10;
		}
		else if(color.equals(Color.GREEN)){
			score += 20;
		}
		else if(color.equals(Color.YELLOW)){
			score += 30;
		}
		else if(color.equals(Color.ORANGE)){
			score += 40;
		}
		else if(color.equals(Color.RED)){
			score += 50;
		}
		updateScore();
	}

	
	//ball speed
	private double vx; 
	private double vy;
	
	//starting ball coords
	private double ballX = CENTER_X - BALL_RADIUS;
	private double ballY = PADDLE_Y - BALL_Y_OFFSET;
	
	//0 is start screen, 1 is pick difficulty (or level), 2 is playing, 3 is game over, 4 is exit (end of the program)
	private int gameState = 0;
    //if true, pauses the game (runGame)
	private boolean pause = false;
	
	private int lives;
	private int bricksLeft = BRICK_AMOUNT;
	
	//placeholder, maybe add 3 difficulties: 1-easy, 2-normal, 3-hard
	private int difficulty;
	
	//for mouseMoved method
	private double last = 0;
	
	//objects
	private GRect paddle;
	private GOval ball;
	
	private RandomGenerator rgen = RandomGenerator.getInstance();
}
