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

public class RasterToVectorPolygons {

    @FXML private Label finishedLB, unsuccessfulLB;
    @FXML private ComboBox<String> inRasCB;
    @FXML private TextField inRasTF, outVecTF;
    @FXML private ProgressIndicator progressInd;
    @FXML private Button inRasBtn, outVecBtn, applyBtn;

    boolean selCBItem = false;

    public RasterToVectorPolygons() throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(RasterToVectorPolygons.class.getResource("fxml/ras_vec_polygons.fxml"));
        fxmlLoader.setController(this);

        Stage rtvpStage = new Stage();
        rtvpStage.setTitle("Raster To Vector Polygon");
        rtvpStage.setScene(new Scene(fxmlLoader.load()));
        rtvpStage.setResizable(false);
        rtvpStage.initOwner(MainApp.mainStage);
        rtvpStage.initModality(Modality.APPLICATION_MODAL);
        rtvpStage.show();

        inRasCB.getItems().setAll(FXCollections.observableArrayList(RasterManager.rasterNameList));
        inRasCB.getSelectionModel().selectedItemProperty().addListener(o -> {

            selCBItem = !inRasCB.getSelectionModel().isEmpty();
            inRasTF.setDisable(selCBItem);
            inRasBtn.setDisable(selCBItem);
        });

        inRasBtn.setOnAction(e -> {

            try {

                FileChooser fC = new FileChooser();
                File rasF = fC.showOpenDialog(rtvpStage);
                if (rasF != null) inRasTF.setText(rasF.getAbsolutePath());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        outVecBtn.setOnAction(e -> {

            try {

                FileChooser fC = new FileChooser();
                File vF = fC.showSaveDialog(rtvpStage);
                if (vF != null) outVecTF.setText(vF.getAbsolutePath());
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        });

        applyBtn.setOnAction(e -> runRasToVecLinesTool());
    }

    private void runRasToVecLinesTool() {

        finishedLB.setVisible(false); unsuccessfulLB.setVisible(false);

        if ((selCBItem || !inRasTF.getText().isBlank()) && !outVecTF.getText().isBlank()) {

            String inPath = System.getProperty("user.dir") + "\\src\\main\\resources\\raster\\rasterCache\\";

            if (selCBItem) inPath = inPath + inRasCB.getSelectionModel().getSelectedItem();
            else inPath = inRasTF.getText();

            String finalInPath = inPath;

            Service<String> service = new Service<>() {

                @Override
                protected Task<String> createTask() {

                    return new Task<>() {

                        @Override
                        protected String call() {

                            if (RunWBT.runScript(" -r=\"RasterToVectorPolygons\"", " -i=\"" + finalInPath + "\"",
                                                 " -o=\"" + outVecTF.getText() + "\"", "", "", "", "")) {

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