package org.example.stickyhabits;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ToolBar;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.stickyhabits.Controllers.DashboardController;

import java.io.IOException;

import static javafx.application.Application.launch;

public class StickyHabits extends Application {
    private double xOffset = 0;
    private double yOffset = 0;
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
        Parent root = fxmlLoader.load();
        DashboardController controller = fxmlLoader.getController();
        ToolBar toolbar = controller.getToolbar();
        Scene scene = new Scene(root);
        toolbar.setOnMousePressed(e -> {
            xOffset = e.getSceneX();
            yOffset = e.getSceneY();
        });

        toolbar.setOnMouseDragged(e -> {
                stage.setX(e.getScreenX() - xOffset);
                stage.setY(e.getScreenY() - yOffset);
        });

        stage.initStyle(StageStyle.UNDECORATED);
        stage.setWidth(250);
        stage.setHeight(250);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
