package MainTimeline;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        Scene menuScene = menuUI.createMenuScene(stage);
        stage.setScene(menuScene);
        stage.setTitle("Space Invaders");
        stage.setResizable(false);
        stage.show();
    }
}