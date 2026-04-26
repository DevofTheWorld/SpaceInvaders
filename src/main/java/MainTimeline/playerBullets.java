package MainTimeline;

import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.layout.Pane;

public class playerBullets {

    private Player player;
    private Pane root;

    private List<ImageView> bullets = new ArrayList<>();
    private List<Long> spawnTimes = new ArrayList<>(); // track when each bullet was created

    // Load your two bullet frames
    private Image frame1 = new Image(getClass().getResource("/shoot1.png").toExternalForm());
    private Image frame2 = new Image(getClass().getResource("/shoot2.png").toExternalForm());

    private static final long FRAME_DURATION = 100_000_000L; // switch frame every 100ms

    public playerBullets(Player player, Pane root) {
        this.player = player;
        this.root = root;
    }

    public void shoot() {
        ImageView bullet = new ImageView(frame1); // start on frame 1


        bullet.setFitWidth(8);
        bullet.setPreserveRatio(true);
        bullet.setSmooth(false);

        bullet.setTranslateX(player.getSprite().getTranslateX() + 12);
        bullet.setTranslateY(player.getSprite().getTranslateY() - 20);

        bullets.add(bullet);
        spawnTimes.add(System.nanoTime());
        root.getChildren().add(bullet);
    }

    // Call this every frame from GameLoop, passing in the current `now` timestamp
    public void updateBullets(long now) {
        for (int i = 0; i < bullets.size(); i++) {
            long elapsed = now - spawnTimes.get(i);
            // alternate between frame 1 and 2
            long frameIndex = (elapsed / FRAME_DURATION) % 2;
            bullets.get(i).setImage(frameIndex == 0 ? frame1 : frame2);
        }
    }

    public List<ImageView> getBullets() {
        return bullets;
    }

    public Pane getRoot() {
        return root;
    }
}