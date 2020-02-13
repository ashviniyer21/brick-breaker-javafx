import javafx.scene.shape.*;
import javafx.scene.paint.Color;
public class Brick extends Rectangle
{
    private int lives;
    public Brick(double x, double y, int lives){
        super(x, y, 75, 50); 
        this.lives = lives;
    }
    public void updateLives(){
        lives --;
        setColor();
    }
    public boolean isAlive(){
        return lives > 0;
    }
    public void setColor(){
        if(lives >= 4){
            setFill(Color.BLUE);
        } else if(lives == 3){
            setFill(Color.TURQUOISE);
        } else if(lives == 2){
            setFill(Color.AQUAMARINE);
        } else if(lives == 1){
            setFill(Color.SPRINGGREEN);
        } else if(lives <= 0){
            setFill(BrickBreaker.getMainColor());
        }
    }
    public void resetLives(int lives){
        this.lives = lives;
        setColor();
    }
    public boolean almostDead(){
        return lives == 1;
    }
}