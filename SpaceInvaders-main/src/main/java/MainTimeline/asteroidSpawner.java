package MainTimeline;

import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

public class asteroidSpawner {

    private Pane root;
    private List<spaceDebris> asteroids = new ArrayList<>();

    private long lastSpawnTime = 0;
    private long spawnInterval = 2_000_000_000L; // new asteroid every 2 seconds

    private static final double MIN_DISTANCE = 60;

    public asteroidSpawner(Pane root) {
        this.root = root;
    }

    private boolean isTooClose(double newX) {
        for (spaceDebris a : asteroids) {
            if (Math.abs(a.getX() - newX) < MIN_DISTANCE) {
                return true;
            }
        }
        return false;
    }

    public void update(long now) {

        // spawn new asteroid every 2 seconds
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

        // update each asteroid, remove if off screen
        asteroids.removeIf(a -> {
            if (a.getY() > 760) {
                root.getChildren().remove(a.getSprite());
                return true;
            }
            a.update();
            return false;
        });
    }

    public List<spaceDebris> getAsteroids() {
        return asteroids;
    }
}