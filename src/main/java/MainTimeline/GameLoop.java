package MainTimeline;

import javafx.animation.AnimationTimer;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class GameLoop {

    private Control control;
    private Player player;
    private playerBullets bullets;
    private enemySpawner spawner;      // add
    private enemyBullets enemyBullets; // add

    public GameLoop(Player player, Control control, playerBullets bullets,
                    enemySpawner spawner, enemyBullets enemyBullets) {
        this.player = player;
        this.control = control;
        this.bullets = bullets;
        this.spawner = spawner;
        this.enemyBullets = enemyBullets;
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
                spawner.update(now, player.getX()); // pass player X
            }
        };

        timer.start();
    }
}