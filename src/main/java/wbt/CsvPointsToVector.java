package wbt;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import mainModule.MainApp;

import java.io.File;
import java.io.IOException;

public class CsvPointsToVector {

    @FXML private Label finishedLB, unsuccessfulLB;
    @FXML private TextField inCsvTF, outVecTF, epsgProjTF;
    @FXML private ProgressIndicator progressInd;
    @FXML private Spinner<Integer> xfieldSP, yfieldSP;
    @FXML private Button inCsvBtn, outVecBtn, applyBtn;

    public CsvPointsToVector() throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(CsvPointsToVector.class.getResource("fxml/csv_vector.fxml"));
        fxmlLoader.setController(this);

        Stage cptvStage = new Stage();
        cptvStage.setTitle("CSV Points To Vector");
        cptvStage.setScene(new Scene(fxmlLoader.load()));
        cptvStage.setResizable(false);
        cptvStage.initOwner(MainApp.mainStage);
        cptvStage.initModality(Modality.APPLICATION_MODAL);
        cptvStage.show();

        xfieldSP.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000, 0));
        yfieldSP.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000, 1));

        inCsvBtn.setOnAction(e -> {

            try {

                FileChooser fC = new FileChooser();
                File csvF = fC.showOpenDialog(cptvStage);
                if (csvF != null) inCsvTF.setText(csvF.getAbsolutePath());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        outVecBtn.setOnAction(e -> {

            try {

                FileChooser fC = new FileChooser();
                File vF = fC.showSaveDialog(cptvStage);
                if (vF != null) outVecTF.setText(vF.getAbsolutePath());
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        });

        applyBtn.setOnAction(e -> runCsvToVecTool());
    }

    private void runCsvToVecTool() {

        finishedLB.setVisible(false); unsuccessfulLB.setVisible(false);

        if (!inCsvTF.getText().isBlank() && !outVecTF.getText().isBlank() && !epsgProjTF.getText().isBlank()) {

            String inPath = inCsvTF.getText();
            String outPath = outVecTF.getText();

            Service<String> service = new Service<>() {

                @Override
                protected Task<String> createTask() {

                    return new Task<>() {

                        @Override
                        protected String call() {

                            if (RunWBT.runScript(" -r=\"CsvPointsToVector\"", " -i=\"" + inPath + "\"", " -o=\"" +
                                                 outPath + "\"", " --xfield=" + xfieldSP.getValue(), " --yfield=" +
                                                 yfieldSP.getValue(), "  --epsg=" + epsgProjTF.getText(), "")) {

                                finishedLB.setVisible(true); unsuccessfulLB.setVisible(false);
                            } else {
                                finishedLB.setVisible(false); unsuccessfulLB.setVisible(true);
                            }

                            return null;
                        }
                    };
                }
            };
            progressInd.visibleProperty().bind(service.runningProperty());
            service.start();
        }
    }
}