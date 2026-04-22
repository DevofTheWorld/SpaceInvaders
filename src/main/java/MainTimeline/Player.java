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
        this.x = startX;
        this.y = startY;

        sprite.setTranslateX(x);
        sprite.setTranslateY(y);

        frameIdle  = new Image(getClass().getResource("/animation/enemy1.png").toExternalForm());
        frameRight = new Image(getClass().getResource("/animation/enemy2.png").toExternalForm());
        frameLeft  = new Image(getClass().getResource("/animation/enemy3.png").toExternalForm());

        sprite.setImage(frameIdle);
    }

    public void move(double dx, double dy, boolean isMoving, long now) {

        if (dx != 0 && dy != 0) {
            dx *= 0.7071;
            dy *= 0.7071;
        }

        x += dx * speed;
        y += dy * speed;

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
}