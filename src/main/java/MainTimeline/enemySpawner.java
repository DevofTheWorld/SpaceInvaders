package MainTimeline;

import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

public class enemySpawner {

    private Pane root;
    private List<enemy> enemies = new ArrayList<>();
    private enemyBullets enemyBullets;

    private long lastSpawnTime = 0;
    private long spawnInterval = 3_000_000_000L; // spawn new enemy every 3 seconds

    private long lastShootTime = 0;
    private long shootInterval = 1_500_000_000L; // enemies shoot every 1.5 seconds

    public enemySpawner(Pane root, enemyBullets enemyBullets) {
        this.root = root;
        this.enemyBullets = enemyBullets;
    }

    public void update(long now, double playerX) {

        // --- spawn a new enemy every 3 seconds ---
        if (now - lastSpawnTime > spawnInterval) {
            enemies.add(new enemy(root));
            lastSpawnTime = now;
        }

        // --- update each enemy: follow player X ---
        enemies.removeIf(e -> {
            if (e.getY() > 740) { // remove if off screen
                root.getChildren().remove(e.getSprite());
                return true;
            }
            e.update(playerX);
            return false;
        });

        // --- random enemy shoots every 1.5 seconds ---
        if (!enemies.isEmpty() && now - lastShootTime > shootInterval) {
            // pick a random enemy to shoot
            enemy shooter = enemies.get((int)(Math.random() * enemies.size()));
            enemyBullets.shoot(shooter.getX(), shooter.getY());
            lastShootTime = now;
        }

        // --- move enemy bullets ---
        enemyBullets.update();
    }

    public List<enemy> getEnemies() {
        return enemies;
    }
}