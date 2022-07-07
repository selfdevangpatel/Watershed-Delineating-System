package launch;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.io.IOException;

public class LaunchApp extends Application {

    @Override
    public void start(Stage loginStage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(LaunchApp.class.getResource("fxml/login.fxml"));

        loginStage.setTitle("WDS - Login");
        loginStage.setScene(new Scene(fxmlLoader.load()));
        loginStage.setResizable(false);
        loginStage.getIcons().add(new Image(String.valueOf(LaunchApp.class.getResource("images/WDS_Logo.png"))));
        loginStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}