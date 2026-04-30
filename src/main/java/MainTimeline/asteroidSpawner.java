package MainTimeline;

import javafx.scene.layout.Pane;
import java.util.ArrayList;
import java.util.List;

public class asteroidSpawner {

    private Pane root;
    private List<spaceDebris> asteroids = new ArrayList<>();
    private List<SpeedBuff> buffs = new ArrayList<>();
    private List<HealthOrb> healthOrbs = new ArrayList<>();
    private List<AsteroidExplosion> explosions = new ArrayList<>();

    private long lastSpawnTime = 0;
    private long spawnInterval = 1_000_000_000L;

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

        asteroids.removeIf(a -> {
            if (a.getY() > 760) {
                root.getChildren().remove(a.getSprite());
                return true;
            }
            a.update();
            return false;
        });

        buffs.removeIf(b -> {
            if (b.getY() > 760) {
                b.remove(root);
                return true;
            }
            b.update();
            return false;
        });

        healthOrbs.removeIf(h -> {
            if (h.getY() > 760) {
                h.remove(root);
                return true;
            }
            h.update();
            return false;
        });

        explosions.removeIf(ex -> {
            boolean done = ex.update(now);
            if (done) ex.remove(root);
            return done;
        });
    }

    public void destroyAsteroid(spaceDebris asteroid) {
        explosions.add(new AsteroidExplosion(root, asteroid.getX(), asteroid.getY(), asteroid.getSize()));

        double roll = Math.random();
        if (roll < 0.20) {
            // 20% chance — speed buff (was 50%, now much rarer)
            buffs.add(new SpeedBuff(root, asteroid.getX(), asteroid.getY()));
        } else if (roll < 0.40) {
            // 20% chance — health orb
            healthOrbs.add(new HealthOrb(root, asteroid.getX(), asteroid.getY()));
        }
        // 60% chance — nothing drops

        asteroid.destroy(root);
        asteroids.remove(asteroid);
    }

    public List<spaceDebris> getAsteroids() { return asteroids; }
    public List<SpeedBuff> getBuffs() { return buffs; }
    public List<HealthOrb> getHealthOrbs() { return healthOrbs; }

    public void removeBuff(SpeedBuff b) {
        b.remove(root);
        buffs.remove(b);
    }

    public void removeHealthOrb(HealthOrb h) {
        h.remove(root);
        healthOrbs.remove(h);
    }
}