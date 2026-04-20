package MainTimeline;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.paint.Color;

public class menuUI {


    protected static Scene startGame(Stage stage, Scene previousScene1){ //Sets a new scene for start game
    VBox gameroot = new VBox();
    gameroot.setSpacing(15);
    gameroot.setAlignment(Pos.CENTER);

    Label gameText = new Label("Game will be released soon");
    gameText.setTextFill(Color.WHITE);
    Button backBtn = new Button("Back");

    gameroot.getChildren().addAll(gameText, backBtn);

    backBtn.setOnAction(e -> {stage.setScene(previousScene1);}); //makes back button functional

        Image bgImg = new Image("/animatedbackground.gif");
        ImageView background = new ImageView(bgImg);
        background.setFitHeight(720);
        background.setFitWidth(720);
        background.setPreserveRatio(false);


        StackPane root = new StackPane();
        root.getChildren().addAll(background, gameroot);

        return new Scene(root, 720, 720);


    }

    protected static Scene abtBtn(Stage stage, Scene previousScene){ //Sets a new scene for about button
        VBox abt = new VBox();
        abt.setSpacing(15);
        abt.setAlignment(Pos.CENTER);

        Label abtText = new Label("The final output of Group 5 for Comprog 2");
        abtText.setTextFill(Color.WHITE);

        Button backBtn2 = new Button("Back");
        backBtn2.setOnAction(e -> stage.setScene(previousScene)); //makes back button functional

        abt.getChildren().addAll(abtText, backBtn2);

        Image bgImg = new Image("/animatedbackground.gif");
        ImageView background = new ImageView(bgImg);
        background.setFitHeight(720);
        background.setFitWidth(720);
        background.setPreserveRatio(false);


        StackPane root = new StackPane();
        root.getChildren().addAll(background, abt);

        return new Scene(root, 720, 720);
    }

    protected static Scene instructionBtn(Stage stage, Scene previousScene){ //Sets a new scene for instruction button
        VBox instrct = new VBox();
        instrct.setSpacing(15);
        instrct.setAlignment(Pos.CENTER);

        Label instText = new Label("Destroy the enemies and conquer the universe");
        instText.setTextFill(Color.WHITE);
        Button instBtn2 = new Button("Back");

        instrct.getChildren().addAll(instText, instBtn2);

        instBtn2.setOnAction(e -> stage.setScene(previousScene)); //makes back button functional
        Image bgImg = new Image("/animatedbackground.gif");
        ImageView background = new ImageView(bgImg);
        background.setFitHeight(720);
        background.setFitWidth(720);
        background.setPreserveRatio(false);


        StackPane root = new StackPane();
        root.getChildren().addAll(background, instrct);

        return new Scene(root, 720, 720);
    }


}
