package MainTimeline;

import javafx.animation.AnimationTimer;
import javafx.scene.image.ImageView;

public class GameLoop {

    private Control control;
    private Player player;
    private playerBullets bullets;

    public GameLoop(Player player, Control control, playerBullets bullets) {
        this.player = player;
        this.control = control;
        this.bullets = bullets;
    }

    public void start() {

        AnimationTimer timer = new AnimationTimer() {

            @Override
            public void handle(long now) {

                double dx = 0;
                double dy = 0;

                if (control.upPressed) dy -= 1;
                if (control.downPressed) dy += 1;
                if (control.leftPressed) dx -= 1;
                if (control.rightPressed) dx += 1;

                boolean moving = dx != 0 || dy != 0;

                player.move(dx, dy, moving, now);

                for (ImageView bullet : bullets.getBullets()) {
                    bullet.setTranslateY(bullet.getTranslateY() - 5);
                }

                bullets.updateBullets(now);
            }
        };

        timer.start();
    }
}