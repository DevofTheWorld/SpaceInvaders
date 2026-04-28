package MainTimeline;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Player {

    private double x;
    private double y;
    private double speed = 3;

    private ImageView sprite;

    private Image frameIdle;
    private Image frameRight;
    private Image frameLeft;

    public Player(ImageView sprite, double startX, double startY) {
        this.sprite = sprite;
        
        sprite.setScaleX(1.5);
        sprite.setScaleY(1.5);
        sprite.setSmooth(false);
        
        
        frameIdle  = new Image(getClass().getResource("/animation/enemy1.png").toExternalForm());
        frameRight = new Image(getClass().getResource("/animation/enemy2.png").toExternalForm());
        frameLeft  = new Image(getClass().getResource("/animation/enemy3.png").toExternalForm());

        
        this.x = 360 - (frameIdle.getWidth() / 2);
        this.y = 560 - frameIdle.getHeight() - 30;

        sprite.setTranslateX(x);
        sprite.setTranslateY(y);

        sprite.setImage(frameIdle);
    }
   

    public void move(double dx, double dy, boolean isMoving, long now) {

        if (dx != 0 && dy != 0) {
            dx *= 0.7071;
            dy *= 0.7071;
        }
        
        //Window borders
        double nextX = x + (dx*speed);
        double nextY = y + (dy*speed);

        double minX = 0;
        double minY = 0;
        double maxX = 720 - sprite.getBoundsInLocal().getWidth();
        double maxY = 720 - sprite.getBoundsInLocal().getHeight();
        
        //Clamp
        if (nextX < minX) nextX = minX;
        if (nextX > maxX) nextX = maxX;
        if (nextY < minY) nextY = minY;
        if (nextY > maxY) nextY = maxY;
        
        x = nextX;
        y = nextY;
        
       /* x += dx * speed;
        y += dy * speed;
       */

        sprite.setTranslateX(x);
        sprite.setTranslateY(y);

        if (!isMoving) {
            sprite.setImage(frameIdle);
        } else if (dx > 0) {
            sprite.setImage(frameRight);
        } else if (dx < 0) {
            sprite.setImage(frameLeft);
        } else {
            sprite.setImage(frameIdle);
        }
    }
    
        public ImageView getSprite() {
        return sprite;

         }

        public double getX() {
        return x;

        }
    
}