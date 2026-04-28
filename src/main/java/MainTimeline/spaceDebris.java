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

    public spaceDebris(Pane root) {
        Image img = new Image(spaceDebris.class.getResource("/asteroid.png").toExternalForm());
        sprite = new ImageView(img);
        sprite.setSmooth(false);
        sprite.setPreserveRatio(true);
        sprite.setFitWidth(40 + Math.random() * 30);

        x = 20 + Math.random() * 680;
        y = -50;

        //random fall speed and drift
        speedY = 1.5 + Math.random() * 2.0;
        speedX = (Math.random() - 0.5) * 1.5;
        rotationSpeed = (Math.random() - 0.5) * 4;

        sprite.setTranslateX(x);
        sprite.setTranslateY(y);

        root.getChildren().add(sprite);
    }

    public void update() {
        x += speedX;
        y += speedY;

        // spin the asteroid every frame
        rotation += rotationSpeed;
        sprite.setRotate(rotation);

        sprite.setTranslateX(x);
        sprite.setTranslateY(y);
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public ImageView getSprite() { return sprite; }
}
