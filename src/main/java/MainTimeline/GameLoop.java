package MainTimeline;

import javafx.animation.AnimationTimer;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.geometry.Pos;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class GameLoop {

    private Control control;
    private Player player;
    private playerBullets bullets;
    private enemySpawner spawner;
    private enemyBullets enemyBullets;
    private asteroidSpawner asteroidSpawner;
    private Pane gameRoot;
    private List<ImageView> heartIcons = new ArrayList<>();
    private Image heartFull;
    private Image heartTwoThirds;
    private Image heartOneThird;
    private Image heartEmpty;
    private Stage stage;
    private javafx.scene.media.MediaPlayer mediaPlayer;

    public int killCount = 0;
    public boolean bossSpawned = false;
    
    private long lastFireTime = 0;
    private static final long FIRE_RATE = 200_000_000L;

    public GameLoop(Player player, Control control, playerBullets bullets,
                    enemySpawner spawner, enemyBullets enemyBullets,
                    asteroidSpawner asteroidSpawner, Pane gameRoot, Stage stage,
                    javafx.scene.media.MediaPlayer mediaPlayer) {
        this.player = player;
        this.control = control;
        this.bullets = bullets;
        this.spawner = spawner;
        this.enemyBullets = enemyBullets;
        this.asteroidSpawner = asteroidSpawner;
        this.gameRoot = gameRoot;
        this.stage = stage;
        this.mediaPlayer = mediaPlayer;

        heartFull      = new Image(GameLoop.class.getResource("/ui/heartFull.png").toExternalForm());
        heartTwoThirds = new Image(GameLoop.class.getResource("/ui/heartTwoThirds.png").toExternalForm());
        heartOneThird  = new Image(GameLoop.class.getResource("/ui/heartOneThird.png").toExternalForm());
        heartEmpty     = new Image(GameLoop.class.getResource("/ui/heartEmpty.png").toExternalForm());

        ImageView heart = new ImageView(heartFull);
        heart.setFitWidth(231);
        heart.setFitHeight(43);
        heart.setTranslateX(25);
        heart.setTranslateY(650);
        heartIcons.add(heart);
        gameRoot.getChildren().add(heart);
    }

    private void updateHealthDisplay() {
        int hp = player.getHealth();
        Image img;
        if (hp >= 3)      img = heartFull;
        else if (hp == 2) img = heartTwoThirds;
        else if (hp == 1) img = heartOneThird;
        else              img = heartEmpty;
        heartIcons.get(0).setImage(img);
    }

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

    public void start() {

        AnimationTimer[] timerRef = new AnimationTimer[1];

        timerRef[0] = new AnimationTimer() {
            @Override
            public void handle(long now) {

                // movement of player
                double dx = 0, dy = 0;
                if (control.upPressed)    dy -= 1;
                if (control.downPressed)  dy += 1;
                if (control.leftPressed)  dx -= 1;
                if (control.rightPressed) dx += 1;

                if (control.dashPressed) {
                    player.dash(dx, dy, now);
                    control.dashPressed = false;
                }

                boolean moving = dx != 0 || dy != 0;
                player.move(dx, dy, moving, now);

                // --- player shooting ---
                if (control.fireHeld && now - lastFireTime >= FIRE_RATE) {
                    bullets.shoot();
                    lastFireTime = now;
                }

                updateHealthDisplay();

                // bullet upward, when off the screen remove
                bullets.getBullets().removeIf(b -> {
                    if (b.getTranslateY() < -20) {
                        bullets.getRoot().getChildren().remove(b);
                        return true;
                    }
                    return false;
                });

                for (ImageView b : bullets.getBullets()) {
                    b.setTranslateY(b.getTranslateY() - bullets.getBulletSpeed(now));
                }

                bullets.updateBullets(now);
                spawner.update(now, player.getX(), player.getY());
                asteroidSpawner.update(now);

                // hit enemies by player bullets
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
                    int index = bullets.getBullets().indexOf(b);
                    if (index >= 0) {
                        bullets.getSpawnTimes().remove(index);
                        bullets.getBullets().remove(index);
                    }
                    gameRoot.getChildren().remove(b);
                }
                for (enemy e : killedEnemies) { 
                    spawner.removeEnemy(e); 
                    killCount++; 
                    System.out.println("Kills: " + killCount);
                }
                
                //Boss Trigger
                if (killCount == 2 && !bossSpawned){
                System.out.println("Spawn Boss ! ! ! !@e13123125 ");
                bossSpawned = true;
                } 

                // hit asteroid by player bullet
                List<ImageView> bulletHitsAsteroid = new ArrayList<>();
                List<spaceDebris> asteroidsShot    = new ArrayList<>();

                for (ImageView b : bullets.getBullets()) {
                    for (spaceDebris a : asteroidSpawner.getAsteroids()) {
                        if (b.getBoundsInParent().intersects(a.getSprite().getBoundsInParent())) {
                            bulletHitsAsteroid.add(b);
                            asteroidsShot.add(a);
                            break;
                        }
                    }
                }

                for (ImageView b : bulletHitsAsteroid) {
                    int index = bullets.getBullets().indexOf(b);
                    if (index >= 0) {
                        bullets.getSpawnTimes().remove(index);
                        bullets.getBullets().remove(index);
                    }
                    gameRoot.getChildren().remove(b);
                }
                for (spaceDebris a : asteroidsShot) {
                    asteroidSpawner.destroyAsteroid(a);
                }

                // --- player collecting speed buffs ---
                List<SpeedBuff> collectedBuffs = new ArrayList<>();
                for (SpeedBuff buff : asteroidSpawner.getBuffs()) {
                    if (buff.getSprite().getBoundsInParent()
                            .intersects(player.getSprite().getBoundsInParent())) {
                        collectedBuffs.add(buff);
                        bullets.activateSpeedBuff(now);
                        
                        //Play Speed UP sfx
                        try {
                            Media sound = new Media(playerBullets.class.getResource("/speedbuff.wav").toExternalForm());
                            MediaPlayer sfx = new MediaPlayer(sound);
                            sfx.setVolume(0.6);
                            sfx.play();
                            sfx.setOnEndOfMedia(sfx::dispose);
                        } catch (Exception ignored) {
                        }
                        
                    }
                }
                for (SpeedBuff buff : collectedBuffs) {
                    asteroidSpawner.removeBuff(buff);
                }

                // --- player collecting health orbs ---
                List<HealthOrb> collectedOrbs = new ArrayList<>();
                for (HealthOrb orb : asteroidSpawner.getHealthOrbs()) {
                    if (orb.getSprite().getBoundsInParent()
                            .intersects(player.getSprite().getBoundsInParent())) {
                        collectedOrbs.add(orb);
                        player.restoreHealth();
                       
                        //Play Health UP sfx
                        try {
                            Media sound = new Media(playerBullets.class.getResource("/healthup.wav").toExternalForm());
                            MediaPlayer sfx = new MediaPlayer(sound);
                            sfx.setVolume(0.7);
                            sfx.play();
                            sfx.setOnEndOfMedia(sfx::dispose);
                        } catch (Exception ignored) {
                        }
                        
                    }
                }
                for (HealthOrb orb : collectedOrbs) {
                    asteroidSpawner.removeHealthOrb(orb);
                }

                // --- asteroid body hitting player ---
                List<spaceDebris> asteroidsToRemove = new ArrayList<>();
                for (spaceDebris a : asteroidSpawner.getAsteroids()) {
                    if (a.getSprite().getBoundsInParent()
                            .intersects(player.getSprite().getBoundsInParent())) {
                        boolean hit = player.takeDamage(now);
                        if (hit) {
                            asteroidsToRemove.add(a);
                            if (player.isDead()) { timerRef[0].stop(); showGameOver(); }
                        }
                    }
                }
                for (spaceDebris a : asteroidsToRemove) {
                    asteroidSpawner.destroyAsteroid(a);
                }

                // --- enemy body / kamikaze hitting player ---
                List<enemy> enemiesToRemove = new ArrayList<>();
                for (enemy e : spawner.getEnemies()) {
                    if (e.collidesWith(player.getSprite())) {
                        boolean hit = player.takeDamage(now);
                        if (hit) {
                            enemiesToRemove.add(e);
                            if (player.isDead()) { timerRef[0].stop(); showGameOver(); }
                        }
                    }
                }
                for (enemy e : enemiesToRemove) spawner.removeEnemy(e);

                // --- enemy bullets hitting player ---
                List<ImageView> bulletsToRemove = new ArrayList<>();
                for (ImageView eb : enemyBullets.getBullets()) {
                    if (eb.getBoundsInParent()
                            .intersects(player.getSprite().getBoundsInParent())) {
                        boolean hit = player.takeDamage(now);
                        if (hit) {
                            bulletsToRemove.add(eb);
                            if (player.isDead()) { timerRef[0].stop(); showGameOver(); }
                        }
                    }
                }
                for (ImageView eb : bulletsToRemove) {
                    gameRoot.getChildren().remove(eb);
                    enemyBullets.getBullets().remove(eb);
                }
            }
        };

        timerRef[0].start();
        
        
        
        
  
        
        
        
    }
    
    
    private void showGameOver() {
        heartIcons.get(0).setImage(heartEmpty);

        // music fade out
        double startVolume = mediaPlayer.getVolume();
        int steps = 40;
        Timeline fadeOut = new Timeline();
        for (int i = 1; i <= steps; i++) {
            final double t = (double) i / steps;
            KeyFrame kf = new KeyFrame(Duration.millis(i * 50), e -> {
                mediaPlayer.setVolume(startVolume * (1.0 - t));
                mediaPlayer.setRate(1.0 - (0.85 * t));
            });
            fadeOut.getKeyFrames().add(kf);
        }
        fadeOut.setOnFinished(e -> {
            mediaPlayer.stop();
            mediaPlayer.setRate(0.85);
            mediaPlayer.setVolume(0.15);
            mediaPlayer.play();
        });
        fadeOut.play();

        // dark red overlay
        javafx.scene.shape.Rectangle overlay = new javafx.scene.shape.Rectangle(720, 720);
        overlay.setFill(Color.color(0.1, 0, 0, 0.75));
        gameRoot.getChildren().add(overlay);

        // game over image
        Image gameOverImg = new Image(GameLoop.class.getResource("/ui/gameOver.png").toExternalForm());
        ImageView gameOverLabel = new ImageView(gameOverImg);
        gameOverLabel.setFitWidth(460);
        gameOverLabel.setPreserveRatio(true);

        // play again button
        ImageView replayBtn = makeImageButton("/ui/btnPlayAgain.png", 150);
        replayBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            mediaPlayer.stop();
            mediaPlayer.setRate(1.0);
            mediaPlayer.setVolume(0.5);
            mediaPlayer.play();
            javafx.scene.Scene newGame = menuUI.startGame(stage, null);
            stage.setScene(newGame);
        });

        // main menu button
        ImageView menuBtn = makeImageButton("/ui/btnMainMenu.png", 150);
        menuBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            mediaPlayer.stop();
            mediaPlayer.setRate(1.0);
            mediaPlayer.setVolume(0.5);
            mediaPlayer.play();
            javafx.scene.Scene menuScene = menuUI.createMenuScene(stage);
            stage.setScene(menuScene);
        });

        VBox gameOverBox = new VBox(20, gameOverLabel, replayBtn, menuBtn);
        gameOverBox.setAlignment(Pos.CENTER);
        gameOverBox.setPrefWidth(720);
        gameOverBox.setPrefHeight(720);
        gameRoot.getChildren().add(gameOverBox);
    }
}