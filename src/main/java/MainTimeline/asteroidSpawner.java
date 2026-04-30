package MainTimeline;

import javafx.scene.layout.Pane;
import java.util.ArrayList;
import java.util.List;

public class asteroidSpawner {

    private Pane root;
    private List<spaceDebris> asteroids = new ArrayList<>();
    private List<SpeedBuff> buffs = new ArrayList<>();
    private List<AsteroidExplosion> explosions = new ArrayList<>();

    private long lastSpawnTime = 0;
    private long spawnInterval = 1_000_000_000L; // spawn every 1s (was 2s)

    private static final double MIN_DISTANCE = 60;

    public asteroidSpawner(Pane root) {
        this.root = root;
    }

    private boolean isTooClose(double newX) {
        for (spaceDebris a : asteroids) {
            if (Math.abs(a.getX() - newX) < MIN_DISTANCE) return true;
        }
        return false;
    }

    public void update(long now) {
        // spawn new asteroid
        if (now - lastSpawnTime > spawnInterval) {
            for (int i = 0; i < 10; i++) {
                double newX = 20 + Math.random() * 680;
                if (!isTooClose(newX)) {
                    asteroids.add(new spaceDebris(root));
                    break;
                }
            }
            lastSpawnTime = now;
        }

        // move asteroids, remove if off screen
        asteroids.removeIf(a -> {
            if (a.getY() > 760) {
                root.getChildren().remove(a.getSprite());
                return true;
            }
            a.update();
            return false;
        });

        // move buffs, remove if off screen
        buffs.removeIf(b -> {
            if (b.getY() > 760) {
                b.remove(root);
                return true;
            }
            b.update();
            return false;
        });

        // tick explosions, remove when finished
        explosions.removeIf(ex -> {
            boolean done = ex.update(now);
            if (done) ex.remove(root);
            return done;
        });
    }

    public void destroyAsteroid(spaceDebris asteroid) {
        // spawn explosion at asteroid center
        explosions.add(new AsteroidExplosion(root, asteroid.getX(), asteroid.getY(), asteroid.getSize()));

        // 50% chance to drop speed buff
        if (Math.random() < 0.5) {
            buffs.add(new SpeedBuff(root, asteroid.getX(), asteroid.getY()));
        }

        asteroid.destroy(root);
        asteroids.remove(asteroid);
    }

    public List<spaceDebris> getAsteroids() { return asteroids; }
    public List<SpeedBuff> getBuffs() { return buffs; }

    public void removeBuff(SpeedBuff b) {
        b.remove(root);
        buffs.remove(b);
    }
}