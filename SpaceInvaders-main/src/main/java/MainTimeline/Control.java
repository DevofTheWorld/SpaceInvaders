package MainTimeline;

import javafx.scene.Scene;

public class Control {

    boolean upPressed = false;
    boolean downPressed = false;
    boolean leftPressed = false;
    boolean rightPressed = false;

    private playerBullets bullets;

    public void setBullets(playerBullets bullets) {
        this.bullets = bullets;
    }

    public void setup(Scene scene) {

        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case W -> upPressed = true;
                case S -> downPressed = true;
                case A -> leftPressed = true;
                case D -> rightPressed = true;
                case SPACE -> {
                    if (bullets != null) bullets.shoot(); // shoot
                }
            }
        });

        scene.setOnKeyReleased(e -> {
            switch (e.getCode()) {
                case W -> upPressed = false;
                case S -> downPressed = false;
                case A -> leftPressed = false;
                case D -> rightPressed = false;
            }
        });
    }
}