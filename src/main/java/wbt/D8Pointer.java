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

public class D8Pointer {

    @FXML private Label finishedLB, unsuccessfulLB;
    @FXML private ComboBox<String> inRasCB;
    @FXML private TextField inRasTF, outRasTF;
    @FXML private CheckBox esCBX, addToMapCBX;
    @FXML private ProgressIndicator progressInd;
    @FXML private Button inRasBtn, outRasBtn, applyBtn;

    boolean selCBItem = false;

    public D8Pointer() throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(D8Pointer.class.getResource("fxml/d8_pointer.fxml"));
        fxmlLoader.setController(this);

        Stage d8pStage = new Stage();
        d8pStage.setTitle("D8 Pointer");
        d8pStage.setScene(new Scene(fxmlLoader.load()));
        d8pStage.setResizable(false);
        d8pStage.initOwner(MainApp.mainStage);
        d8pStage.initModality(Modality.APPLICATION_MODAL);
        d8pStage.show();

        inRasCB.getItems().setAll(FXCollections.observableArrayList(RasterManager.rasterNameList));
        inRasCB.getSelectionModel().selectedItemProperty().addListener(o -> {

            selCBItem = !inRasCB.getSelectionModel().isEmpty();
            inRasTF.setDisable(selCBItem);
            inRasBtn.setDisable(selCBItem);
        });

        inRasBtn.setOnAction(e -> {

            try {

                FileChooser fC = new FileChooser();
                File rF = fC.showOpenDialog(d8pStage);
                if (rF != null) inRasTF.setText(rF.getAbsolutePath());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        outRasBtn.setOnAction(e -> {

            try {

                FileChooser fC = new FileChooser();
                File rF = fC.showSaveDialog(d8pStage);
                if (rF != null) outRasTF.setText(rF.getAbsolutePath());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        applyBtn.setOnAction(e -> runD8PTool());
    }

    private void runD8PTool() {

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

                            if (RunWBT.runScript(" -r=\"D8Pointer\"", " -i=\"" + finalInPath + "\"", " -o=\"" + outPath
                                                 + "\"", esCBX.isSelected() ? " --esri_pntr" : "", "", "", "")) {

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

                if (addToMapCBX.isSelected() && finishedLB.isVisible()) {

                    RasterManager.d8Pointer = true;
                    RasterManager.addRasterLayer(outPath);
                }
            });
        }
    }
}