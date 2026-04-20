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

        Canvas canvas = new Canvas(720, 720);
        GraphicsContext gb = canvas.getGraphicsContext2D();

        //backrground layout
        Image bg = new Image(getClass().getResource("/parallax-space-backgound.png").toExternalForm()); //
        gb.drawImage(bg, 0, 0, 720, 720);

        //Buttons for the main menu
        Button b1 = new Button("Start game");
        Button b2 = new Button("About");
        Button b3 = new Button("Instructions");
        Button b4 = new Button("Exit");

        vbox.getChildren().addAll(b1, b2, b3, b4);


        StackPane root = new StackPane();
        root.getChildren().addAll(canvas, vbox);
        Scene scene = new Scene(root, 720, 720);
        menuUI buttonFunc = new menuUI();

        //Sets actions on butto when clicked
        b1.setOnAction(e -> stage.setScene(buttonFunc.startGame(stage, scene)));
        b2.setOnAction(e -> stage.setScene(buttonFunc.abtBtn(stage, scene)));
        b3.setOnAction(e -> stage.setScene(buttonFunc.instructionBtn(stage, scene)));
        b4.setOnAction(e -> System.exit(0));


        stage.setScene(scene);
        stage.setTitle("Space Invaders");
        stage.show();


    }

}
