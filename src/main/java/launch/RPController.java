package launch;

import database.DB_CRUD;

import email.EmailUtil;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class RPController {

    @FXML private Button resetPwdBtn, sendOTPBtn, backBtn;
    @FXML private TextField otpTF, unameTF;
    @FXML private Label pwdInvalidLB, pwdUnmatchLB, wrongOTPLB, otpSuccessLB, unameInvalidLB;
    @FXML private Label rpSuccessLB, otpFailedLB;
    @FXML private PasswordField newPwdPF, confirmPwdPF;
    @FXML private ProgressIndicator otpPI;

    int OTP;
    boolean sendSuccess;

    @FXML private void switchToLogin() throws IOException {
        LaunchController.switchToLogin(backBtn);
    }

    @FXML private void sendOTP() {

        String uname = unameTF.getText();
        boolean unameExists = DB_CRUD.unameExists(uname);

        sendSuccess = false;
        unameInvalidLB.setVisible(!unameExists);
        otpFailedLB.setVisible(false);

        if (unameExists) {

            // OTP ranges from 111111 to 999999.
            OTP = (int)(Math.random() * (888890) + 111111);
            String recipient = DB_CRUD.getEmail(uname);

            Service<String> service = new Service<>() {

                @Override
                protected Task<String> createTask() {

                    return new Task<>() {

                        @Override
                        protected String call() throws Exception {

                            if (EmailUtil.sendMail(uname, recipient, OTP)) {

                                Timer timer = new Timer();
                                TimerTask task = new TimerTask() {

                                    @Override
                                    public void run() {
                                        OTP = -1;
                                    }
                                };
                                timer.schedule(task, 300000);
                                sendSuccess = true;
                            }
                            otpSuccessLB.setVisible(sendSuccess);
                            otpTF.setDisable(!sendSuccess);
                            resetPwdBtn.setDisable(!sendSuccess);
                            sendOTPBtn.setDisable(sendSuccess);
                            unameTF.setDisable(sendSuccess);

                            return null;
                        }
                    };
                }
            };
            otpPI.visibleProperty().bind(service.runningProperty());
            service.start();
            service.setOnFailed(event -> otpFailedLB.setVisible(true));
        }
    }

    @FXML private void resetPassword() {

        boolean pwordValid = newPwdPF.getText().length() > 3;
        boolean pwordMatches = newPwdPF.getText().equals(confirmPwdPF.getText());
        boolean otpValid = !otpTF.getText().equals("-1") && otpTF.getText().equals(Integer.toString(OTP));

        pwdInvalidLB.setVisible(!pwordValid);
        pwdUnmatchLB.setVisible(!pwordMatches);
        wrongOTPLB.setVisible(!otpValid);

        if (pwordValid && pwordMatches && otpValid) {

            boolean setPwd = DB_CRUD.setPassword(unameTF.getText(), newPwdPF.getText());

            newPwdPF.setDisable(setPwd);
            confirmPwdPF.setDisable(setPwd);
            otpTF.setDisable(setPwd);
            resetPwdBtn.setDisable(setPwd);
            rpSuccessLB.setVisible(setPwd);
        }
    }
}