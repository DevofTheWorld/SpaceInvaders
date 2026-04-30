package MainTimeline;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class enemy {

    private ImageView sprite;
    private double x;
    private double y;
    private double speedX;
    private double speedY = 0.5;
    private int health = 3;

    private long spawnTime;
    private static final long KAMIKAZE_DELAY = 10_000_000_000L;

    private boolean kamikazeMode = false;
    private double kamikazeDX = 0;
    private double kamikazeDY = 0;
    private static final double KAMIKAZE_SPEED = 6.0;

    private boolean dead = false;
    private double currentRotation = 0;

    private static final double TOP_THIRD = 720 * 0.33;

    public enemy(Pane root, long now) {
        Image img = new Image(enemy.class.getResource("/gray3.png").toExternalForm());
        sprite = new ImageView(img);
        sprite.setSmooth(false);
        sprite.setPreserveRatio(true);
        sprite.setFitWidth(40);

        x = 20 + Math.random() * 660;
        y = -40;

        speedX = (Math.random() - 0.5) * 1.2;

        sprite.setTranslateX(x);
        sprite.setTranslateY(y);

        root.getChildren().add(sprite);
        spawnTime = now;
    }

    public void update(long now, double playerX, double playerY) {
        if (kamikazeMode) {
            x += kamikazeDX * KAMIKAZE_SPEED;
            y += kamikazeDY * KAMIKAZE_SPEED;
        } else {
            if (y < TOP_THIRD) {
                if (x < playerX) x += Math.min(1.5, playerX - x) * 0.06;
                else if (x > playerX) x -= Math.min(1.5, x - playerX) * 0.06;

                y += speedY;
            } else {
                if (x < playerX) x += Math.min(2.5, playerX - x) * 0.14;
                else if (x > playerX) x -= Math.min(2.5, x - playerX) * 0.14;

                y += (Math.random() - 0.5) * 1.5;
            }

            if (x < 10 || x > 670) speedX *= -1;
            x += speedX;

            y = Math.max(-40, Math.min(TOP_THIRD, y));

            if (now - spawnTime >= KAMIKAZE_DELAY) {
                triggerKamikaze(playerX, playerY);
            }
        }

        // smooth tilt toward player X — works in both normal and kamikaze
        double targetAngle = (x - playerX) * 0.05;
        targetAngle = Math.max(-25, Math.min(25, targetAngle));
        currentRotation += (targetAngle - currentRotation) * 0.25;
        sprite.setRotate(currentRotation);

        sprite.setTranslateX(x);
        sprite.setTranslateY(y);
    }

    private void triggerKamikaze(double playerX, double playerY) {
        kamikazeMode = true;
        double dirX = playerX - x;
        double dirY = playerY - y;
        double len = Math.sqrt(dirX * dirX + dirY * dirY);
        if (len == 0) len = 1;
        kamikazeDX = dirX / len;
        kamikazeDY = dirY / len;
        sprite.setOpacity(0.6);
    }

    public boolean isKamikaze() { return kamikazeMode; }

    public boolean takeDamage() {
        health--;
        sprite.setOpacity(0.4);
        new Thread(() -> {
            try { Thread.sleep(80); } catch (InterruptedException ignored) {}
            javafx.application.Platform.runLater(() -> {
                if (!dead) sprite.setOpacity(1.0);
            });
        }).start();
        if (health <= 0) {
            dead = true;
            return true;
        }
        return false;
    }

    public boolean collidesWith(ImageView other) {
        return sprite.getBoundsInParent().intersects(other.getBoundsInParent());
    }

    public double getX() {
        return x; }
    public double getY() {
        return y; }
    public ImageView getSprite() {
        return sprite; }
}