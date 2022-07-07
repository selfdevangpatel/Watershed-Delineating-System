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

public class D8FlowAccumulation {

    @FXML private Label finishedLB, unsuccessfulLB;
    @FXML private ComboBox<String> inRasCB;
    @FXML private TextField inRasTF, outRasTF;
    @FXML private RadioButton ot_cellRB, ot_caRB;
    @FXML private CheckBox ltoCBX, ird8pCBX, essCBX, addToMapCBX;
    @FXML private ProgressIndicator progressInd;
    @FXML private Button inRasBtn, outRasBtn, applyBtn;

    boolean selCBItem = false;

    public D8FlowAccumulation() throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(D8FlowAccumulation.class.getResource("fxml/d8_flow_accu.fxml"));
        fxmlLoader.setController(this);

        Stage d8faStage = new Stage();
        d8faStage.setTitle("D8 Flow Accumulation");
        d8faStage.setScene(new Scene(fxmlLoader.load()));
        d8faStage.setResizable(false);
        d8faStage.initOwner(MainApp.mainStage);
        d8faStage.initModality(Modality.APPLICATION_MODAL);
        d8faStage.show();

        inRasCB.getItems().setAll(FXCollections.observableArrayList(RasterManager.rasterNameList));
        inRasCB.getSelectionModel().selectedItemProperty().addListener(o -> {

            selCBItem = !inRasCB.getSelectionModel().isEmpty();
            inRasTF.setDisable(selCBItem);
            inRasBtn.setDisable(selCBItem);
        });

        inRasBtn.setOnAction(e -> {

            try {

                FileChooser fC = new FileChooser();
                File rF = fC.showOpenDialog(d8faStage);
                if (rF != null) inRasTF.setText(rF.getAbsolutePath());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        outRasBtn.setOnAction(e -> {

            try {

                FileChooser fC = new FileChooser();
                File rF = fC.showSaveDialog(d8faStage);
                if (rF != null) outRasTF.setText(rF.getAbsolutePath());
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        });

        applyBtn.setOnAction(e -> runD8FATool());
    }

    private void runD8FATool() {

        finishedLB.setVisible(false); unsuccessfulLB.setVisible(false);

        if ((selCBItem || !inRasTF.getText().isBlank())  && !outRasTF.getText().isBlank()) {

            String inPath = System.getProperty("user.dir") + "\\src\\main\\resources\\raster\\rasterCache\\";
            String outPath = outRasTF.getText();

            if (selCBItem) inPath = inPath + inRasCB.getSelectionModel().getSelectedItem();
            else inPath = inRasTF.getText();

            String selCBX;
            if (ot_cellRB.isSelected()) selCBX = "cells\"";
            else if (ot_caRB.isSelected()) selCBX = "catchment area\"";
            else selCBX = "specific contributing area\"";

            String finalInPath = inPath;
            String finalSelCBX = selCBX;
            Service<String> service = new Service<>() {

                @Override
                protected Task<String> createTask() {

                    return new Task<>() {

                        @Override
                        protected String call() {

                            if (RunWBT.runScript(" -r=\"D8FlowAccumulation\"", " -i=\"" +finalInPath + "\"", " -o=\"" +
                                                 outPath + "\"", " --out_type=\"" + finalSelCBX, ltoCBX.isSelected() ?
                                                 " --log" : "", ird8pCBX.isSelected() ? " --pntr" : "",
                                                 essCBX.isSelected() ? " --esri_pntr" : "")) {

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