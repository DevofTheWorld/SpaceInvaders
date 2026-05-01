package MainTimeline;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class menuUI {

    private static ProgressBar loadBar;

    // shared MediaPlayer — one instance for the whole app lifetime
    private static javafx.scene.media.MediaPlayer mediaPlayer;

    // persistent title animation — never stopped between screens
    private static AnimationTimer titleAnim;
    private static ImageView titleLabel;
    private static Image titleImg1;
    private static Image titleImg2;
    private static long[] lastSwap = {0};
    private static boolean[] showingFirst = {true};

    private static ImageView makeImageButton(String path, double width) {
        Image img = new Image(menuUI.class.getResource(path).toExternalForm());
        ImageView btn = new ImageView(img);
        btn.setFitWidth(width);
        btn.setPreserveRatio(true);
        btn.setCursor(javafx.scene.Cursor.HAND);
        btn.addEventHandler(MouseEvent.MOUSE_ENTERED,  e -> btn.setOpacity(0.8));
        btn.addEventHandler(MouseEvent.MOUSE_EXITED,   e -> btn.setOpacity(1.0));
        btn.addEventHandler(MouseEvent.MOUSE_PRESSED,  e -> btn.setOpacity(0.6));
        btn.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> btn.setOpacity(1.0));
        return btn;
    }

    // initialise the title ImageView and its AnimationTimer once, ever
    private static void ensureTitleAnim() {
        if (titleImg1 == null) {
            titleImg1 = new Image(menuUI.class.getResource("/ui/title.png").toExternalForm());
            titleImg2 = new Image(menuUI.class.getResource("/ui/title2.png").toExternalForm());
        }
        if (titleLabel == null) {
            titleLabel = new ImageView(titleImg1);
            titleLabel.setFitWidth(600);
            titleLabel.setPreserveRatio(true);
        }
        if (titleAnim == null) {
            titleAnim = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    if (now - lastSwap[0] >= 500_000_000L) {
                        titleLabel.setImage(showingFirst[0] ? titleImg2 : titleImg1);
                        showingFirst[0] = !showingFirst[0];
                        lastSwap[0] = now;
                    }
                }
            };
            titleAnim.start(); // started once, never stopped
        }
    }

    private static void ensureMusicPlaying() {
        if (mediaPlayer == null) {
            javafx.scene.media.Media bgm = new javafx.scene.media.Media(
                    menuUI.class.getResource("/BGM/bgm.mp3").toExternalForm()
            );
            mediaPlayer = new javafx.scene.media.MediaPlayer(bgm);
            mediaPlayer.setCycleCount(javafx.scene.media.MediaPlayer.INDEFINITE);
            mediaPlayer.setVolume(0.5);
            mediaPlayer.setRate(1.0);
        } else {
            mediaPlayer.setRate(1.0);
            mediaPlayer.setVolume(0.5);
        }
        if (mediaPlayer.getStatus() != javafx.scene.media.MediaPlayer.Status.PLAYING) {
            mediaPlayer.play();
        }
    }

    protected static Scene createLoadingScene() {
        Image loadingTextImg = new Image(menuUI.class.getResource("/ui/loading.png").toExternalForm());
        ImageView loadingText = new ImageView(loadingTextImg);
        loadingText.setFitWidth(200);
        loadingText.setPreserveRatio(true);

        loadBar = new ProgressBar(0);
        loadBar.setPrefWidth(400);
        loadBar.setPrefHeight(30);

        VBox loadingRoot = new VBox(10, loadingText, loadBar);
        loadingRoot.setAlignment(Pos.CENTER);

        Image bgImg = new Image(menuUI.class.getResource("/animatedbackground.gif").toExternalForm());
        ImageView background = new ImageView(bgImg);
        background.setFitHeight(720);
        background.setFitWidth(720);

        StackPane root = new StackPane(background, loadingRoot);
        return new Scene(root, 720, 720);
    }

    public static Scene createMenuScene(Stage stage) {
        ensureMusicPlaying();
        ensureTitleAnim(); // guarantees titleLabel is ready and timer is running

        // title box
        VBox titleBox = new VBox(titleLabel);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(250, 0, 0, 0));

        ImageView startBtn = makeImageButton("/ui/btnStart.png",        230);
        ImageView aboutBtn = makeImageButton("/ui/btnAbout.png",        230);
        ImageView instBtn  = makeImageButton("/ui/btnInstructions.png", 230);
        ImageView exitBtn  = makeImageButton("/ui/btnExit.png",         230);

        VBox buttonBox = new VBox(10, startBtn, aboutBtn, instBtn, exitBtn);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(35, 0, 200, 0));

        VBox menuBox = new VBox();
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setSpacing(15);
        menuBox.getChildren().addAll(titleBox, buttonBox);

        Image bgImg = new Image(menuUI.class.getResource("/animatedbackground.gif").toExternalForm());
        ImageView background = new ImageView(bgImg);
        background.setFitWidth(720);
        background.setFitHeight(720);

        StackPane menuRoot = new StackPane(background, menuBox);
        Scene menuScene = new Scene(menuRoot, 720, 720);
        menuScene.getStylesheets().add(menuUI.class.getResource("style.css").toExternalForm());

        menuUI ui = new menuUI();

        startBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            stage.setScene(createLoadingScene());
            loadGame(stage, menuScene);
        });

        aboutBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, e ->
                stage.setScene(ui.abtBtn(stage, menuScene)));

        instBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, e ->
                stage.setScene(ui.instructionBtn(stage, menuScene)));

        exitBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> System.exit(0));

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
                stage.setScene(startGame(stage, menuScene));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        task.setOnFailed(e -> task.getException().printStackTrace());

        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
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

        Player player                   = new Player(playerSprite, 360, 600);
        Control control                 = new Control();
        playerBullets bullets           = new playerBullets(player, gameRoot);
        control.setBullets(bullets);
        enemyBullets enemyBullets       = new enemyBullets(gameRoot);
        enemySpawner spawner            = new enemySpawner(gameRoot, enemyBullets);
        asteroidSpawner asteroidSpawner = new asteroidSpawner(gameRoot);

        GameLoop loop = new GameLoop(player, control, bullets, spawner, enemyBullets,
                asteroidSpawner, gameRoot, stage, mediaPlayer);
        loop.start();

        Platform.runLater(() -> {
            gameScene.getRoot().requestFocus();
            control.setup(gameScene);
        });

        return gameScene;
    }

    protected static Scene abtBtn(Stage stage, Scene previousScene) {
        ImageView abtText = new ImageView(
                new Image(menuUI.class.getResource("/ui/aboutText.png").toExternalForm())
        );
        abtText.setFitWidth(500);
        abtText.setPreserveRatio(true);

        ImageView backBtn = makeImageButton("/ui/btnBack.png", 200);
        backBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> stage.setScene(previousScene));

        VBox abt = new VBox(30, abtText, backBtn);
        abt.setAlignment(Pos.CENTER);

        Image bgImg = new Image(menuUI.class.getResource("/animatedbackground.gif").toExternalForm());
        ImageView background = new ImageView(bgImg);
        background.setFitHeight(720);
        background.setFitWidth(720);
        background.setPreserveRatio(false);

        StackPane root = new StackPane(background, abt);
        return new Scene(root, 720, 720);
    }

    protected static Scene instructionBtn(Stage stage, Scene previousScene) {
        ImageView instText = new ImageView(
                new Image(menuUI.class.getResource("/ui/instructionsText.png").toExternalForm())
        );
        instText.setFitWidth(500);
        instText.setPreserveRatio(true);

        ImageView backBtn = makeImageButton("/ui/btnBack.png", 200);
        backBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> stage.setScene(previousScene));

        VBox instrct = new VBox(30, instText, backBtn);
        instrct.setAlignment(Pos.CENTER);

        Image bgImg = new Image(menuUI.class.getResource("/animatedbackground.gif").toExternalForm());
        ImageView background = new ImageView(bgImg);
        background.setFitHeight(720);
        background.setFitWidth(720);
        background.setPreserveRatio(false);

        StackPane root = new StackPane(background, instrct);
        return new Scene(root, 720, 720);
    }
}