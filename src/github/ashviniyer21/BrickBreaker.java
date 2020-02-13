import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.shape.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.control.*;
import java.io.File;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.input.*;
public class BrickBreaker extends Application
{
    private final int X = 1000;
    private final int Y = 600;
    private Label mouseArea = new Label("");
    private Brick[][] bricks = new Brick[X/100][Y/200];
    private Rectangle paddle;
    private Ball ball;
    private Ball ball2;
    private final int BALL_RADIUS = 10;
    private final int PADDLE_LENGTH = 100;
    private final int PADDLE_WIDTH = 40;
    private int score = 0;
    private boolean gameOver = false;
    private boolean pause = false;
    private boolean reverse = false;
    private boolean paddleExtend = false;
    private boolean paddleShrink = false;
    private int powerupExpireCounter = 0;
    private int powerupStartCounter = 0;
    private boolean addCount = false;
    private PowerupHolder powerupHolder;
    private boolean resetPowerup = false;
    private boolean startPowerup = true;
    private Text combo2Display;
    private static Color mainColor = Color.BLACK;
    private static Color secondaryColor = Color.WHITE;
    private final int LOOP_COUNT = 40;
    private int loopCounts = 0;
    private Text gameOverText;
    private static int highScore = 0;
    private static int musicCount = 0;
    private int addBallScore = 0;
    @Override
    public void start(Stage stage) {
        String uriString = new File("Tetris.mp3").toURI().toString();
        MediaPlayer player = new MediaPlayer(new Media(uriString));
        Pane root = new Pane();
        player.setCycleCount(MediaPlayer.INDEFINITE);
        gameOver = false;
        final Timer clockTimer = new Timer();
        Scene gameScene = new Scene(root,X+100 ,Y);
        stage.setTitle("BrickBreaker");
        stage.setScene(gameScene);
        Rectangle r = new Rectangle(0, 0, X, Y);
        Rectangle t = new Rectangle(X, 0, 3000, 3000);
        Rectangle s = new Rectangle(0, Y, 3000, 3000);
        powerupHolder = new PowerupHolder(getPowerup(), getPowerupPos());
        r.setFill(mainColor);
        s.setFill(mainColor);
        t.setFill(mainColor);
        root.getChildren().addAll(r);
        root.getChildren().addAll(s);
        root.getChildren().addAll(t);
        for(int i = 0; i < X/100; i++){
            for(int j = 0; j < Y/200; j++){
                bricks[i][j] = new Brick(11 + i*100, 20 + j*80, 4-j);
                bricks[i][j].setColor();
                root.getChildren().addAll(bricks[i][j]);
            }
        }
        Text scoreDisplay = new Text(X, 300, "Score: " + score);
        scoreDisplay.setFill(secondaryColor);
        root.getChildren().addAll(scoreDisplay);
        Text highScoreDisplay = new Text(X, 100, "High Score: " + highScore);
        highScoreDisplay.setFill(secondaryColor);
        root.getChildren().addAll(highScoreDisplay);
        paddle = new Rectangle((X- PADDLE_LENGTH)/2,Y- PADDLE_WIDTH, PADDLE_LENGTH, PADDLE_WIDTH);
        paddle.setFill(Color.BLUE);
        ball2 = new Ball((X- BALL_RADIUS)/2, Y+10);
        ball2.setVelocities(0, 0);
        root.getChildren().addAll(ball2);
        ball = new Ball((X- BALL_RADIUS)/2, Y+10);
        ball.setFill(Color.GREEN);
        ball.setVelocities(getXSpeed(), getYSpeed());
        root.getChildren().addAll(ball);
        Text combo1Display = new Text(X, 400, "Combo 1: " + ball.getCombo());
        combo1Display.setFill(secondaryColor);
        combo2Display = new Text(X, 500, "Combo 2: " + ball2.getCombo());
        combo2Display.setFill(mainColor);
        gameOverText = new Text(X/2-50, 3*Y/4, "");
        Text time = new Text(X, 200, "Time: " + getTime());
        time.setFill(secondaryColor);
        gameOverText.setFill(secondaryColor);
        root.getChildren().addAll(gameOverText);
        root.getChildren().addAll(mouseArea);
        root.getChildren().addAll(powerupHolder);
        root.getChildren().addAll(paddle);
        root.getChildren().addAll(combo1Display);
        root.getChildren().addAll(combo2Display);
        root.getChildren().addAll(time);
        gameScene.setOnMouseMoved(event -> {
            if (!gameOver && !checkWin()) {
                if(!pause){
                    if(reverse){
                        paddle.setX(Math.max(0, Math.min(X-paddle.getWidth(), X-event.getSceneX())));
                    } else {
                        paddle.setX(Math.max(0, Math.min(event.getSceneX() - paddle.getWidth() / 2.0, X - paddle.getWidth())));
                    }
                }
            }
        });
        gameScene.setOnMouseClicked(event -> pause = !pause);
        gameScene.setOnKeyPressed(event -> {
            if(gameOver && event.getCode() == KeyCode.SPACE){
                reset();
            }
        });
        player.play();
        clockTimer.scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run(){
                if(reverse || paddleExtend || paddleShrink || addCount){
                    powerupExpireCounter++;
                } else {
                    powerupStartCounter++;
                }
                if(powerupExpireCounter > 200){
                    reverse = false;
                    paddleExtend = false;
                    paddleShrink = false;
                    addCount = false;
                    startPowerup = true;
                    powerupExpireCounter = 0;
                }
                if(powerupStartCounter > 200 && startPowerup){
                    powerupHolder.reset(getPowerup(), getPowerupPos());
                    powerupHolder.setFill(Color.PURPLE);
                    startPowerup = false;
                    resetPowerup = false;
                }
                if(paddleExtend){
                    paddle.setWidth(PADDLE_LENGTH+50);
                } else if(paddleShrink){
                    paddle.setWidth(PADDLE_LENGTH-50);
                } else {
                    paddle.setWidth(PADDLE_LENGTH);
                }
                if(!pause){
                    loopCounts++;
                    if(checkWin()){
                        player.stop();
                        combo1Display.setText("");
                        combo2Display.setText("");
                        gameOverText.setText("You win. Press space to play again");
                        gameOver = true;
                    } else if(!gameOver){
                        if(addBallScore > 25 && ball1Dead()){
                            ball.setCenterX((X- BALL_RADIUS)/2);
                            ball.setCenterY(Y-8* PADDLE_WIDTH);
                            ball.setFill(Color.GREEN);
                            ball.setVelocities(getXSpeed(), getYSpeed());
                            combo1Display.setFill(secondaryColor);
                            addBallScore = 0;
                        } else if(addBallScore > 25 && ball2Dead()){
                            ball2.setCenterX((X- BALL_RADIUS)/2);
                            ball2.setCenterY(Y-8* PADDLE_WIDTH);
                            ball2.setFill(Color.GREEN);
                            ball2.setVelocities(getXSpeed(), getYSpeed());
                            combo2Display.setFill(secondaryColor);
                            addBallScore = 0;
                        } else if(!ball1Dead() && ! ball2Dead()){
                            addBallScore = 0;
                        }
                        scoreDisplay.setText("Score: " + score);
                        if(score > highScore){
                            highScore = score;
                        }
                        highScoreDisplay.setText("High Score: " + highScore);
                        if(ball1Dead() && ball2Dead()){
                            combo1Display.setText("");
                            combo2Display.setText("");
                        }
                        if(ball1Dead()){
                            combo1Display.setText("Combo: " + (ball2.getCombo() - 1));
                            combo2Display.setText("");
                        }
                        else if(ball2Dead()){
                            combo2Display.setText("");
                            combo1Display.setText("Combo: " + (ball.getCombo()-1));
                        } else {
                            combo1Display.setText("Combo 1: " + (ball.getCombo()-1));
                            combo2Display.setText("Combo 2: " + (ball2.getCombo() - 1));
                        }
                        time.setText("Time: " + getTime());
                        checkCollision(ball);
                        checkCollision(ball2);
                        checkDeath();
                        ball.update();
                        ball2.update();
                        powerupHolder.move();
                        for(int i = 0; i < X/100; i++){
                            for(int j = 0; j < Y/200; j++){
                                checkBreak(ball, bricks[i][j]);
                                checkBreak(ball2, bricks[i][j]);
                            }
                        }
                        setPowerup(powerupHolder);
                        player.play();
                    } else {
                        combo1Display.setText("");
                        combo2Display.setText("");
                        player.stop();
                        if(musicCount == 0){
                            gameOverText.setText("Press Space to play");
                        } else {
                            gameOverText.setText("Game Over. Press Space to play again.");
                        }
                    }
                } else {
                    player.pause();
                }
            }
        }, 0, LOOP_COUNT);
        stage.show();
    }
    private void checkCollision(Ball ball){
        if(ball.getCenterY()+ BALL_RADIUS >= paddle.getY() && ball.getCenterY()+ BALL_RADIUS <=paddle.getY() + PADDLE_WIDTH){
            if(ball.getCenterX() >= paddle.getX() && ball.getCenterX() <= paddle.getX() + paddle.getWidth()){
                ball.setCenterY(paddle.getY() - BALL_RADIUS);
                ball.resetCombo();
                ball.setVelocities(ball.getXVel(), -ball.getYVel());
            }
        }
        if(ball.getCenterX()+ BALL_RADIUS >= X){
            ball.setCenterX(X- BALL_RADIUS);ball.setVelocities(-ball.getXVel(), ball.getYVel());
        }
        if(ball.getCenterX()- BALL_RADIUS <= 0){
            ball.setCenterX(BALL_RADIUS);
            ball.setVelocities(-ball.getXVel(), ball.getYVel());
        }
        if(ball.getCenterY() - BALL_RADIUS <= 0){
            ball.setCenterY(BALL_RADIUS);
            ball.setVelocities(ball.getXVel(), -ball.getYVel());
        }
    }
    private void checkBreak(Ball ball, Brick b){
        int BRICK_WIDTH = 50;
        if(ball.getCenterY()- BALL_RADIUS < b.getY() + BRICK_WIDTH && ball.getCenterY() + BALL_RADIUS > b.getY()){
            int BRICK_LENGTH = 75;
            if(ball.getCenterX()- BALL_RADIUS < b.getX() + BRICK_LENGTH && ball.getCenterX() + BALL_RADIUS > b.getX()){
                if(b.isAlive()){
                    score+= ball.getCombo();
                    addBallScore+= ball.getCombo();
                    ball.updateCombo();
                    if(!b.almostDead()){
                        if(ball.getCenterY() > b.getY()+ BRICK_WIDTH){
                            ball.setVelocities(ball.getXVel(), -ball.getYVel());
                            ball.setCenterY(b.getY()+ BRICK_WIDTH + BALL_RADIUS);
                        } else if(ball.getCenterY() < b.getY()){
                            ball.setVelocities(ball.getXVel(), -ball.getYVel());
                            ball.setCenterY(b.getY()- BALL_RADIUS);
                        } else if(ball.getCenterX() > b.getX()+ BRICK_LENGTH){
                            ball.setVelocities(-ball.getXVel(), ball.getYVel());
                            ball.setCenterX(b.getX()+ BRICK_LENGTH + BALL_RADIUS);
                        } else {
                            ball.setVelocities(-ball.getXVel(), ball.getYVel());
                            ball.setCenterX(b.getX()- BALL_RADIUS);
                        }
                    }
                    b.updateLives();
                }
            }
        }
    }
    private void checkDeath(){
        if(ball1Dead() && ball2Dead()){
            gameOver = true;
        }
    }
    private boolean checkWin(){
        for(int i = 0; i < 10; i++){
            for(int j = 0; j < 3; j++){
                if(bricks[i][j].isAlive()){
                    return false;
                }
            }
        }
        return true;
    }
    private int getXSpeed(){
        int speed = (int)(Math.random() * 3) + 5;
        int sign = 0;
        while(sign == 0){
            sign = (int)(Math.random()*3)-1;
        }
        return speed * sign;
    }
    private int getYSpeed(){
        return (int)(2*Math.random()) + 6;
    }
    private int getPowerupPos(){
        return (int)((X-100)*Math.random()) + 50;
    }
    private void setPowerup(PowerupHolder p){
        int POWERUP_RADIUS = 20;
        if(p.getCenterX()+ POWERUP_RADIUS > paddle.getX() && p.getCenterX() - POWERUP_RADIUS < paddle.getX() + paddle.getWidth()){
            if(p.getCenterY()+ POWERUP_RADIUS > paddle.getY() && p.getCenterY() - POWERUP_RADIUS < paddle.getY() + PADDLE_WIDTH){
                p.gotHit();
                if(p.getPowerup() == Powerup.reverse){
                    reverse = true;
                } else if(p.getPowerup() == Powerup.paddleExtend){
                    paddleExtend = true;
                } else if(p.getPowerup() == Powerup.paddleShrink){
                    paddleShrink = true;
                }
                powerupExpireCounter = 0;
            }
        }
        if(p.getCenterY()- POWERUP_RADIUS > Y && !resetPowerup){
            addCount = true;
            powerupExpireCounter = 0;
            resetPowerup = true;
        }
    }
    private Powerup getPowerup(){
        int s = (int)(Math.random()*3);
        if(s == 0){
            return Powerup.reverse;
        } else if(s == 1){
            return Powerup.paddleExtend;
        }
        return Powerup.paddleShrink;
    }
    public static Color getMainColor(){
        return mainColor;
    }
    private String getTime(){
        int seconds = loopCounts*LOOP_COUNT/1000;
        int minute = 0;
        while(seconds >= 60){
            minute++;
            seconds -= 60;
        }
        if(seconds < 10){
            return minute + ":0" + seconds;
        }
        return minute + ":" + seconds;
    }
    private void reset(){
        gameOver = false;
        musicCount++;
        ball.setCenterX((X- BALL_RADIUS)/2);
        ball.setCenterY(Y-8* PADDLE_WIDTH);
        ball2.setCenterX((X- BALL_RADIUS)/2);
        ball2.setCenterY(Y+10);
        ball.setVelocities(getXSpeed(), getYSpeed());
        ball2.setVelocities(0, 0);
        ball.resetCombo();
        ball2.resetCombo();
        score = 0;
        addBallScore = 0;
        pause = false;
        reverse = false;
        paddleExtend = false;
        paddleShrink = false;
        powerupExpireCounter = 0;
        powerupStartCounter = 0;
        addCount = false;
        resetPowerup = false;
        startPowerup = true;
        loopCounts = 0;
        for(int i = 0; i < X/100; i++){
            for(int j = 0; j < Y/200; j++){
                bricks[i][j].resetLives(4-j);
            }
        }
        gameOverText.setText("");
        powerupHolder.reset(getPowerup(), getPowerupPos());
    }
    private boolean ball1Dead(){
        return ball.getCenterY()+ BALL_RADIUS >= Y;
    }
    private boolean ball2Dead(){
        return ball2.getCenterY() + BALL_RADIUS >= Y;
    }
}