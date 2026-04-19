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

        Canvas canvas = new Canvas(960, 540);
        GraphicsContext gb = canvas.getGraphicsContext2D();

        Image bg = new Image(getClass().getResource("/parallax-space-backgound.png").toExternalForm());
        gb.drawImage(bg, 0, 0, 960, 540);

        Button b1 = new Button("Start game");
        Button b2 = new Button("About");
        Button b3 = new Button("Instructions");
        Button b4 = new Button("Exit");

        vbox.getChildren().addAll(b1, b2, b3, b4);

        StackPane root = new StackPane();
        root.getChildren().addAll(canvas, vbox);


        Scene scene = new Scene(root, 960, 540);
        stage.setScene(scene);
        stage.setTitle("Space Invaders");
        stage.show();


    }

}
