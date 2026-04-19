package MainTimeline;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class Main extends Application {
    public static void main(String[] args){
        launch(args);

    }


    @Override
    public void start(Stage stage){
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(15);

        Canvas canvas = new Canvas(520, 720);
        GraphicsContext gb = canvas.getGraphicsContext2D();

        Image bg = new Image(getClass().getResource("/parallax-space-backgound.png").toExternalForm());
        gb.drawImage(bg, 0, 0, 520, 720);

        Button b1 = new Button("Start game");
        Button b2 = new Button("About");
        Button b3 = new Button("Instructions");
        Button b4 = new Button("Exit");

        vbox.getChildren().addAll(b1, b2, b3, b4);
//**********************************************************//
        VBox gameroot = new VBox();
        gameroot.setAlignment(Pos.CENTER);
        Label gameText = new Label("Game will be release soon");
        Button backBtn = new Button("Back");

        gameroot.getChildren().addAll(gameText, backBtn);
        Scene gameScene = new Scene(gameroot, 520, 720);

//********************************************************//
        VBox abt = new VBox();
        abt.setAlignment(Pos.CENTER);
        Label abtText = new Label("The final output of Group 5 for Comprog 2");
        Button backBtn2 = new Button("Back");

        abt.getChildren().addAll(abtText, backBtn2);
        Scene aboutScene = new Scene(abt, 520, 720);

//********************************************************//

//********************************************************//
        VBox instrct = new VBox();
        instrct.setAlignment(Pos.CENTER);
        Label instText = new Label("Destroy the enemies and conquer the universe");
        Button instBtn2 = new Button("Back");

        instrct.getChildren().addAll(instText, instBtn2);
        Scene instScene = new Scene(instrct, 520, 720);

//********************************************************//

        b1.setOnAction(e -> stage.setScene(gameScene));
        b2.setOnAction(e -> stage.setScene(aboutScene));
        b3.setOnAction(e -> stage.setScene(instScene));
        b4.setOnAction(e -> System.exit(0));

        StackPane root = new StackPane();
        root.getChildren().addAll(canvas, vbox);

        Scene scene = new Scene(root, 520, 720);
        stage.setScene(scene);
        backBtn.setOnAction(e -> stage.setScene(scene));
        backBtn2.setOnAction(e -> stage.setScene(scene));
        instBtn2.setOnAction(e -> stage.setScene(scene));
        stage.setTitle("Space Invaders");
        stage.show();


    }

}
