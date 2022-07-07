package wbt;

import javafx.collections.FXCollections;
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

import raster.RasterManager;

import java.io.File;
import java.io.IOException;

public class ExtractStreams {

    @FXML private Label finishedLB, unsuccessfulLB;
    @FXML private ComboBox<String> inRasCB;
    @FXML private TextField inRasTF, outRasTF;
    @FXML private CheckBox zeroBackCBX, addToMapCBX;
    @FXML private Spinner<Double> thresholdSP;
    @FXML private ProgressIndicator progressInd;
    @FXML private Button inRasBtn, outRasBtn, applyBtn;

    boolean selCBItem = false;

    public ExtractStreams() throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(ExtractStreams.class.getResource("fxml/extract_streams.fxml"));
        fxmlLoader.setController(this);

        Stage esStage = new Stage();
        esStage.setTitle("Extract Streams");
        esStage.setScene(new Scene(fxmlLoader.load()));
        esStage.setResizable(false);
        esStage.initOwner(MainApp.mainStage);
        esStage.initModality(Modality.APPLICATION_MODAL);
        esStage.show();

        inRasCB.getItems().setAll(FXCollections.observableArrayList(RasterManager.rasterNameList));
        inRasCB.getSelectionModel().selectedItemProperty().addListener(o -> {

            selCBItem = !inRasCB.getSelectionModel().isEmpty();
            inRasTF.setDisable(selCBItem);
            inRasBtn.setDisable(selCBItem);
        });

        thresholdSP.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 1000000.0, 100.0));

        inRasBtn.setOnAction(e -> {

            try {

                FileChooser fC = new FileChooser();
                File rF = fC.showOpenDialog(esStage);
                if (rF != null) inRasTF.setText(rF.getAbsolutePath());
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        });

        outRasBtn.setOnAction(e -> {

            try {

                FileChooser fC = new FileChooser();
                File rF = fC.showSaveDialog(esStage);
                if (rF != null) outRasTF.setText(rF.getAbsolutePath());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        applyBtn.setOnAction(e -> runESTool());
    }

    private void runESTool() {

        finishedLB.setVisible(false); unsuccessfulLB.setVisible(false);

        if ((selCBItem || !inRasTF.getText().isBlank())  && !outRasTF.getText().isBlank()) {

            String inPath = System.getProperty("user.dir") + "\\src\\main\\resources\\raster\\rasterCache\\";
            String outPath = outRasTF.getText();

            if (selCBItem) inPath = inPath + inRasCB.getSelectionModel().getSelectedItem();
            else inPath = inRasTF.getText();

            String finalInPath = inPath;
            Service<String> service = new Service<>() {

                @Override
                protected Task<String> createTask() {

                    return new Task<>() {

                        @Override
                        protected String call() {

                            if (RunWBT.runScript(" -r=\"ExtractStreams\"", " --flow_accum=\"" + finalInPath + "\"",
                                                 " -o=\"" + outPath + "\"", " --threshold=" + thresholdSP.getValue(),
                                                 zeroBackCBX.isSelected() ? " --zero_background" : "", "", "")) {

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
            service.setOnSucceeded(e -> {

                if (addToMapCBX.isSelected() && finishedLB.isVisible()) RasterManager.addRasterLayer(outPath);
            });
        }
    }
}