import javafx.scene.shape.*;
import javafx.scene.paint.Color;
public class PowerupHolder extends Circle
{
    private Powerup powerup;
    public PowerupHolder(Powerup powerup, int x){
        super(x, -20, 20);
        setFill(Color.PURPLE);
        this.powerup = powerup;
    }
    public void move(){
        setCenterY(getCenterY()+5);
    }
    public Powerup getPowerup(){
        return powerup;
    }
    public void gotHit(){
        setFill(BrickBreaker.getMainColor());
    }
    public void reset(Powerup p, int x){
        setCenterY(-20);
        this.powerup = p;
        setCenterX(x);
        setFill(Color.PURPLE);
    }
}