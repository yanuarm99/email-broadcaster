package com.yanuar;

import com.yanuar.ui.MainFrameController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        MainFrameController controller = new MainFrameController();
        Scene scene = new Scene(controller.getView(), 900, 600);
        primaryStage.setTitle("Broadcast Email");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
