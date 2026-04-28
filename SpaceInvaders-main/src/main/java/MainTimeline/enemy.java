package MainTimeline;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class enemy {

    private ImageView sprite;
    private double x;
    private double y;
    private double speed = 1.5;

    public enemy(Pane root) {
        Image img = new Image(enemy.class.getResource("/gray3.png").toExternalForm());
        sprite = new ImageView(img);
        sprite.setSmooth(false);
        sprite.setPreserveRatio(true);
        sprite.setFitWidth(40);

        x = 20 + Math.random() * 660;
        y = 0;

        sprite.setTranslateX(x);
        sprite.setTranslateY(y);

        root.getChildren().add(sprite);
    }

    public void update(double playerX) {
        if (x < playerX) x += speed;
        else if (x > playerX) x -= speed;

        sprite.setTranslateX(x);
        sprite.setTranslateY(y);
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public ImageView getSprite() { return sprite; }
}
