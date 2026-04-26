package MainTimeline;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
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

        // When loading finishes, switch to the actual game
        task.setOnSucceeded(e -> {
            Scene gameScene = startGame(stage, menuScene);
            stage.setScene(gameScene);
        });

        new Thread(task).start();
    }

    protected static Scene startGame(Stage stage, Scene previousScene) {

        // Use Pane instead of StackPane — positions everything by TranslateX/Y correctly
        Pane gameRoot = new Pane();
        Scene gameScene = new Scene(gameRoot, 720, 720);

        // Background — add it first so it's behind everything
        Image bgImg = new Image(menuUI.class.getResource("/animatedbackground.gif").toExternalForm());
        ImageView background = new ImageView(bgImg);
        background.setFitWidth(720);
        background.setFitHeight(720);
        gameRoot.getChildren().add(background);

        // Player sprite — make sure this is YOUR player image, not the enemy
        ImageView playerSprite = new ImageView(
                new Image(menuUI.class.getResource("/enemy1.png").toExternalForm())
        );
        gameRoot.getChildren().add(playerSprite);

        Player player = new Player(playerSprite, 360, 600); // start near bottom center
        Control control = new Control();

        playerBullets bullets = new playerBullets(player, gameRoot);
        control.setBullets(bullets);

        enemyBullets enemyBullets = new enemyBullets(gameRoot);
        enemySpawner spawner = new enemySpawner(gameRoot, enemyBullets);

        GameLoop loop = new GameLoop(player, control, bullets, spawner, enemyBullets);
        loop.start();

        Platform.runLater(() -> {
            gameScene.getRoot().requestFocus();
            control.setup(gameScene);
        });

        return gameScene;
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