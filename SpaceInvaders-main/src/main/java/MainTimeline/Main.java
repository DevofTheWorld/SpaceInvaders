package MainTimeline;

import javafx.application.Application;
import javafx.application.Platform;
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

        VBox menuBox = new VBox();
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setSpacing(30);

        Label titleLabel = new Label("SPACE INVADERS");
        titleLabel.getStyleClass().add("game-title");

        Image bgImg = new Image(getClass().getResource("/animatedbackground.gif").toExternalForm());
        ImageView background = new ImageView(bgImg);
        background.setFitWidth(720);
        background.setFitHeight(720);

        StackPane menuRoot = new StackPane(background, menuBox);
        Scene menuScene = new Scene(menuRoot, 720, 720);
        menuScene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        Button startBtn = new Button("Start Game");
        Button aboutBtn = new Button("About");
        Button instBtn = new Button("Instructions");
        Button exitBtn = new Button("Exit");

        menuUI ui = new menuUI();

        startBtn.setOnAction(e -> {
            // Step 1: show loading screen
            Scene loadingScene = menuUI.createLoadingScene();
            stage.setScene(loadingScene);

            // Step 2: loadGame() will handle switching to the game when done
            menuUI.loadGame(stage, menuScene);
        });

        menuBox.getChildren().addAll(titleLabel, startBtn, aboutBtn, instBtn, exitBtn);

        aboutBtn.setOnAction(e -> stage.setScene(ui.abtBtn(stage, menuScene)));
        instBtn.setOnAction(e -> stage.setScene(ui.instructionBtn(stage, menuScene)));
        exitBtn.setOnAction(e -> System.exit(0));

        stage.setScene(menuScene);
        stage.setTitle("Space Invaders");
        stage.setResizable(false);
        stage.show();
    }
}