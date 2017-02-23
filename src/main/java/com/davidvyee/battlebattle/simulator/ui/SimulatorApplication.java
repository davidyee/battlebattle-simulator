package com.davidvyee.battlebattle.simulator.ui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SimulatorApplication extends Application {
    public static Application APPLICATION;

    public SimulatorApplication() {
        APPLICATION = this;
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("SimulatorApplication.fxml"));

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add("style.css");

        primaryStage.setTitle("BattleBattle Simulator");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
