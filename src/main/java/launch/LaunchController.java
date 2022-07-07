package launch;

import database.DB_CRUD;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import mainModule.MainApp;

import java.io.IOException;

public class LaunchController {

    @FXML private Label invalidLoginLB;
    @FXML private Button resetPassBtn, registerBtn, loginBtn;
    @FXML private TextField unameTF;
    @FXML private PasswordField pwdPF;

    // Switch to reset password scene.
    @FXML private void switchToRP() throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(LaunchApp.class.getResource("fxml/reset_pwd.fxml"));

        Stage resetPassStage = (Stage) resetPassBtn.getScene().getWindow();
        resetPassStage.setTitle("WDS - Reset password");
        resetPassStage.setScene(new Scene(fxmlLoader.load()));
        resetPassStage.setResizable(false);
    }

    // Switch to log in scene.
    @FXML protected static void switchToLogin(Button backBtn) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(LaunchApp.class.getResource("fxml/login.fxml"));

        Stage loginStage = (Stage) backBtn.getScene().getWindow();
        loginStage.setTitle("WDS - Login");
        loginStage.setScene(new Scene(fxmlLoader.load()));
        loginStage.setResizable(false);
    }

    // Switch to user registration scene.
    @FXML private void switchToUR() throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(LaunchApp.class.getResource("fxml/register_user.fxml"));

        Stage userRegStage = (Stage) registerBtn.getScene().getWindow();
        userRegStage.setTitle("WDS - User registration");
        userRegStage.setScene(new Scene(fxmlLoader.load()));
        userRegStage.setResizable(false);
    }

    // Switch to main scene.
    @FXML private void switchToMain() throws IOException {

        if (DB_CRUD.validateLogin(unameTF.getText(), pwdPF.getText())) {

            pwdPF.setText("");

            MainApp mainApp = new MainApp();
            mainApp.loggedInUser = unameTF.getText();
            mainApp.start(new Stage());

            Stage loginStage = (Stage) loginBtn.getScene().getWindow();
            loginStage.close();
        } else {

            invalidLoginLB.setVisible(true);
            resetPassBtn.setVisible(true);
        }
    }
}