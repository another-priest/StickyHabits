package org.example.stickyhabits;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ToolBar;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.stickyhabits.Components.DatabaseFunctions;
import org.example.stickyhabits.Components.HabitAIService;
import org.example.stickyhabits.Controllers.DashboardController;

import java.io.IOException;
import java.time.LocalDate;

import static javafx.application.Application.launch;

public class StickyHabits extends Application {
    private double xOffset = 0;
    private double yOffset = 0;
private final DatabaseFunctions db = new DatabaseFunctions();
private HabitAIService aiService;
    @Override
    public void init() throws IOException {

        aiService = new HabitAIService(db);
        if(!aiService.getRecommender().tryLoadModel()){
            aiService.retrain();
        }else{
            System.out.println("AI model loaded successfully");
        }

    }
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
        Parent root = fxmlLoader.load();
        DashboardController ctrl = fxmlLoader.getController();
        ctrl.setAiService(aiService);        // wstrzykujemy zależność

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
