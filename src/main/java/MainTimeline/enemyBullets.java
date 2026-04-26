package MainTimeline;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;
public class enemyBullets {

        private Pane root;
        private List<ImageView> bullets = new ArrayList<>();
        private Image bulletImg = new Image(
                enemyBullets.class.getResource("/shoot2.png").toExternalForm()
        );

        public enemyBullets(Pane root) {
            this.root = root;
        }

        public void shoot(double enemyX, double enemyY) {
            ImageView bullet = new ImageView(bulletImg);
            bullet.setFitWidth(12);
            bullet.setFitHeight(12);
            bullet.setSmooth(false);
            bullet.setPreserveRatio(true);

            bullet.setTranslateX(enemyX + 14);
            bullet.setTranslateY(enemyY + 30); // spawn just below enemy

            bullets.add(bullet);
            root.getChildren().add(bullet);
        }

        public void update() {
            bullets.removeIf(b -> {
                if (b.getTranslateY() > 740) { // off bottom of screen
                    root.getChildren().remove(b);
                    return true;
                }
                b.setTranslateY(b.getTranslateY() + 4); // move downward
                return false;
            });
        }

        public List<ImageView> getBullets() {
            return bullets;
        }
}
