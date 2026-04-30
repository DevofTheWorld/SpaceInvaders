package MainTimeline;

import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

public class enemySpawner {

    private Pane root;
    private List<enemy> enemies = new ArrayList<>();
    private List<AsteroidExplosion> explosions = new ArrayList<>();
    private enemyBullets enemyBullets;

    private long lastSpawnTime = 0;
    private long spawnInterval = 3_500_000_000L;

    private long lastShootTime = 0;
    private long shootInterval = 300_000_000L;

    private static final double MIN_SPAWN_DISTANCE = 80;

    public enemySpawner(Pane root, enemyBullets enemyBullets) {
        this.root = root;
        this.enemyBullets = enemyBullets;
    }

    private boolean isTooClose(double newX) {
        for (enemy e : enemies) {
            if (Math.abs(e.getX() - newX) < MIN_SPAWN_DISTANCE) return true;
        }
        return false;
    }

    public void update(long now, double playerX, double playerY) {

        // spawn, avoid stacking
        if (now - lastSpawnTime > spawnInterval) {
            for (int i = 0; i < 10; i++) {
                double newX = 20 + Math.random() * 660;
                if (!isTooClose(newX)) {
                    enemies.add(new enemy(root, now));
                    break;
                }
            }
            lastSpawnTime = now;
        }

        enemies.removeIf(e -> {
            if (e.getY() > 760) {
                root.getChildren().remove(e.getSprite());
                return true;
            }
            e.update(now, playerX, playerY);
            return false;
        });

        // tick explosions
        explosions.removeIf(ex -> {
            boolean done = ex.update(now);
            if (done) ex.remove(root);
            return done;
        });

        if (!enemies.isEmpty() && now - lastShootTime > shootInterval) {
            enemy shooter = enemies.get((int)(Math.random() * enemies.size()));
            enemyBullets.shoot(shooter.getX(), shooter.getY(), playerX, playerY);
            lastShootTime = now;
        }

        enemyBullets.update();
    }

    /** Removes enemy and plays explosion. Called from GameLoop on kill or kamikaze hit. */
    public void removeEnemy(enemy e) {
        explosions.add(new AsteroidExplosion(root, e.getX(), e.getY(), 50));
        root.getChildren().remove(e.getSprite());
        enemies.remove(e);
    }

    public List<enemy> getEnemies() { return enemies; }
}