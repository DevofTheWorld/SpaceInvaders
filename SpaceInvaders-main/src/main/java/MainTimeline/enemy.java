package MainTimeline;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class enemy {

    private ImageView sprite;
    private double x;
    private double y;
    private double speed = 1.5;
    private int health = 3;

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

    // returns true if enemy is dead
    public boolean takeDamage() {
        health--;
        // flash white to show hit
        sprite.setOpacity(0.4);
        new Thread(() -> {
            try { Thread.sleep(80); } catch (InterruptedException ignored) {}
            javafx.application.Platform.runLater(() -> sprite.setOpacity(1.0));
        }).start();
        return health <= 0;
    }

    public boolean collidesWith(ImageView other) {
        return sprite.getBoundsInParent().intersects(other.getBoundsInParent());
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public ImageView getSprite() { return sprite; }
}