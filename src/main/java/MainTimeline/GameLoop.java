package MainTimeline;

import javafx.animation.AnimationTimer;
import javafx.scene.image.ImageView;

public class GameLoop {

    private Control control;
    private Player player;
    private playerBullets bullets;
    private enemySpawner spawner;
    private enemyBullets enemyBullets;
    private asteroidSpawner asteroidSpawner; // add

    public GameLoop(Player player, Control control, playerBullets bullets,
                    enemySpawner spawner, enemyBullets enemyBullets,
                    asteroidSpawner asteroidSpawner) { // add
        this.player = player;
        this.control = control;
        this.bullets = bullets;
        this.spawner = spawner;
        this.enemyBullets = enemyBullets;
        this.asteroidSpawner = asteroidSpawner; // add
    }

    public void start() {

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {

                // --- player movement ---
                double dx = 0, dy = 0;
                if (control.upPressed) dy -= 1;
                if (control.downPressed) dy += 1;
                if (control.leftPressed) dx -= 1;
                if (control.rightPressed) dx += 1;

                boolean moving = dx != 0 || dy != 0;
                player.move(dx, dy, moving, now);

                // --- player bullets ---
                bullets.getBullets().removeIf(b -> {
                    if (b.getTranslateY() < -20) {
                        bullets.getRoot().getChildren().remove(b);
                        return true;
                    }
                    return false;
                });

                for (ImageView b : bullets.getBullets()) {
                    b.setTranslateY(b.getTranslateY() - 5);
                }

                bullets.updateBullets(now);

                // --- enemies + enemy bullets ---
                spawner.update(now, player.getX());

                // --- asteroids ---
                asteroidSpawner.update(now); // add
            }
        };

        timer.start();
    }
}