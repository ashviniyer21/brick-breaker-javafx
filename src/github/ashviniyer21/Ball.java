import javafx.scene.shape.*;
import javafx.scene.paint.Color; 
public class Ball extends Circle
{
    private int xVel = 0;
    private int yVel = 0;
    private int combo = 1;
    public Ball(int x, int y){
        super(x, y, 10);
        //setFill(BrickBreaker.getMainColor());
        combo = 1;
    }
    public void setVelocities(int xVel, int yVel){
        this.xVel = xVel;
        this.yVel = yVel;
    }
    public void update(){
        setCenterX(getCenterX()+xVel);
        setCenterY(getCenterY()+yVel);
    }
    public int getXVel(){
        return xVel;
    }
    public int getYVel(){
        return yVel;
    }
    public void resetCombo(){
        combo = 1;
    }
    public void updateCombo(){
        combo++;
    }
    public int getCombo(){
        return combo;
    }
}