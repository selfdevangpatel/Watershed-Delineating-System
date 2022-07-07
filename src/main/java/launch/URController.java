package launch;

import database.DB_CRUD;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URController {

    @FXML private TextField unameTF, emailTF;
    @FXML private PasswordField pwdPF, confirmPwdPF;
    @FXML private Label unameExistsLB, unameInvalidLB, pwdInvalidLB, pwdUnmatchLB, emailTakenLB;
    @FXML private Label emailInvalidLB, URSuccessLB;
    @FXML private Button backBtn;

    @FXML private void switchToLogin() throws IOException {
        LaunchController.switchToLogin(backBtn);
    }

    @FXML private void registerUser() {

        String emailRegex = "^\\w+([\\-]?\\w+)*@\\w+([\\-]?\\w+)*(\\.\\w{2,3})+$";
        String email = emailTF.getText();
        Pattern pattern = Pattern.compile(emailRegex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);

        boolean unameExists = DB_CRUD.unameExists(unameTF.getText());
        boolean unameValid = unameTF.getText().length() > 3;
        boolean pwdValid = pwdPF.getText().length() > 3;
        boolean pwdMatches = pwdPF.getText().equals(confirmPwdPF.getText());
        boolean emailExists = DB_CRUD.emailExists(emailTF.getText());
        boolean emailValid = matcher.find();

        unameExistsLB.setVisible(unameExists);
        unameInvalidLB.setVisible(!unameValid);
        pwdInvalidLB.setVisible(!pwdValid);
        pwdUnmatchLB.setVisible(!pwdMatches);
        emailTakenLB.setVisible(emailExists);
        emailInvalidLB.setVisible(!emailValid);
        URSuccessLB.setVisible(false);

        if (!unameExists && unameValid && pwdValid && pwdMatches && !emailExists && emailValid) {

            DB_CRUD.registerUser(unameTF.getText(), pwdPF.getText(), emailTF.getText());
            URSuccessLB.setVisible(true);
        }
    }
}