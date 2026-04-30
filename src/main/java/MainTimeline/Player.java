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

    public int maxHealth = 3;
    public int health = maxHealth;

    private boolean invincible = false;
    private long invincibleTimer = 0;
    private static final long INVINCIBLE_DURATION = 1_500_000_000L;

    // dash
    private boolean dashing = false;
    private long dashTimer = 0;
    private static final long DASH_DURATION = 200_000_000L;   // 0.2s dash
    private static final long DASH_COOLDOWN = 1_000_000_000L; // 1s cooldown
    private long lastDashTime = 0;
    private double dashDX = 0;
    private double dashDY = 0;
    private static final double DASH_SPEED = 12;

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

    public void dash(double dx, double dy, long now) {
        if (dashing) return;
        if (now - lastDashTime < DASH_COOLDOWN) return;

        // if not moving, dash upward by default
        if (dx == 0 && dy == 0) {
            dashDX = 0;
            dashDY = -1;
        } else {
            double len = Math.sqrt(dx * dx + dy * dy);
            dashDX = dx / len;
            dashDY = dy / len;
        }

        dashing = true;
        dashTimer = now;
        lastDashTime = now;
        sprite.setOpacity(0.6);
    }

    public void move(double dx, double dy, boolean isMoving, long now) {

        if (dashing) {
            long elapsed = now - dashTimer;
            if (elapsed > DASH_DURATION) {
                dashing = false;
                sprite.setOpacity(invincible ? 0.3 : 1.0);
            } else {
                double nextX = x + dashDX * DASH_SPEED;
                double nextY = y + dashDY * DASH_SPEED;
                nextX = Math.max(0, Math.min(720 - sprite.getBoundsInLocal().getWidth(), nextX));
                nextY = Math.max(0, Math.min(720 - sprite.getBoundsInLocal().getHeight(), nextY));
                x = nextX;
                y = nextY;
                sprite.setTranslateX(x);
                sprite.setTranslateY(y);
                return; // skip normal movement while dashing
            }
        }

        if (dx != 0 && dy != 0) {
            dx *= 0.7071;
            dy *= 0.7071;
        }

        double nextX = x + (dx * speed);
        double nextY = y + (dy * speed);

        double minX = 0;
        double minY = 0;
        double maxX = 720 - sprite.getBoundsInLocal().getWidth();
        double maxY = 720 - sprite.getBoundsInLocal().getHeight();

        if (nextX < minX) nextX = minX;
        if (nextX > maxX) nextX = maxX;
        if (nextY < minY) nextY = minY;
        if (nextY > maxY) nextY = maxY;

        x = nextX;
        y = nextY;

        sprite.setTranslateX(x);
        sprite.setTranslateY(y);

        // invincibility
        if (invincible) {
            long elapsed = now - invincibleTimer;
            if (elapsed > INVINCIBLE_DURATION) {
                invincible = false;
                sprite.setOpacity(1.0);
            } else {
                sprite.setOpacity((elapsed / 100_000_000L) % 2 == 0 ? 0.3 : 1.0);
            }
        }

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

    public boolean takeDamage(long now) {
        if (invincible || dashing) return false; // dash also grants invincibility
        health--;
        invincible = true;
        invincibleTimer = now;
        System.out.println("Player hit! Health: " + health);
        return true;
    }

    public boolean isDashing() { return dashing; }
    public boolean isDead() { return health <= 0; }
    public int getHealth() { return health; }
    public ImageView getSprite() { return sprite; }
    public double getX() { return x; }
    public double getY() { return y; }
}