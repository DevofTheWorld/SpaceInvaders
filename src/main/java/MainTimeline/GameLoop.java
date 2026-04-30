package MainTimeline;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Cursor;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class GameLoop {

    // dependencies
    private final Control control;
    private final Player player;
    private final playerBullets bullets;
    private final enemySpawner spawner;
    private final enemyBullets enemyBullets;
    private final asteroidSpawner asteroidSpawner;
    private final Pane gameRoot;
    private final Stage stage;
    private javafx.scene.media.MediaPlayer mediaPlayer;

    // Player HP bar
    private static final int    PLAYER_MAX_HP    = 3;
    private static final double PLR_BAR_W        = 220;
    private static final double PLR_BAR_H        = 14;
    private static final double PLR_BAR_X        = 25;
    private static final double PLR_BAR_Y        = 670;   // bottom-left
    private static final double PLR_SEG_GAP      = 4;

    // One rectangle per HP segment + a shared scanline
    private final List<Rectangle> plrSegFills    = new ArrayList<>();
    private final List<Rectangle> plrSegGlows    = new ArrayList<>();
    private Rectangle              plrScanline;
    private Text                   plrHpLabel;
    private Timeline               plrPulse;
    private Timeline               plrScanlineAnim;
    private Color                  plrCurrentColor = Color.web("#00ffe7");
    private int                    lastKnownHp     = PLAYER_MAX_HP;

    // Boss HP bar
    private static final double HP_BAR_W = 400;
    private static final double HP_BAR_H = 14;
    private static final double HP_BAR_X = 160;
    private static final double HP_BAR_Y = 22;

    private Rectangle bossHpTrack;
    private Rectangle bossHpFill;
    private Rectangle bossHpGlowRect;
    private Rectangle bossHpScanline;
    private Text      bossHpLabel;
    private Text      bossHpPercent;
    private Timeline  hpPulseTimeline;
    private Timeline  scanlineTimeline;
    private Color     currentFillColor = Color.web("#00ffe7");

    // state
    public int  killCount   = 0;
    private static final int BOSS_KILL_THRESHOLD = 7;

    private boolean bossPhaseTriggered = false;
    private boolean bossMusicPlaying   = false;
    private boolean bossHpBarShown     = false;
    private boolean bossSpawned        = false;
    private Boss    boss               = null;
    private final List<BossBeam> bossBeams = new ArrayList<>();

    private javafx.scene.media.MediaPlayer bossMusicPlayer;
    private Timeline activeFadeOut = null;

    private long lastFireTime = 0;
    private static final long FIRE_RATE = 200_000_000L;

    //Constructor
    public GameLoop(Player player, Control control, playerBullets bullets,
                    enemySpawner spawner, enemyBullets enemyBullets,
                    asteroidSpawner asteroidSpawner, Pane gameRoot, Stage stage,
                    javafx.scene.media.MediaPlayer mediaPlayer) {
        this.player          = player;
        this.control         = control;
        this.bullets         = bullets;
        this.spawner         = spawner;
        this.enemyBullets    = enemyBullets;
        this.asteroidSpawner = asteroidSpawner;
        this.gameRoot        = gameRoot;
        this.stage           = stage;
        this.mediaPlayer     = mediaPlayer;

        showPlayerHpBar();
    }

    //  Helper builders
    private ImageView makeImageButton(String path, double width) {
        Image img = new Image(GameLoop.class.getResource(path).toExternalForm());
        ImageView btn = new ImageView(img);
        btn.setFitWidth(width);
        btn.setPreserveRatio(true);
        btn.setCursor(Cursor.HAND);
        btn.addEventHandler(MouseEvent.MOUSE_ENTERED,  e -> btn.setOpacity(0.8));
        btn.addEventHandler(MouseEvent.MOUSE_EXITED,   e -> btn.setOpacity(1.0));
        btn.addEventHandler(MouseEvent.MOUSE_PRESSED,  e -> btn.setOpacity(0.6));
        btn.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> btn.setOpacity(1.0));
        return btn;
    }

    // Player HP bar
    private void showPlayerHpBar() {
        double segW = (PLR_BAR_W - PLR_SEG_GAP * (PLAYER_MAX_HP - 1)) / PLAYER_MAX_HP;
        Color frameColor = Color.web("#00ffe7");

        addCornerBracket(PLR_BAR_X - 3, PLR_BAR_Y - 3,
                8, 8, true,  true,  frameColor);
        addCornerBracket(PLR_BAR_X + PLR_BAR_W - 5, PLR_BAR_Y - 3,
                8, 8, false, true,  frameColor);
        addCornerBracket(PLR_BAR_X - 3, PLR_BAR_Y + PLR_BAR_H - 5,
                8, 8, true,  false, frameColor);
        addCornerBracket(PLR_BAR_X + PLR_BAR_W - 5, PLR_BAR_Y + PLR_BAR_H - 5,
                8, 8, false, false, frameColor);

        plrHpLabel = new Text("HP");
        plrHpLabel.setFont(Font.font("Monospace", FontWeight.BOLD, 10));
        plrHpLabel.setFill(Color.web("#00ffe7cc"));
        plrHpLabel.setTranslateX(PLR_BAR_X);
        plrHpLabel.setTranslateY(PLR_BAR_Y - 5);
        DropShadow labelGlow = new DropShadow();
        labelGlow.setColor(Color.web("#00ffe7"));
        labelGlow.setRadius(6);
        plrHpLabel.setEffect(labelGlow);
        gameRoot.getChildren().add(plrHpLabel);

        for (int i = 0; i < PLAYER_MAX_HP; i++) {
            double sx = PLR_BAR_X + i * (segW + PLR_SEG_GAP);

            Rectangle track = new Rectangle(segW, PLR_BAR_H);
            track.setFill(Color.color(0, 0.04, 0.08, 0.88));
            track.setStroke(Color.web("#00ffe733"));
            track.setStrokeWidth(1);
            track.setTranslateX(sx);
            track.setTranslateY(PLR_BAR_Y);
            gameRoot.getChildren().add(track);

            Rectangle fill = new Rectangle(segW, PLR_BAR_H);
            fill.setFill(makeHpGradient(segW, plrCurrentColor));
            fill.setTranslateX(sx);
            fill.setTranslateY(PLR_BAR_Y);
            DropShadow glow = new DropShadow();
            glow.setColor(Color.web("#00ffe7cc"));
            glow.setRadius(10);
            glow.setSpread(0.3);
            fill.setEffect(glow);
            gameRoot.getChildren().add(fill);
            plrSegFills.add(fill);

            Rectangle glowLine = new Rectangle(segW, 2);
            glowLine.setFill(Color.web("#aaffff88"));
            glowLine.setTranslateX(sx);
            glowLine.setTranslateY(PLR_BAR_Y);
            gameRoot.getChildren().add(glowLine);
            plrSegGlows.add(glowLine);
        }

        plrScanline = new Rectangle(18, PLR_BAR_H);
        plrScanline.setFill(new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.TRANSPARENT),
                new Stop(0.5, Color.web("#ffffff22")),
                new Stop(1.0, Color.TRANSPARENT)));
        plrScanline.setTranslateX(PLR_BAR_X - 18);
        plrScanline.setTranslateY(PLR_BAR_Y);
        gameRoot.getChildren().add(plrScanline);

        plrScanlineAnim = new Timeline(
                new KeyFrame(Duration.ZERO,          e -> plrScanline.setTranslateX(PLR_BAR_X - 18)),
                new KeyFrame(Duration.seconds(1.8),  e -> plrScanline.setTranslateX(PLR_BAR_X + PLR_BAR_W + 18))
        );
        plrScanlineAnim.setCycleCount(Timeline.INDEFINITE);
        plrScanlineAnim.play();

        plrPulse = new Timeline(
                new KeyFrame(Duration.ZERO,         e -> setPlrGlowRadius(10)),
                new KeyFrame(Duration.millis(700),  e -> setPlrGlowRadius(18)),
                new KeyFrame(Duration.millis(1400), e -> setPlrGlowRadius(10))
        );
        plrPulse.setCycleCount(Timeline.INDEFINITE);
        plrPulse.play();
    }

    private void setPlrGlowRadius(double r) {
        for (Rectangle seg : plrSegFills) {
            if (seg.getEffect() instanceof DropShadow d) d.setRadius(r);
        }
    }

    private void updatePlayerHpBar() {
        int hp = Math.max(0, player.getHealth());
        if (hp == lastKnownHp) return;
        lastKnownHp = hp;

        Color newColor;
        if (hp >= 3)    newColor = Color.web("#00ffe7");   // cyan — full
        else if (hp == 2) newColor = Color.web("#ff8c00"); // amber — warning
        else if (hp == 1) newColor = Color.web("#ff2244"); // red   — critical
        else              newColor = Color.web("#ff2244");

        boolean colorChanged = !newColor.equals(plrCurrentColor);
        plrCurrentColor = newColor;

        double segW = (PLR_BAR_W - PLR_SEG_GAP * (PLAYER_MAX_HP - 1)) / PLAYER_MAX_HP;

        for (int i = 0; i < PLAYER_MAX_HP; i++) {
            Rectangle fill  = plrSegFills.get(i);
            Rectangle gline = plrSegGlows.get(i);

            boolean segAlive = (i < hp);

            if (segAlive) {
                fill.setWidth(segW);
                gline.setWidth(segW);
                if (colorChanged) {
                    fill.setFill(makeHpGradient(segW, newColor));
                    DropShadow glow = new DropShadow();
                    glow.setColor(newColor.deriveColor(0, 1, 1, 0.8));
                    glow.setRadius(10);
                    glow.setSpread(0.3);
                    fill.setEffect(glow);
                    gline.setFill(Color.web("#aaffff88"));
                }
            } else {
                fill.setWidth(0);
                gline.setWidth(0);
            }
        }

        if (colorChanged) {
            plrHpLabel.setFill(newColor.deriveColor(0, 1, 1, 0.8));
            DropShadow lg = new DropShadow();
            lg.setColor(newColor);
            lg.setRadius(6);
            plrHpLabel.setEffect(lg);
        }
    }

    // Boss phase helpers
    private void triggerBossPhase() {
        bossPhaseTriggered = true;
        spawner.setFrozen(true);
        asteroidSpawner.setFrozen(true);

        if (activeFadeOut != null) { activeFadeOut.stop(); activeFadeOut = null; }

        double startVol = mediaPlayer.getVolume();
        int steps = 40;
        Timeline fadeOut = new Timeline();
        for (int i = 1; i <= steps; i++) {
            final double t = (double) i / steps;
            fadeOut.getKeyFrames().add(new KeyFrame(Duration.millis(i * 40),
                    e -> mediaPlayer.setVolume(startVol * (1.0 - t))));
        }
        fadeOut.setOnFinished(e -> { activeFadeOut = null; mediaPlayer.stop(); startBossMusic(); });
        activeFadeOut = fadeOut;
        fadeOut.play();
    }

    private void startBossMusic() {
        bossMusicPlaying = true;
        try {
            Media bossMusic = new Media(
                    GameLoop.class.getResource("/bossbgm.mp3").toExternalForm());
            bossMusicPlayer = new MediaPlayer(bossMusic);
            bossMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            bossMusicPlayer.setVolume(0.6);
            bossMusicPlayer.play();
        } catch (Exception ex) { ex.printStackTrace(); }

        showBossHpBar();
        new Timeline(new KeyFrame(Duration.seconds(1), e -> spawnBoss())).play();
    }

    // Boss HP bar (unchanged)
    private void showBossHpBar() {
        bossHpBarShown = true;
        double bx = HP_BAR_X, by = HP_BAR_Y, bw = HP_BAR_W, bh = HP_BAR_H, cs = 8;
        Color frameColor = Color.web("#00ffe7");

        addCornerBracket(bx - 3,          by - 3,          cs, cs, true,  true,  frameColor);
        addCornerBracket(bx + bw - cs + 3, by - 3,          cs, cs, false, true,  frameColor);
        addCornerBracket(bx - 3,          by + bh - cs + 3, cs, cs, true,  false, frameColor);
        addCornerBracket(bx + bw - cs + 3, by + bh - cs + 3, cs, cs, false, false, frameColor);

        bossHpTrack = new Rectangle(bw, bh);
        bossHpTrack.setFill(Color.color(0, 0.04, 0.08, 0.88));
        bossHpTrack.setStroke(Color.web("#00ffe733"));
        bossHpTrack.setStrokeWidth(1);
        bossHpTrack.setTranslateX(bx);
        bossHpTrack.setTranslateY(by);
        gameRoot.getChildren().add(bossHpTrack);

        for (int i = 1; i < 10; i++) {
            double tx = bx + (bw * i / 10.0);
            Line tick = new Line(tx, by + 1, tx, by + bh - 1);
            tick.setStroke(Color.web("#00ffe722"));
            tick.setStrokeWidth(1);
            gameRoot.getChildren().add(tick);
        }

        bossHpFill = new Rectangle(bw, bh);
        bossHpFill.setFill(makeHpGradient(bw, currentFillColor));
        bossHpFill.setTranslateX(bx);
        bossHpFill.setTranslateY(by);
        DropShadow glow = new DropShadow();
        glow.setColor(Color.web("#00ffe7cc"));
        glow.setRadius(10);
        glow.setSpread(0.3);
        bossHpFill.setEffect(glow);
        gameRoot.getChildren().add(bossHpFill);

        bossHpGlowRect = new Rectangle(bw, 2);
        bossHpGlowRect.setFill(Color.web("#aaffff88"));
        bossHpGlowRect.setTranslateX(bx);
        bossHpGlowRect.setTranslateY(by);
        gameRoot.getChildren().add(bossHpGlowRect);

        bossHpScanline = new Rectangle(18, bh);
        bossHpScanline.setFill(new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.TRANSPARENT),
                new Stop(0.5, Color.web("#ffffff22")),
                new Stop(1.0, Color.TRANSPARENT)));
        bossHpScanline.setTranslateX(bx - 18);
        bossHpScanline.setTranslateY(by);
        gameRoot.getChildren().add(bossHpScanline);

        scanlineTimeline = new Timeline(
                new KeyFrame(Duration.ZERO,         e -> bossHpScanline.setTranslateX(bx - 18)),
                new KeyFrame(Duration.seconds(2.2), e -> bossHpScanline.setTranslateX(bx + bw + 18))
        );
        scanlineTimeline.setCycleCount(Timeline.INDEFINITE);
        scanlineTimeline.play();

        bossHpLabel = new Text("BOSS");
        bossHpLabel.setFont(Font.font("Monospace", FontWeight.BOLD, 10));
        bossHpLabel.setFill(Color.web("#00ffe7cc"));
        bossHpLabel.setTranslateX(bx);
        bossHpLabel.setTranslateY(by - 5);
        DropShadow labelGlow = new DropShadow();
        labelGlow.setColor(Color.web("#00ffe7"));
        labelGlow.setRadius(6);
        bossHpLabel.setEffect(labelGlow);
        gameRoot.getChildren().add(bossHpLabel);

        bossHpPercent = new Text("100%");
        bossHpPercent.setFont(Font.font("Monospace", FontWeight.BOLD, 10));
        bossHpPercent.setFill(Color.web("#00ffe7aa"));
        bossHpPercent.setTranslateX(bx + bw - 34);
        bossHpPercent.setTranslateY(by - 5);
        gameRoot.getChildren().add(bossHpPercent);

        hpPulseTimeline = new Timeline(
                new KeyFrame(Duration.ZERO,         e -> { DropShadow d = (DropShadow) bossHpFill.getEffect(); if (d != null) d.setRadius(10); }),
                new KeyFrame(Duration.millis(700),  e -> { DropShadow d = (DropShadow) bossHpFill.getEffect(); if (d != null) d.setRadius(18); }),
                new KeyFrame(Duration.millis(1400), e -> { DropShadow d = (DropShadow) bossHpFill.getEffect(); if (d != null) d.setRadius(10); })
        );
        hpPulseTimeline.setCycleCount(Timeline.INDEFINITE);
        hpPulseTimeline.play();
    }

    private void addCornerBracket(double x, double y, double w, double h,
                                   boolean left, boolean top, Color color) {
        double hx1 = left ? x : x + w,  hx2 = left ? x + w : x;
        double vy1 = top  ? y : y + h,  vy2 = top  ? y + h : y;
        Line horiz = new Line(hx1, vy1, hx2, vy1);
        Line vert  = new Line(hx1, vy1, hx1, vy2);
        DropShadow s = new DropShadow();
        s.setColor(color.deriveColor(0, 1, 1, 0.8));
        s.setRadius(4);
        for (Line ln : new Line[]{horiz, vert}) {
            ln.setStroke(color);
            ln.setStrokeWidth(1.5);
            ln.setEffect(s);
            gameRoot.getChildren().add(ln);
        }
    }

    private LinearGradient makeHpGradient(double width, Color base) {
        Color bright = base.deriveColor(0, 1.0, 1.4, 1.0);
        Color mid    = base.deriveColor(0, 1.0, 0.9, 1.0);
        Color dim    = base.deriveColor(0, 0.8, 0.6, 1.0);
        return new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0.0,  dim),
                new Stop(0.7,  mid),
                new Stop(0.92, bright),
                new Stop(1.0,  Color.WHITE));
    }

    private void spawnBoss() {
        boss = new Boss(gameRoot, System.nanoTime());
        bossSpawned = true;
        asteroidSpawner.setFrozen(false);
    }

    private void updateBossHpBar() {
        if (bossHpFill == null || boss == null) return;
        double ratio = Math.max(0, (double) boss.getHp() / Boss.MAX_HP);
        double fillW = HP_BAR_W * ratio;
        bossHpFill.setWidth(fillW);
        bossHpGlowRect.setWidth(fillW);

        Color newColor;
        if      (ratio > 0.5)  newColor = Color.web("#00ffe7");
        else if (ratio > 0.25) newColor = Color.web("#ff8c00");
        else                   newColor = Color.web("#ff2244");

        if (!newColor.equals(currentFillColor)) {
            currentFillColor = newColor;
            bossHpFill.setFill(makeHpGradient(HP_BAR_W, newColor));
            DropShadow glow = new DropShadow();
            glow.setColor(newColor.deriveColor(0, 1, 1, 0.8));
            glow.setRadius(10);
            glow.setSpread(0.3);
            bossHpFill.setEffect(glow);
            bossHpLabel.setFill(newColor.deriveColor(0, 1, 1, 0.8));
            DropShadow lg = new DropShadow();
            lg.setColor(newColor);
            lg.setRadius(6);
            bossHpLabel.setEffect(lg);
            bossHpPercent.setFill(newColor.deriveColor(0, 1, 1, 0.7));
        }

        bossHpScanline.setClip(new Rectangle(HP_BAR_X, HP_BAR_Y, fillW, HP_BAR_H));
        int pct = (int) Math.ceil(ratio * 100);
        bossHpPercent.setText(pct + "%");
        bossHpPercent.setTranslateX(HP_BAR_X + HP_BAR_W - 34);
    }

    // Main loop
    public void start() {
        AnimationTimer[] timerRef = new AnimationTimer[1];

        timerRef[0] = new AnimationTimer() {
            @Override
            public void handle(long now) {

                double dx = 0, dy = 0;
                if (control.upPressed)    dy -= 1;
                if (control.downPressed)  dy += 1;
                if (control.leftPressed)  dx -= 1;
                if (control.rightPressed) dx += 1;

                if (control.dashPressed) { player.dash(dx, dy, now); control.dashPressed = false; }
                player.move(dx, dy, dx != 0 || dy != 0, now);

                if (control.fireHeld && now - lastFireTime >= FIRE_RATE) {
                    bullets.shoot();
                    lastFireTime = now;
                }

                // ← replaces updateHealthDisplay()
                updatePlayerHpBar();

                bullets.getBullets().removeIf(b -> {
                    if (b.getTranslateY() < -20) { bullets.getRoot().getChildren().remove(b); return true; }
                    return false;
                });
                for (ImageView b : bullets.getBullets()) b.setTranslateY(b.getTranslateY() - bullets.getBulletSpeed(now));
                bullets.updateBullets(now);

                if (!bossPhaseTriggered && killCount >= BOSS_KILL_THRESHOLD) triggerBossPhase();

                spawner.update(now, player.getX(), player.getY());
                asteroidSpawner.update(now);

                if (!bossSpawned) {
                    List<ImageView> bulletHits    = new ArrayList<>();
                    List<enemy>     killedEnemies = new ArrayList<>();
                    for (ImageView b : bullets.getBullets()) {
                        for (enemy e : spawner.getEnemies()) {
                            if (b.getBoundsInParent().intersects(e.getSprite().getBoundsInParent())) {
                                bulletHits.add(b);
                                if (e.takeDamage()) killedEnemies.add(e);
                                break;
                            }
                        }
                    }
                    for (ImageView b : bulletHits) {
                        int idx = bullets.getBullets().indexOf(b);
                        if (idx >= 0) { bullets.getSpawnTimes().remove(idx); bullets.getBullets().remove(idx); }
                        gameRoot.getChildren().remove(b);
                    }
                    for (enemy e : killedEnemies) { spawner.removeEnemy(e); killCount++; System.out.println("Kills: " + killCount); }
                }

                {
                    List<ImageView>  bulletHitsAsteroid = new ArrayList<>();
                    List<spaceDebris> asteroidsShot      = new ArrayList<>();
                    for (ImageView b : bullets.getBullets()) {
                        for (spaceDebris a : asteroidSpawner.getAsteroids()) {
                            if (b.getBoundsInParent().intersects(a.getSprite().getBoundsInParent())) {
                                bulletHitsAsteroid.add(b); asteroidsShot.add(a); break;
                            }
                        }
                    }
                    for (ImageView b : bulletHitsAsteroid) {
                        int idx = bullets.getBullets().indexOf(b);
                        if (idx >= 0) { bullets.getSpawnTimes().remove(idx); bullets.getBullets().remove(idx); }
                        gameRoot.getChildren().remove(b);
                    }
                    for (spaceDebris a : asteroidsShot) asteroidSpawner.destroyAsteroid(a);
                }

                List<SpeedBuff> collectedBuffs = new ArrayList<>();
                for (SpeedBuff buff : asteroidSpawner.getBuffs()) {
                    if (buff.getSprite().getBoundsInParent().intersects(player.getSprite().getBoundsInParent())) {
                        collectedBuffs.add(buff); bullets.activateSpeedBuff(now); playSfx("/speedbuff.wav", 0.6);
                    }
                }
                collectedBuffs.forEach(asteroidSpawner::removeBuff);

                List<HealthOrb> collectedOrbs = new ArrayList<>();
                for (HealthOrb orb : asteroidSpawner.getHealthOrbs()) {
                    if (orb.getSprite().getBoundsInParent().intersects(player.getSprite().getBoundsInParent())) {
                        collectedOrbs.add(orb); player.restoreHealth(); playSfx("/healthup.wav", 0.7);
                    }
                }
                collectedOrbs.forEach(asteroidSpawner::removeHealthOrb);

                List<spaceDebris> asteroidsToRemove = new ArrayList<>();
                for (spaceDebris a : asteroidSpawner.getAsteroids()) {
                    if (a.getSprite().getBoundsInParent().intersects(player.getSprite().getBoundsInParent())) {
                        boolean hit = player.takeDamage(now);
                        if (hit) { asteroidsToRemove.add(a); if (player.isDead()) { timerRef[0].stop(); showGameOver(); return; } }
                    }
                }
                asteroidsToRemove.forEach(asteroidSpawner::destroyAsteroid);

                List<enemy> enemiesToRemove = new ArrayList<>();
                for (enemy e : spawner.getEnemies()) {
                    if (e.collidesWith(player.getSprite())) {
                        boolean hit = player.takeDamage(now);
                        if (hit) { enemiesToRemove.add(e); if (player.isDead()) { timerRef[0].stop(); showGameOver(); return; } }
                    }
                }
                enemiesToRemove.forEach(spawner::removeEnemy);

                List<ImageView> enemyBulletsToRemove = new ArrayList<>();
                for (ImageView eb : enemyBullets.getBullets()) {
                    if (eb.getBoundsInParent().intersects(player.getSprite().getBoundsInParent())) {
                        boolean hit = player.takeDamage(now);
                        if (hit) { enemyBulletsToRemove.add(eb); if (player.isDead()) { timerRef[0].stop(); showGameOver(); return; } }
                    }
                }
                for (ImageView eb : enemyBulletsToRemove) { gameRoot.getChildren().remove(eb); enemyBullets.getBullets().remove(eb); }

                if (bossSpawned && boss != null && !boss.isDead()) {

                    boss.update(now);

    
                    if (boss.isEntered() && spawner.isFrozen()) {
                     spawner.setFrozen(false);
                    }

                    if (boss.collidesWith(player.getSprite())) {
                        boolean hit = player.takeDamage(now);
                        if (hit && player.isDead()) { timerRef[0].stop(); showGameOver(); return; }
                    }
                    updateBossHpBar();

                    List<ImageView> bossHits = new ArrayList<>();
                    for (ImageView b : bullets.getBullets()) {
                        if (b.getBoundsInParent().intersects(boss.getSprite().getBoundsInParent())) {
                            bossHits.add(b);
                            boolean killed = boss.takeDamage(now);
                            if (killed) { new AsteroidExplosion(gameRoot, boss.getX(), boss.getY(), 200); boss.remove(gameRoot); timerRef[0].stop(); showVictory(); return; }
                            break;
                        }
                    }
                    for (ImageView b : bossHits) {
                        int idx = bullets.getBullets().indexOf(b);
                        if (idx >= 0) { bullets.getSpawnTimes().remove(idx); bullets.getBullets().remove(idx); }
                        gameRoot.getChildren().remove(b);
                    }

                    if (boss.isEntered() && boss.pollReadyToFire()) {
                        bossBeams.add(new BossBeam(gameRoot, boss.getBeamX(), boss.getBeamY(), now));
                        boss.setFrozen(true);
                    }

                    List<BossBeam> beamsToRemove = new ArrayList<>();
                    for (BossBeam beam : bossBeams) {
                        boolean offScreen = beam.update(now);
                        if (offScreen) { beamsToRemove.add(beam); continue; }
                        if (beam.hitsPlayer(player)) {
                            boolean dead1 = player.takeDamage(now);
                            if (!dead1) player.takeDamage(now);
                            beamsToRemove.add(beam);
                            if (player.isDead()) { timerRef[0].stop(); showGameOver(); return; }
                        }
                    }
                    for (BossBeam beam : beamsToRemove) { beam.remove(gameRoot); bossBeams.remove(beam); boss.setFrozen(false); }
                }
            }
        };

        timerRef[0].start();
    }

    // Game-over screen
    private void showGameOver() {
        stopBossMusic();
        stopHpBarAnimations();
        resetMainBgm();

        Rectangle overlay = new Rectangle(720, 720);
        overlay.setFill(Color.color(0.1, 0, 0, 0.75));
        gameRoot.getChildren().add(overlay);

        Image gameOverImg = new Image(GameLoop.class.getResource("/ui/gameOver.png").toExternalForm());
        ImageView gameOverLabel = new ImageView(gameOverImg);
        gameOverLabel.setFitWidth(460);
        gameOverLabel.setPreserveRatio(true);

        ImageView replayBtn = makeImageButton("/ui/btnPlayAgain.png", 150);
        replayBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> restartGame());
        ImageView menuBtn = makeImageButton("/ui/btnMainMenu.png", 150);
        menuBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> goToMenu());

        VBox box = new VBox(20, gameOverLabel, replayBtn, menuBtn);
        box.setAlignment(Pos.CENTER);
        box.setPrefWidth(720);
        box.setPrefHeight(720);
        gameRoot.getChildren().add(box);
    }

    // Victory screen
    private void showVictory() {
        stopBossMusic();
        stopHpBarAnimations();

        try {
            Media fanfare = new Media(GameLoop.class.getResource("/victory.wav").toExternalForm());
            MediaPlayer vp = new MediaPlayer(fanfare);
            vp.setVolume(0.8);
            vp.play();
            vp.setOnEndOfMedia(vp::dispose);
        } catch (Exception ignored) {}

        Rectangle overlay = new Rectangle(720, 720);
        overlay.setFill(Color.color(0, 0.05, 0.1, 0.80));
        gameRoot.getChildren().add(overlay);

        Image victoryImg = new Image(GameLoop.class.getResource("/ui/victory.png").toExternalForm());
        ImageView victoryLabel = new ImageView(victoryImg);
        victoryLabel.setFitWidth(480);
        victoryLabel.setPreserveRatio(true);

        ImageView replayBtn = makeImageButton("/ui/btnPlayAgain.png", 150);
        replayBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> restartGame());
        ImageView menuBtn = makeImageButton("/ui/btnMainMenu.png", 150);
        menuBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> goToMenu());

        VBox box = new VBox(20, victoryLabel, replayBtn, menuBtn);
        box.setAlignment(Pos.CENTER);
        box.setPrefWidth(720);
        box.setPrefHeight(720);
        gameRoot.getChildren().add(box);
    }

    // Music / SFX 
    private void stopBossMusic() {
        if (bossMusicPlayer != null) { bossMusicPlayer.stop(); bossMusicPlayer.dispose(); bossMusicPlayer = null; }
    }

    private void stopHpBarAnimations() {
        if (hpPulseTimeline  != null) { hpPulseTimeline.stop();  hpPulseTimeline  = null; }
        if (scanlineTimeline != null) { scanlineTimeline.stop(); scanlineTimeline = null; }
        if (plrPulse         != null) { plrPulse.stop();         plrPulse         = null; }
        if (plrScanlineAnim  != null) { plrScanlineAnim.stop();  plrScanlineAnim  = null; }
    }

    private void resetMainBgm() {
        if (activeFadeOut != null) { activeFadeOut.stop(); activeFadeOut = null; }
        if (mediaPlayer == null) return;
        mediaPlayer.stop();
        mediaPlayer.setRate(1.0);
        mediaPlayer.setVolume(0.5);
    }

    private void restartGame() {
        stopBossMusic(); resetMainBgm(); mediaPlayer.play();
        stage.setScene(menuUI.startGame(stage, null));
    }

    private void goToMenu() {
        stopBossMusic(); resetMainBgm(); mediaPlayer.play();
        stage.setScene(menuUI.createMenuScene(stage));
    }

    private void playSfx(String path, double volume) {
        try {
            Media sound = new Media(GameLoop.class.getResource(path).toExternalForm());
            MediaPlayer sfx = new MediaPlayer(sound);
            sfx.setVolume(volume);
            sfx.play();
            sfx.setOnEndOfMedia(sfx::dispose);
        } catch (Exception ignored) {}
    }
}