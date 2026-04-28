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
import javafx.scene.text.TextAlignment;

public class menuUI {

    private static ProgressBar loadBar;

    protected static Scene createLoadingScene() {
        Label loadLabel = new Label("INITIALIZING SYSTEM...");
        loadLabel.setTextFill(Color.LIMEGREEN);
        loadBar = new ProgressBar(0);
        loadBar.setPrefWidth(300);

        VBox loadingRoot = new VBox(20, loadLabel, loadBar);
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
        menuBox.setSpacing(20);

        Label titleLabel = new Label("SPACE INVADERS");
        titleLabel.getStyleClass().add("game-title");

        Image bgImg = new Image(menuUI.class.getResource("/animatedbackground.gif").toExternalForm());
        ImageView background = new ImageView(bgImg);
        background.setFitWidth(720);
        background.setFitHeight(720);

        Button startBtn = new Button("START GAME");
        Button instBtn = new Button("INSTRUCTIONS");
        Button aboutBtn = new Button("ABOUT");
        Button exitBtn = new Button("EXIT");

        startBtn.setPrefWidth(200);
        instBtn.setPrefWidth(200);
        aboutBtn.setPrefWidth(200);
        exitBtn.setPrefWidth(200);

        StackPane menuRoot = new StackPane(background, menuBox);
        Scene menuScene = new Scene(menuRoot, 720, 720);

        String css = menuUI.class.getResource("style.css").toExternalForm();
        menuScene.getStylesheets().add(css);

        startBtn.setOnAction(e -> {
            stage.setScene(createLoadingScene());
            loadGame(stage, menuScene);
        });

        menuUI ui = new menuUI();
        aboutBtn.setOnAction(e -> stage.setScene(ui.abtBtn(stage, menuScene)));
        instBtn.setOnAction(e -> stage.setScene(ui.instructionBtn(stage, menuScene)));
        exitBtn.setOnAction(e -> System.exit(0));

        menuBox.getChildren().addAll(titleLabel, startBtn, instBtn, aboutBtn, exitBtn);

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
        VBox abt = new VBox(15);
        abt.setAlignment(Pos.CENTER);

        Label abtText = new Label(
                "--- PROJECT INFO ---\n\n" +
                        "THIS IS THE FINAL OUTPUT OF GROUP 5\n" +
                        "FOR COMPUTER PROGRAMMING 2\n\n" +
                        "DEVELOPERS:\n" +
                        "• ANTHONY LUMANTAO\n" +
                        "• MARC KEN LUZAME\n" +
                        "• CHRISTIAN TORRELINO\n" +
                        "• JOHN DENVER DIEGO\n" +
                        "• REIGNSTER RODRIGUEZ"
        );
        abtText.getStyleClass().add("about-text");
        abtText.setTextAlignment(TextAlignment.CENTER);
        abtText.setTextFill(Color.WHITE);

        Button backBtn = new Button("BACK TO MENU");
        backBtn.setOnAction(e -> stage.setScene(previousScene));

        abt.getChildren().addAll(abtText, backBtn);

        Image bgImg = new Image(menuUI.class.getResource("/animatedbackground.gif").toExternalForm());
        ImageView background = new ImageView(bgImg);
        background.setFitHeight(720);
        background.setFitWidth(720);

        StackPane root = new StackPane(background, abt);
        Scene scene = new Scene(root, 720, 720);
        scene.getStylesheets().add(menuUI.class.getResource("style.css").toExternalForm());

        return scene;
    }

    protected static Scene instructionBtn(Stage stage, Scene previousScene) {
        VBox instrct = new VBox(20);
        instrct.setAlignment(Pos.CENTER);

        Label instTitle = new Label("MISSION BRIEFING");
        instTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #00FF00;");

        Label instText = new Label(
                "OBJECTIVE: \nDestroy the alien invaders and protect the universe!\n\n" +
                        "CONTROLS: \n" +
                        "• [ W / A / S / D ] KEYS - Move Spaceship\n" +
                        "• [ SPACEBAR ] - Fire Laser Cannons\n" +
                        "• [ Q ] - EMERGENCY DASH (Triple Speed)\n\n" +
                        "SURVIVAL TIPS: \n" +
                        "• Use WASD to dodge in all directions.\n" +
                        "• Asteroids are destructive—shoot them down.\n" +
                        "• Hold Q while moving for tactical speed!"
        );
        instText.setTextAlignment(TextAlignment.CENTER);
        instText.setTextFill(Color.WHITE);
        instText.setStyle("-fx-font-size: 16px; -fx-line-spacing: 5px;");

        Button backBtn = new Button("BACK TO MENU");
        backBtn.setOnAction(e -> stage.setScene(previousScene));

        instrct.getChildren().addAll(instTitle, instText, backBtn);

        Image bgImg = new Image(menuUI.class.getResource("/animatedbackground.gif").toExternalForm());
        ImageView background = new ImageView(bgImg);
        background.setFitHeight(720);
        background.setFitWidth(720);

        StackPane root = new StackPane(background, instrct);
        Scene scene = new Scene(root, 720, 720);
        scene.getStylesheets().add(menuUI.class.getResource("style.css").toExternalForm());

        return scene;
    }
}