package MainTimeline;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class AsteroidExplosion {

    private ImageView sprite;
    private Image[] frames = new Image[5];
    private long startTime;
    private static final long FRAME_DURATION = 60_000_000L;   // 60ms per frame
    private static final long TOTAL_DURATION = 300_000_000L;  // 5 frames x 60ms
    private boolean finished = false;

    public AsteroidExplosion(Pane root, double x, double y, double size) {
        for (int i = 0; i < 5; i++) {
            frames[i] = new Image(
                    AsteroidExplosion.class.getResource("/Explosion/explosion" + (i + 1) + ".png").toExternalForm()
            );
        }

        sprite = new ImageView(frames[0]);
        sprite.setFitWidth(size);
        sprite.setPreserveRatio(true);
        sprite.setSmooth(false);

        sprite.setTranslateX(x - size / 2);
        sprite.setTranslateY(y - size / 2);

        root.getChildren().add(sprite);
        startTime = System.nanoTime();

        //play explosion sfx
        try {
            Media sound = new Media(AsteroidExplosion.class.getResource("/SFX/explosion.wav").toExternalForm());
            MediaPlayer sfx = new MediaPlayer(sound);
            sfx.setVolume(0.7);
            sfx.play();
            // auto-dispose when done so it doesn't leak memory
            sfx.setOnEndOfMedia(sfx::dispose);
        } catch (Exception ignored) {}
    }

    public boolean update(long now) {
        long elapsed = now - startTime;
        if (elapsed >= TOTAL_DURATION) {
            finished = true;
            return true;
        }
        int frameIndex = (int) (elapsed / FRAME_DURATION);
        frameIndex = Math.min(frameIndex, frames.length - 1); // safety clamp
        sprite.setImage(frames[frameIndex]);
        return false;
    }

    public void remove(Pane root) {
        root.getChildren().remove(sprite);
    }

    public boolean isFinished() { return finished; }
}