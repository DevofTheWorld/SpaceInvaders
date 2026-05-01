package MainTimeline;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class BossBeam {

    private static final Image BEAM_IMG = new Image(
            BossBeam.class.getResource("/ui/bossfire2.png").toExternalForm()
    );

    private static final double BEAM_WIDTH  = 110.0;
    private static final long   LINGER_NS   = 2_300_000_000L;  // 2.3 s visible
    private static final int    DAMAGE      = 2;

    private static final int SLICES = 64;

    // How much of the beam (from the top) is part of the fade zone.
    // 0.08 = only the top 8% of slices fade; everything below is fully opaque.
    private static final double FADE_ZONE = 0.08;

    private final ImageView[] slices = new ImageView[SLICES];
    private final double beamTop;
    private final double beamHeight;

    private final long   spawnTime;
    private boolean done = false;

    private final double[] baseOpacity = new double[SLICES];

    public BossBeam(Pane root, double spawnX, double spawnY, long now) {
        spawnTime  = now;
        beamTop    = spawnY;
        beamHeight = 780 - spawnY;

        double left   = spawnX - BEAM_WIDTH / 2;
        double sliceH = beamHeight / SLICES;

        for (int i = 0; i < SLICES; i++) {
            // t goes 0.0 (top) → 1.0 (bottom)
            double t = (double) i / (SLICES - 1);

            // Only fade within the top FADE_ZONE fraction of slices.
            // Inside that zone use a quadratic so the very tip is near-invisible
            // and it snaps to fully opaque quickly. Everything below is 0.95.
            if (t < FADE_ZONE) {
                double localT = t / FADE_ZONE;          // 0.0 → 1.0 within fade zone
                baseOpacity[i] = localT * localT * 0.95; // quadratic ramp
            } else {
                baseOpacity[i] = 0.95;
            }

            ImageView iv = new ImageView(BEAM_IMG);
            iv.setFitWidth(BEAM_WIDTH);
            iv.setFitHeight(sliceH + 1);   // +1 to avoid hairline gaps
            iv.setPreserveRatio(false);
            iv.setSmooth(true);
            iv.setOpacity(baseOpacity[i]);
            iv.setTranslateX(left);
            iv.setTranslateY(beamTop + i * sliceH);

            // Crop the correct vertical strip from the source texture
            javafx.scene.image.WritableImage crop = new javafx.scene.image.WritableImage(
                    BEAM_IMG.getPixelReader(),
                    0,
                    (int)(BEAM_IMG.getHeight() * i / SLICES),
                    (int) BEAM_IMG.getWidth(),
                    Math.max(1, (int)(BEAM_IMG.getHeight() / SLICES))
            );
            iv.setImage(crop);

            slices[i] = iv;
            root.getChildren().add(iv);
        }
    }

    /**
     * Call every frame. Returns true when the beam is done and should be removed.
     */
    public boolean update(long now) {
        if (done) return true;

        long age = now - spawnTime;
        if (age >= LINGER_NS) {
            done = true;
            return true;
        }

        // Fade out all slices uniformly in the last 200 ms
        long   fadeStart = LINGER_NS - 200_000_000L;
        double fadeMult  = 1.0;
        if (age > fadeStart) {
            fadeMult = 1.0 - (double)(age - fadeStart) / 200_000_000L;
        }

        for (int i = 0; i < SLICES; i++) {
            slices[i].setOpacity(baseOpacity[i] * fadeMult);
        }

        return false;
    }

    public boolean hitsPlayer(Player player) {
        if (done) return false;
        for (int i = SLICES - 1; i >= 0; i--) {
            if (slices[i].getBoundsInParent()
                         .intersects(player.getSprite().getBoundsInParent())) {
                return true;
            }
        }
        return false;
    }

    public int getDamage() { return DAMAGE; }

    public void remove(Pane root) {
        for (ImageView iv : slices) root.getChildren().remove(iv);
    }

    public ImageView getSprite() { return slices[SLICES - 1]; }
}
