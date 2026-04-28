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

        Image bgImg = new Image(menuUI.class.getResource("/animatedbackground.gif").toExternalForm());
        ImageView background = new ImageView(bgImg);
        background.setFitHeight(720);
        background.setFitWidth(720);

        StackPane root = new StackPane(background, loadingRoot);
        return new Scene(root, 720, 720);
    }

    public static Scene createMenuScene(Stage stage) {
        VBox menuBox = new VBox();
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setSpacing(30);

        Label titleLabel = new Label("SPACE INVADERS");
        titleLabel.getStyleClass().add("game-title");

        Image bgImg = new Image(menuUI.class.getResource("/animatedbackground.gif").toExternalForm());
        ImageView background = new ImageView(bgImg);
        background.setFitWidth(720);
        background.setFitHeight(720);

        StackPane menuRoot = new StackPane(background, menuBox);
        Scene menuScene = new Scene(menuRoot, 720, 720);
        menuScene.getStylesheets().add(menuUI.class.getResource("style.css").toExternalForm());

        Button startBtn = new Button("Start Game");
        Button aboutBtn = new Button("About");
        Button instBtn = new Button("Instructions");
        Button exitBtn = new Button("Exit");

        menuUI ui = new menuUI();

        startBtn.setOnAction(e -> {
            Scene loadingScene = menuUI.createLoadingScene();
            stage.setScene(loadingScene);
            menuUI.loadGame(stage, menuScene);
        });

        menuBox.getChildren().addAll(titleLabel, startBtn, aboutBtn, instBtn, exitBtn);

        aboutBtn.setOnAction(e -> stage.setScene(ui.abtBtn(stage, menuScene)));
        instBtn.setOnAction(e -> stage.setScene(ui.instructionBtn(stage, menuScene)));
        exitBtn.setOnAction(e -> System.exit(0));

        return menuScene;
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
            try {
                Scene gameScene = startGame(stage, menuScene);
                stage.setScene(gameScene);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        task.setOnFailed(e -> task.getException().printStackTrace());

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    protected static Scene startGame(Stage stage, Scene previousScene) {
        Pane gameRoot = new Pane();
        Scene gameScene = new Scene(gameRoot, 720, 720);

        Image bgImg = new Image(menuUI.class.getResource("/animatedbackground.gif").toExternalForm());
        ImageView background = new ImageView(bgImg);
        background.setFitWidth(720);
        background.setFitHeight(720);
        gameRoot.getChildren().add(background);

        ImageView playerSprite = new ImageView(
                new Image(menuUI.class.getResource("/animation/enemy1.png").toExternalForm())
        );
        gameRoot.getChildren().add(playerSprite);

        Player player = new Player(playerSprite, 360, 600);
        Control control = new Control();

        playerBullets bullets = new playerBullets(player, gameRoot);
        control.setBullets(bullets);

        enemyBullets enemyBullets = new enemyBullets(gameRoot);
        enemySpawner spawner = new enemySpawner(gameRoot, enemyBullets);
        asteroidSpawner asteroidSpawner = new asteroidSpawner(gameRoot);

        GameLoop loop = new GameLoop(player, control, bullets, spawner, enemyBullets, asteroidSpawner, gameRoot, stage);
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

        Image bgImg = new Image(menuUI.class.getResource("/animatedbackground.gif").toExternalForm());
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

        Image bgImg = new Image(menuUI.class.getResource("/animatedbackground.gif").toExternalForm());
        ImageView background = new ImageView(bgImg);
        background.setFitHeight(720);
        background.setFitWidth(720);
        background.setPreserveRatio(false);

        StackPane root = new StackPane(background, instrct);
        return new Scene(root, 720, 720);
    }
}