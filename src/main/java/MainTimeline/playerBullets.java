package MainTimeline;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class playerBullets {

    private Player player;
    private Pane root;

    private List<ImageView> bullets = new ArrayList<>();
    private List<Long> spawnTimes = new ArrayList<>();

    private Image frame1 = new Image(getClass().getResource("/animation/shoot1.png").toExternalForm());
    private Image frame2 = new Image(getClass().getResource("/animation/shoot2.png").toExternalForm());

    private static final long FRAME_DURATION = 100_000_000L;

    //speed buff
    private boolean speedBuffActive = false;
    private long speedBuffEndTime = 0;
    private static final long BUFF_DURATION = 5_000_000_000L; // 5 seconds
    private static final double NORMAL_SPEED = 5;
    private static final double BUFFED_SPEED = 10;

    public playerBullets(Player player, Pane root) {
        this.player = player;
        this.root = root;
    }

    public void shoot() {
        ImageView bullet = new ImageView(frame1);

        bullet.setFitWidth(8);
        bullet.setPreserveRatio(true);
        bullet.setSmooth(false);

        bullet.setTranslateX(player.getSprite().getTranslateX() + 12);
        bullet.setTranslateY(player.getSprite().getTranslateY() - 20);

        bullets.add(bullet);
        spawnTimes.add(System.nanoTime());
        root.getChildren().add(bullet);

        
        
        // play shoot sfx
        try {
            Media sound = new Media(playerBullets.class.getResource("/SFX/shot1.wav").toExternalForm());
            MediaPlayer sfx = new MediaPlayer(sound);
            sfx.setVolume(0.2);
            sfx.play();
            sfx.setOnEndOfMedia(sfx::dispose);
        } catch (Exception ignored) {
        }
    }

    public void updateBullets(long now) {
        for (int i = 0; i < bullets.size(); i++) {
            long elapsed = now - spawnTimes.get(i);
            long frameIndex = (elapsed / FRAME_DURATION) % 2;
            bullets.get(i).setImage(frameIndex == 0 ? frame1 : frame2);
        }
    }

    public void activateSpeedBuff(long now) {
        speedBuffActive = true;
        speedBuffEndTime = now + BUFF_DURATION;
    }

    public boolean isSpeedBuffActive(long now) {
        if (speedBuffActive && now > speedBuffEndTime) {
            speedBuffActive = false;
        }
        return speedBuffActive;
    }

    public double getBulletSpeed(long now) {
        return isSpeedBuffActive(now) ? BUFFED_SPEED : NORMAL_SPEED;
    }

    public List<ImageView> getBullets() { return bullets; }
    public List<Long> getSpawnTimes() { return spawnTimes; }
    public Pane getRoot() { return root; } 
}