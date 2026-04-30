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

    /** When true: no new asteroids spawn. Existing ones keep falling. */
    private boolean frozen = false;

    public asteroidSpawner(Pane root) {
        this.root = root;
    }

    public void setFrozen(boolean frozen) { this.frozen = frozen; }

    private boolean isTooClose(double newX) {
        for (spaceDebris a : asteroids) {
            if (Math.abs(a.getX() - newX) < MIN_DISTANCE) return true;
        }
        return false;
    }

    public void update(long now) {

        // spawn only when not frozen
        if (!frozen && now - lastSpawnTime > spawnInterval) {
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
            if (b.getY() > 760) { b.remove(root); return true; }
            b.update();
            return false;
        });

        healthOrbs.removeIf(h -> {
            if (h.getY() > 760) { h.remove(root); return true; }
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
            buffs.add(new SpeedBuff(root, asteroid.getX(), asteroid.getY()));
        } else if (roll < 0.40) {
            healthOrbs.add(new HealthOrb(root, asteroid.getX(), asteroid.getY()));
        }

        asteroid.destroy(root);
        asteroids.remove(asteroid);
    }

    public List<spaceDebris> getAsteroids() { return asteroids; }
    public List<SpeedBuff>   getBuffs()     { return buffs; }
    public List<HealthOrb>   getHealthOrbs(){ return healthOrbs; }

    public void removeBuff(SpeedBuff b) {
        b.remove(root); buffs.remove(b);
    }

    public void removeHealthOrb(HealthOrb h) {
        h.remove(root); healthOrbs.remove(h);
    }
}