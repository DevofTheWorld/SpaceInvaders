package MainTimeline;

import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.paint.Color;

public class menuUI {

    private static ProgressBar loadBar;

    protected static Scene createLoadingScene() {

        Label loadLabel = new Label("Loading...");
        loadBar = new ProgressBar(0);

        VBox loadingRoot = new VBox(10, loadLabel, loadBar);
        loadingRoot.setAlignment(Pos.CENTER);

        Image bgImg = new Image("/animatedbackground.gif");
        ImageView background = new ImageView(bgImg);
        background.setFitHeight(720);
        background.setFitWidth(720);

        StackPane root = new StackPane(background, loadingRoot);

        return new Scene(root, 720, 720);
    }

    public static void loadGame(Stage stage, Scene menuScene) {

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {

                for (int i = 0; i <= 100; i++) {
                    Thread.sleep(20);
                    updateProgress(i, 100);
                }

                return null;
            }
        };

        loadBar.progressProperty().bind(task.progressProperty());

        task.setOnSucceeded(e -> {
            stage.setScene(startGame(stage, menuScene));
        });

        new Thread(task).start();
    }

    protected static Scene startGame(Stage stage, Scene previousScene1) {

        VBox gameroot = new VBox();
        gameroot.setSpacing(15);
        gameroot.setAlignment(Pos.CENTER);


        Image bgImg = new Image("/animatedbackground.gif");
        ImageView background = new ImageView(bgImg);
        background.setFitHeight(720);
        background.setFitWidth(720);
        background.setPreserveRatio(false);

        StackPane root = new StackPane(background, gameroot);

        return new Scene(root, 720, 720);
    }

    protected static Scene abtBtn(Stage stage, Scene previousScene) {

        VBox abt = new VBox();
        abt.setSpacing(15);
        abt.setAlignment(Pos.CENTER);

        Label abtText = new Label("The final output of Group 5 for Comprog 2");
        abtText.setTextFill(Color.WHITE);

        Button backBtn2 = new Button("Back");
        backBtn2.setOnAction(e -> stage.setScene(previousScene));

        abt.getChildren().addAll(abtText, backBtn2);

        Image bgImg = new Image("/animatedbackground.gif");
        ImageView background = new ImageView(bgImg);
        background.setFitHeight(720);
        background.setFitWidth(720);
        background.setPreserveRatio(false);

        StackPane root = new StackPane(background, abt);

        return new Scene(root, 720, 720);
    }

    protected static Scene instructionBtn(Stage stage, Scene previousScene) {

        VBox instrct = new VBox();
        instrct.setSpacing(15);
        instrct.setAlignment(Pos.CENTER);

        Label instText = new Label("Destroy the enemies and conquer the universe");
        instText.setTextFill(Color.WHITE);

        Button instBtn2 = new Button("Back");
        instBtn2.setOnAction(e -> stage.setScene(previousScene));

        instrct.getChildren().addAll(instText, instBtn2);

        Image bgImg = new Image("/animatedbackground.gif");
        ImageView background = new ImageView(bgImg);
        background.setFitHeight(720);
        background.setFitWidth(720);
        background.setPreserveRatio(false);

        StackPane root = new StackPane(background, instrct);

        return new Scene(root, 720, 720);
    }
}