package MainTimeline;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class menuUI {


    protected static Scene startGame(Stage stage, Scene previousScene1){ //Sets a new scene for start game
    VBox gameroot = new VBox();
    gameroot.setSpacing(15);
    gameroot.setAlignment(Pos.CENTER);
    Label gameText = new Label("Game will be released soon");
    Button backBtn = new Button("Back");
    backBtn.setOnAction(e -> {stage.setScene(previousScene1);}); //makes back button functional

    gameroot.getChildren().addAll(gameText, backBtn);
    Scene gameScene = new Scene(gameroot, 720, 720);

    return gameScene;

    }

    protected static Scene abtBtn(Stage stage, Scene previousScene){ //Sets a new scene for about button
        VBox abt = new VBox();
        abt.setSpacing(15);
        abt.setAlignment(Pos.CENTER);
        Label abtText = new Label("The final output of Group 5 for Comprog 2");
        Button backBtn2 = new Button("Back");
        backBtn2.setOnAction(e -> stage.setScene(previousScene)); //makes back button functional

        abt.getChildren().addAll(abtText, backBtn2);
        Scene abtScene = new Scene(abt, 720, 720);

        return abtScene;
    }

    protected static Scene instructionBtn(Stage stage, Scene previousScene){ //Sets a new scene for instruction button
        VBox instrct = new VBox();
        instrct.setSpacing(15);
        instrct.setAlignment(Pos.CENTER);
        Label instText = new Label("Destroy the enemies and conquer the universe");
        Button instBtn2 = new Button("Back");
        instBtn2.setOnAction(e -> stage.setScene(previousScene)); //makes back button functional

        instrct.getChildren().addAll(instText, instBtn2);
        Scene instScene = new Scene(instrct, 720, 720);

        return instScene;
    }


}
