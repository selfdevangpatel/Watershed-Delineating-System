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

public class Watershed {

    @FXML private Label finishedLB, unsuccessfulLB;
    @FXML private ComboBox<String> inRasCB;
    @FXML private TextField inRasTF, outRasTF, inShpTF;
    @FXML private CheckBox essCBX, addToMapCBX;
    @FXML private ProgressIndicator progressInd;
    @FXML private Button inRasBtn, inShpBtn, outRasBtn, applyBtn;

    boolean selCBItem = false;

    public Watershed() throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(Watershed.class.getResource("fxml/watershed.fxml"));
        fxmlLoader.setController(this);

        Stage wStage = new Stage();
        wStage.setTitle("Watershed");
        wStage.setScene(new Scene(fxmlLoader.load()));
        wStage.setResizable(false);
        wStage.initOwner(MainApp.mainStage);
        wStage.initModality(Modality.APPLICATION_MODAL);
        wStage.show();

        inRasCB.getItems().setAll(FXCollections.observableArrayList(RasterManager.rasterNameList));
        inRasCB.getSelectionModel().selectedItemProperty().addListener(o -> {

            selCBItem = !inRasCB.getSelectionModel().isEmpty();
            inRasTF.setDisable(selCBItem);
            inRasBtn.setDisable(selCBItem);
        });

        inRasBtn.setOnAction(e -> {

            try {

                FileChooser fC = new FileChooser();
                File rF = fC.showOpenDialog(wStage);
                if (rF != null) inRasTF.setText(rF.getAbsolutePath());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        inShpBtn.setOnAction(e -> {

            try {

                FileChooser fC = new FileChooser();
                File sF = fC.showOpenDialog(wStage);
                if (sF != null) inShpTF.setText(sF.getAbsolutePath());
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        });

        outRasBtn.setOnAction(e -> {

            try {

                FileChooser fC = new FileChooser();
                File rF = fC.showSaveDialog(wStage);
                if (rF != null) outRasTF.setText(rF.getAbsolutePath());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        applyBtn.setOnAction(e -> runWSTool());
    }

    private void runWSTool() {

        finishedLB.setVisible(false); unsuccessfulLB.setVisible(false);

        if ((selCBItem || !inRasTF.getText().isBlank())  && !outRasTF.getText().isBlank() &&
             !inShpTF.getText().isBlank()) {

            String inPath = System.getProperty("user.dir") + "\\src\\main\\resources\\raster\\rasterCache\\";
            String inShpPath = inShpTF.getText();
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

                            if (RunWBT.runScript(" -r=\"Watershed\"", " --d8_pntr=\"" + finalInPath + "\"",
                                                 " --pour_pts=\"" + inShpPath + "\"", " -o=\"" + outPath + "\"",
                                                 "", "", "")) {

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