package MainTimeline;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class spaceDebris {

    private ImageView sprite;
    private double x;
    private double y;
    private double speedY;
    private double speedX;
    private double rotation = 0;
    private double rotationSpeed;
    private double size;

    public spaceDebris(Pane root) {
        Image img = new Image(spaceDebris.class.getResource("/asteroid.png").toExternalForm());
        sprite = new ImageView(img);
        sprite.setSmooth(false);
        sprite.setPreserveRatio(true);

        size = 40 + Math.random() * 30;
        sprite.setFitWidth(size);

        x = 20 + Math.random() * 680;
        y = -50;

        speedY = 1.5 + Math.random() * 2.0;   // slower than before
        speedX = (Math.random() - 0.5) * 1.5;
        rotationSpeed = (Math.random() - 0.5) * 6;

        sprite.setTranslateX(x);
        sprite.setTranslateY(y);

        root.getChildren().add(sprite);
    }

    public void update() {
        x += speedX;
        y += speedY;

        rotation += rotationSpeed;
        sprite.setRotate(rotation);

        sprite.setTranslateX(x);
        sprite.setTranslateY(y);
    }

    public void destroy(Pane root) {
        root.getChildren().remove(sprite);
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getSize() { return size; }
    public ImageView getSprite() { return sprite; }
}