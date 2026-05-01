package MainTimeline;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class SpeedBuff {

    private ImageView sprite;
    private double x, y;
    private static final double FALL_SPEED = 2.0;

    public SpeedBuff(Pane root, double x, double y) {
        Image img = new Image(SpeedBuff.class.getResource("/Buff/orbBuff.png").toExternalForm());
        sprite = new ImageView(img);
        sprite.setFitWidth(20);
        sprite.setPreserveRatio(true);
        sprite.setSmooth(false);

        this.x = x;
        this.y = y;
        sprite.setTranslateX(x);
        sprite.setTranslateY(y);
        root.getChildren().add(sprite);
    }

    public void update() {
        y += FALL_SPEED;
        sprite.setTranslateY(y);
    }


    public double getY() { return y;
    }
    public ImageView getSprite() { return sprite;
    }

    public void remove(Pane root) {
        root.getChildren().remove(sprite);
    }
}