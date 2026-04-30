package MainTimeline;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

public class enemyBullets {

    private Pane root;
    private List<ImageView> bullets = new ArrayList<>();
    private List<double[]> directions = new ArrayList<>(); // dx, dy per bullet

    private Image bulletImg = new Image(
            enemyBullets.class.getResource("/shoot2.png").toExternalForm()
    );

    private static final double BULLET_SPEED = 4.5;

    public enemyBullets(Pane root) {
        this.root = root;
    }

    public void shoot(double enemyX, double enemyY, double playerX, double playerY) {
        ImageView bullet = new ImageView(bulletImg);
        bullet.setFitWidth(14);
        bullet.setFitHeight(14);
        bullet.setSmooth(false);
        bullet.setPreserveRatio(true);

        double spawnX = enemyX + 14;
        double spawnY = enemyY + 20;

        bullet.setTranslateX(spawnX);
        bullet.setTranslateY(spawnY);

        // direction vector toward player
        double dirX = playerX - spawnX;
        double dirY = playerY - spawnY;
        double len = Math.sqrt(dirX * dirX + dirY * dirY);
        if (len == 0) len = 1;
        dirX /= len;
        dirY /= len;

        // rotate bullet sprite to face direction of travel
        double angle = Math.toDegrees(Math.atan2(dirY, dirX)) + 90;
        bullet.setRotate(angle);

        bullets.add(bullet);
        directions.add(new double[]{dirX, dirY});
        root.getChildren().add(bullet);
    }

    public void update() {
        List<ImageView> toRemove = new ArrayList<>();

        for (int i = 0; i < bullets.size(); i++) {
            ImageView b = bullets.get(i);
            double[] dir = directions.get(i);

            double newX = b.getTranslateX() + dir[0] * BULLET_SPEED;
            double newY = b.getTranslateY() + dir[1] * BULLET_SPEED;

            b.setTranslateX(newX);
            b.setTranslateY(newY);

            // remove if off screen in any direction
            if (newX < -20 || newX > 740 || newY < -20 || newY > 760) {
                toRemove.add(b);
            }
        }

        for (ImageView b : toRemove) {
            int i = bullets.indexOf(b);
            if (i >= 0) {
                directions.remove(i);
                bullets.remove(i);
            }
            root.getChildren().remove(b);
        }
    }

    public List<ImageView> getBullets() { return bullets; }
}