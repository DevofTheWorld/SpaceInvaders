package MainTimeline;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {

        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(15);

        Image bgImg = new Image("/animatedbackground.gif");
        ImageView background = new ImageView(bgImg);
        background.setFitHeight(720);
        background.setFitWidth(720);
        background.setPreserveRatio(false);

        StackPane root = new StackPane(background, vbox);
        Scene scene = new Scene(root, 720, 720);

        Button b1 = new Button("Start game");
        Button b2 = new Button("About");
        Button b3 = new Button("Instructions");
        Button b4 = new Button("Exit");

        vbox.getChildren().addAll(b1, b2, b3, b4);

        menuUI ui = new menuUI();
        b1.setOnAction(e -> {

            Scene loadingScene = menuUI.createLoadingScene();
            stage.setScene(loadingScene);

            menuUI.loadGame(stage, scene);
        });

        b2.setOnAction(e -> stage.setScene(ui.abtBtn(stage, scene)));
        b3.setOnAction(e -> stage.setScene(ui.instructionBtn(stage, scene)));
        b4.setOnAction(e -> System.exit(0));

        stage.setScene(scene);
        stage.setTitle("Space Invaders");
        stage.show();
    }
}