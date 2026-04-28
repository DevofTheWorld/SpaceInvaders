package MainTimeline;

import javafx.animation.AnimationTimer;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.geometry.Pos;

import java.util.ArrayList;
import java.util.List;

public class GameLoop {

    private Control control;
    private Player player;
    private playerBullets bullets;
    private enemySpawner spawner;
    private enemyBullets enemyBullets;
    private asteroidSpawner asteroidSpawner;
    private Pane gameRoot;
    private Label healthLabel;
    private Stage stage;

    private long lastFireTime = 0;
    private static final long FIRE_RATE = 300_000_000L; // 0.3 seconds

    public GameLoop(Player player, Control control, playerBullets bullets,
                    enemySpawner spawner, enemyBullets enemyBullets,
                    asteroidSpawner asteroidSpawner, Pane gameRoot, Stage stage) {
        this.player = player;
        this.control = control;
        this.bullets = bullets;
        this.spawner = spawner;
        this.enemyBullets = enemyBullets;
        this.asteroidSpawner = asteroidSpawner;
        this.gameRoot = gameRoot;
        this.stage = stage;

        healthLabel = new Label("❤ ❤ ❤");
        healthLabel.setTextFill(Color.WHITE);
        healthLabel.setStyle("-fx-font-size: 22px;");
        healthLabel.setTranslateX(10);
        healthLabel.setTranslateY(10);
        gameRoot.getChildren().add(healthLabel);
    }

    private void updateHealthDisplay() {
        int hp = player.getHealth();
        StringBuilder hearts = new StringBuilder();
        for (int i = 0; i < player.maxHealth; i++) {
            hearts.append(i < hp ? "❤ " : "♡ ");
        }
        healthLabel.setText(hearts.toString().trim());
    }

    public void start() {

        AnimationTimer[] timerRef = new AnimationTimer[1];

        timerRef[0] = new AnimationTimer() {
            @Override
            public void handle(long now) {

                // player movement
                double dx = 0, dy = 0;
                if (control.upPressed) dy -= 1;
                if (control.downPressed) dy += 1;
                if (control.leftPressed) dx -= 1;
                if (control.rightPressed) dx += 1;

                if (control.dashPressed) {
                    player.dash(dx, dy, now);
                    control.dashPressed = false;
                }

                boolean moving = dx != 0 || dy != 0;
                player.move(dx, dy, moving, now);

                // auto fire while space held
                if (control.fireHeld && now - lastFireTime >= FIRE_RATE) {
                    bullets.shoot();
                    lastFireTime = now;
                }

                updateHealthDisplay();

                // player bullets movement
                bullets.getBullets().removeIf(b -> {
                    if (b.getTranslateY() < -20) {
                        bullets.getRoot().getChildren().remove(b);
                        return true;
                    }
                    return false;
                });

                for (ImageView b : bullets.getBullets()) {
                    b.setTranslateY(b.getTranslateY() - 5);
                }

                bullets.updateBullets(now);

                // enemies + enemy bullets
                spawner.update(now, player.getX());

                // asteroids
                asteroidSpawner.update(now);

                // player bullets hitting enemies
                List<ImageView> bulletHits = new ArrayList<>();
                List<enemy> killedEnemies = new ArrayList<>();

                for (ImageView b : bullets.getBullets()) {
                    for (enemy e : spawner.getEnemies()) {
                        if (b.getBoundsInParent().intersects(e.getSprite().getBoundsInParent())) {
                            bulletHits.add(b);
                            boolean died = e.takeDamage();
                            if (died) killedEnemies.add(e);
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
                }

                // asteroid collision with player
                List<spaceDebris> asteroidsToRemove = new ArrayList<>();
                for (spaceDebris a : asteroidSpawner.getAsteroids()) {
                    if (a.getSprite().getBoundsInParent()
                            .intersects(player.getSprite().getBoundsInParent())) {
                        boolean hit = player.takeDamage(now);
                        if (hit) {
                            asteroidsToRemove.add(a);
                            if (player.isDead()) {
                                timerRef[0].stop();
                                showGameOver();
                            }
                        }
                    }
                }
                for (spaceDebris a : asteroidsToRemove) {
                    gameRoot.getChildren().remove(a.getSprite());
                    asteroidSpawner.getAsteroids().remove(a);
                }

                // enemy body collision with player
                List<enemy> enemiesToRemove = new ArrayList<>();
                for (enemy e : spawner.getEnemies()) {
                    if (e.collidesWith(player.getSprite())) {
                        boolean hit = player.takeDamage(now);
                        if (hit) {
                            enemiesToRemove.add(e);
                            if (player.isDead()) {
                                timerRef[0].stop();
                                showGameOver();
                            }
                        }
                    }
                }
                for (enemy e : enemiesToRemove) {
                    spawner.removeEnemy(e);
                }

                // --- enemy bullet collision with player ---
                List<ImageView> bulletsToRemove = new ArrayList<>();
                for (ImageView eb : enemyBullets.getBullets()) {
                    if (eb.getBoundsInParent()
                            .intersects(player.getSprite().getBoundsInParent())) {
                        boolean hit = player.takeDamage(now);
                        if (hit) {
                            bulletsToRemove.add(eb);
                            if (player.isDead()) {
                                timerRef[0].stop();
                                showGameOver();
                            }
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
        javafx.scene.shape.Rectangle overlay = new javafx.scene.shape.Rectangle(720, 720);
        overlay.setFill(Color.color(0, 0, 0, 0.6));
        gameRoot.getChildren().add(overlay);

        Label gameOverLabel = new Label("GAME OVER");
        gameOverLabel.setTextFill(Color.RED);
        gameOverLabel.setStyle("-fx-font-size: 48px; -fx-font-weight: bold;");

        Button replayBtn = new Button("Play Again");
        replayBtn.setStyle(
                "-fx-font-size: 18px;" +
                        "-fx-background-color: white;" +
                        "-fx-text-fill: black;" +
                        "-fx-padding: 10 30 10 30;" +
                        "-fx-cursor: hand;"
        );
        replayBtn.setOnAction(e -> {
            javafx.scene.Scene newGame = menuUI.startGame(stage, null);
            stage.setScene(newGame);
        });

        Button menuBtn = new Button("Main Menu");
        menuBtn.setStyle(
                "-fx-font-size: 18px;" +
                        "-fx-background-color: transparent;" +
                        "-fx-text-fill: white;" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 1px;" +
                        "-fx-padding: 10 30 10 30;" +
                        "-fx-cursor: hand;"
        );
        menuBtn.setOnAction(e -> {
            javafx.scene.Scene menuScene = menuUI.createMenuScene(stage);
            stage.setScene(menuScene);
        });

        VBox gameOverBox = new VBox(20, gameOverLabel, replayBtn, menuBtn);
        gameOverBox.setAlignment(Pos.CENTER);
        gameOverBox.setPrefWidth(720);
        gameOverBox.setPrefHeight(720);

        gameRoot.getChildren().add(gameOverBox);
        healthLabel.setText("♡ ♡ ♡");
    }
}