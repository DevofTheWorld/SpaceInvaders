package tryTheButton;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) {

        // 🎯 Create a button
        Button button = new Button("CLICK TO TEST JAVA FX");

        // 🔥 When clicked, change text
        button.setOnAction(e -> {
            button.setText("IT WORKS 🎉");
            System.out.println("Button clicked - JavaFX is working!");
        });

        // Layout
        Pane root = new Pane();
        root.getChildren().add(button);

        Scene scene = new Scene(root, 400, 300);

        stage.setTitle("JavaFX Test");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();

    }
}
