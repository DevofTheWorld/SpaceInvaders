package MainTimeline;

import javafx.scene.Scene;

public class Control {

    boolean upPressed = false;
    boolean downPressed = false;
    boolean leftPressed = false;
    boolean rightPressed = false;
    boolean spacePressed = false;

    private playerBullets bullets;

    
    private long lastShotTime = 0;
    
    
    private final long SHOOT_COOLDOWN =  200_000_000L; //Lower value, faster shooting
    
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
                case SPACE -> spacePressed = true;
            }
        });

        scene.setOnKeyReleased(e -> {
            switch (e.getCode()) {
                case W -> upPressed = false;
                case S -> downPressed = false;
                case A -> leftPressed = false;
                case D -> rightPressed = false;
                case SPACE -> spacePressed = false;
            }
        });

        }
                
        public void updateShooting(long now){
            
                    if (spacePressed && bullets != null) {
                        if (now - lastShotTime >= SHOOT_COOLDOWN){
                        bullets.shoot(); // shoot
                        lastShotTime = now;
                        }
                    }
                    
                    
                }
    }
