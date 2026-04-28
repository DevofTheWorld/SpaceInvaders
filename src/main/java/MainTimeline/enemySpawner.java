package MainTimeline;

import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

public class enemySpawner {

    private Pane root;
    private List<enemy> enemies = new ArrayList<>();
    private enemyBullets enemyBullets;

    private long lastSpawnTime = 0;
    private long spawnInterval = 1_500_000_000L;

    private long lastShootTime = 0;
    private long shootInterval = 300_000_000L;

    public enemySpawner(Pane root, enemyBullets enemyBullets) {
        this.root = root;
        this.enemyBullets = enemyBullets;
    }

    public void update(long now, double playerX) {

        if (now - lastSpawnTime > spawnInterval) {
            enemies.add(new enemy(root));
            lastSpawnTime = now;
        }

        enemies.removeIf(e -> {
            if (e.getY() > 740) {
                root.getChildren().remove(e.getSprite());
                return true;
            }
            e.update(playerX);
            return false;
        });

        if (!enemies.isEmpty() && now - lastShootTime > shootInterval) {
            enemy shooter = enemies.get((int)(Math.random() * enemies.size()));
            enemyBullets.shoot(shooter.getX(), shooter.getY());
            lastShootTime = now;
        }

        enemyBullets.update();
    }

    public List<enemy> getEnemies() { return enemies; }

    public void removeEnemy(enemy e) {
        root.getChildren().remove(e.getSprite());
        enemies.remove(e);
    }
}