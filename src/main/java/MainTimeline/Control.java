package MainTimeline;

import javafx.scene.Scene;

public class Control {

    boolean upPressed = false;
    boolean downPressed = false;
    boolean leftPressed = false;
    boolean rightPressed = false;
    boolean dashPressed = false;
    boolean fireHeld = false; //tracks if space is being held

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
                case Q -> dashPressed = true;
                case SPACE -> fireHeld = true;
            }
        });

        scene.setOnKeyReleased(e -> {
            switch (e.getCode()) {
                case W -> upPressed = false;
                case S -> downPressed = false;
                case A -> leftPressed = false;
                case D -> rightPressed = false;
                case Q -> dashPressed = false;
                case SPACE -> fireHeld = false;
            }
        });
    }
}