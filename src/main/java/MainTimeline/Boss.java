package MainTimeline;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

/**
 * Boss enemy.
 *
 * Animation frames:  boss1.png → boss2.png → boss3.png → boss2.png → boss1.png  (loops)
 * Pre-fire frame:    bossfire1.png   (charge-up, shown for ~1 s before the beam)
 * Fire frame:        bossfire2.png   (actual beam — handled by BossBeam)
 *
 * Every 4 seconds the boss plays the charge animation, then BossBeam is spawned
 * by GameLoop (which reads isReadyToFire()).
 *
 * Before charging, the boss spends WINDUP_DUR decelerating to a full stop,
 * then holds position while the charge frame plays.
 */
public class Boss {

    // ── sprites ──────────────────────────────────────────────────────────────
    private final ImageView sprite;

    private final Image[] idleFrames = new Image[3];   // boss1-3.png
    private final Image chargeFrame;                   // bossfire1.png

    // idle animation: 1→2→3→2→1  =  indices 0,1,2,1,0
    private static final int[] ANIM_SEQ   = {0, 1, 2, 1, 0};
    private static final long  FRAME_NS   = 120_000_000L;   // 120 ms per idle frame

    // ── position / movement ───────────────────────────────────────────────────
    private double x;
    private double y = -120;                            // starts off-screen top
    private static final double TARGET_Y  = 60;        // rests here
    private static final double ENTER_SPD = 2.0;
    private boolean entered = false;

    private double moveDir  = 1;                        // 1 = right, -1 = left
    private static final double MOVE_SPD  = 1.8;

    // bounds — keep boss fully inside the 720px wide screen
    private static final double SPRITE_W  = 250;
    private static final double LEFT_CAP  = -50;
    private static final double RIGHT_CAP = 400 - SPRITE_W;   // 470

    // ── combat ────────────────────────────────────────────────────────────────
    public static final int MAX_HP = 80;
    private int hp = MAX_HP;
    private boolean dead = false;

    // beam fire timing
    private static final long FIRE_INTERVAL  = 4_000_000_000L;  // every 4 s
    private static final long CHARGE_DUR     = 1_000_000_000L;  // 1 s charge
    private static final long INVINCIBLE_DUR = 1_000_000_000L;  // 1 s invincibility after firing
    private long lastFireTime  = 0;
    private boolean charging   = false;
    private boolean readyToFire = false;
    private long chargeStart   = 0;
    private long invincibleEnd = 0;          // invincibility window end timestamp

    // pre-charge wind-down (boss slows to a stop before charging)
    private static final long WINDUP_DUR = 800_000_000L;   // 0.8 s slow-down window
    private boolean windingUp  = false;
    private long    windupStart = 0;

    // flash on hit
    private long flashEnd = 0;

    // freeze (while beam is active)
    private boolean frozen = false;

    public Boss(Pane root, long now) {
        for (int i = 0; i < 3; i++) {
            idleFrames[i] = new Image(
                    Boss.class.getResource("/ui/boss" + (i + 1) + ".png").toExternalForm());
        }
        chargeFrame = new Image(
                Boss.class.getResource("/ui/bossfire1.png").toExternalForm());

        sprite = new ImageView(idleFrames[0]);
        sprite.setFitWidth(500);
        sprite.setFitHeight(345);
        sprite.setPreserveRatio(false);
        sprite.setSmooth(false);

        x = 235;   // centre-ish (720/2 - 250/2)
        sprite.setTranslateX(x);
        sprite.setTranslateY(y);

        root.getChildren().add(sprite);
        lastFireTime = now + 2_000_000_000L; // first shot after 2 s once entered
    }

    /** Called every frame from GameLoop. Returns true when boss is fully dead. */
    public boolean update(long now) {
        if (dead) return true;

        // ── entry glide ───────────────────────────────────────────────────────
        if (!entered) {
            y += ENTER_SPD;
            if (y >= TARGET_Y) { y = TARGET_Y; entered = true; lastFireTime = now; }
            sprite.setTranslateY(y);

            // idle animation during entry
            int seqIdx = (int) ((now / FRAME_NS) % ANIM_SEQ.length);
            sprite.setImage(idleFrames[ANIM_SEQ[seqIdx]]);

            return false;
        }

        // ── x-axis patrol (skipped while winding up, charging, or beam-frozen) ─
        if (!frozen && !windingUp && !charging) {
            x += MOVE_SPD * moveDir;
            if (x >= RIGHT_CAP) { x = RIGHT_CAP; moveDir = -1; }
            if (x <= LEFT_CAP)  { x = LEFT_CAP;  moveDir =  1; }
            sprite.setTranslateX(x);
        }

        // ── beam fire cycle ──────────────────────────────────────────────────

        // Step 1 — start wind-up (decelerate to stop) before charging
        if (!windingUp && !charging && now - lastFireTime >= FIRE_INTERVAL) {
            windingUp   = true;
            windupStart = now;
        }

        // Step 2 — during wind-up, ease speed from full → 0 and move at reduced rate
        if (windingUp && !charging) {
            double progress = (double)(now - windupStart) / WINDUP_DUR;  // 0.0 → 1.0
            progress = Math.min(progress, 1.0);

            // ease-out: speed drops from MOVE_SPD → 0
            double slowedSpeed = MOVE_SPD * (1.0 - progress);
            x += slowedSpeed * moveDir;
            if (x >= RIGHT_CAP) { x = RIGHT_CAP; moveDir = -1; }
            if (x <= LEFT_CAP)  { x = LEFT_CAP;  moveDir =  1; }
            sprite.setTranslateX(x);

            // once wind-up completes, transition into charge frame
            if (progress >= 1.0) {
                windingUp   = false;
                charging    = true;
                chargeStart = now;
                sprite.setImage(chargeFrame);
            }
        }

        // Step 3 — after charge duration elapses, signal ready to fire
        if (charging && now - chargeStart >= CHARGE_DUR) {
            charging      = false;
            readyToFire   = true;
            lastFireTime  = now;
            invincibleEnd = now + INVINCIBLE_DUR;   // start invincibility window
            sprite.setImage(idleFrames[0]);
        }

        // ── idle animation (only when not winding up or charging) ────────────
        if (!windingUp && !charging) {
            int seqIdx = (int) ((now / FRAME_NS) % ANIM_SEQ.length);
            sprite.setImage(idleFrames[ANIM_SEQ[seqIdx]]);
        }

        // ── hit flash ────────────────────────────────────────────────────────
        if (now < flashEnd) {
            sprite.setOpacity(0.4);
        } else {
            sprite.setOpacity(1.0);
        }

        return false;
    }

    /** Returns true and resets the flag — call exactly once per check. */
    public boolean pollReadyToFire() {
        if (readyToFire) { readyToFire = false; return true; }
        return false;
    }

    /** @return true if boss is now dead. Ignores damage during invincibility window. */
    public boolean takeDamage(long now) {
        if (dead) return false;
        if (now < invincibleEnd) return false;   // invincible — ignore hit
        hp--;
        flashEnd = now + 80_000_000L;
        if (hp <= 0) { dead = true; return true; }
        return false;
    }

    public boolean collidesWith(ImageView other) {
        return sprite.getBoundsInParent().intersects(other.getBoundsInParent());
    }

    public void setFrozen(boolean frozen) { this.frozen = frozen; }

    public void remove(Pane root) { root.getChildren().remove(sprite); }

    public boolean isEntered()    { return entered; }
    public boolean isDead()       { return dead; }
    public int  getHp()           { return hp; }
    public double getX()          { return x + SPRITE_W / 2; }       // centre
    public double getY()          { return y + 70; }                   // mid-body
    public ImageView getSprite()  { return sprite; }

    /** Beam spawns from bottom-centre of boss sprite, higher up */
    public double getBeamX()  { return x + 255; }
    public double getBeamY()  { return y + 115; }   // roughly mid-body, higher up
}