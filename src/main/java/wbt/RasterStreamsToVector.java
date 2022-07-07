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
import java.nio.file.Files;
import java.nio.file.Path;

public class RasterStreamsToVector {

    @FXML private Label finishedLB, unsuccessfulLB;
    @FXML private ComboBox<String> inRasStreamsCB, inRasD8PCB;
    @FXML private TextField inRasStreamsTF, inRasD8PTF, outVecTF;
    @FXML private ProgressIndicator progressInd;
    @FXML private RadioButton essRB;
    @FXML private Button inRasStreamsBtn, inRasD8PBtn, outVecBtn, applyBtn;

    boolean selStreamsCBItem = false, selD8PCBItem = false;

    public RasterStreamsToVector() throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(RasterStreamsToVector.class.getResource("fxml/ras_streams_vec.fxml"));
        fxmlLoader.setController(this);

        Stage rstvStage = new Stage();
        rstvStage.setTitle("Raster Streams To Vector");
        rstvStage.setScene(new Scene(fxmlLoader.load()));
        rstvStage.setResizable(false);
        rstvStage.initOwner(MainApp.mainStage);
        rstvStage.initModality(Modality.APPLICATION_MODAL);
        rstvStage.show();

        inRasStreamsCB.getItems().setAll(FXCollections.observableArrayList(RasterManager.rasterNameList));
        inRasStreamsCB.getSelectionModel().selectedItemProperty().addListener(o -> {

            selStreamsCBItem = !inRasStreamsCB.getSelectionModel().isEmpty();
            inRasStreamsTF.setDisable(selStreamsCBItem);
            inRasStreamsBtn.setDisable(selStreamsCBItem);
        });

        inRasD8PCB.getItems().setAll(FXCollections.observableArrayList(RasterManager.rasterNameList));
        inRasD8PCB.getSelectionModel().selectedItemProperty().addListener(o -> {

            selD8PCBItem = !inRasD8PCB.getSelectionModel().isEmpty();
            inRasD8PTF.setDisable(selD8PCBItem);
            inRasD8PBtn.setDisable(selD8PCBItem);
        });

        inRasStreamsBtn.setOnAction(e -> {

            try {

                FileChooser fC = new FileChooser();
                File rasF = fC.showOpenDialog(rstvStage);
                if (rasF != null) inRasStreamsTF.setText(rasF.getAbsolutePath());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        inRasD8PBtn.setOnAction(e -> {

            try {

                FileChooser fC = new FileChooser();
                File rasF = fC.showOpenDialog(rstvStage);
                if (rasF != null) inRasD8PTF.setText(rasF.getAbsolutePath());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        outVecBtn.setOnAction(e -> {

            try {

                FileChooser fC = new FileChooser();
                File vF = fC.showSaveDialog(rstvStage);
                if (vF != null) outVecTF.setText(vF.getAbsolutePath());
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        });

        applyBtn.setOnAction(e -> runRasStreamToVecTool());
    }

    private void runRasStreamToVecTool() {

        finishedLB.setVisible(false); unsuccessfulLB.setVisible(false);

        if ((selStreamsCBItem || !inRasStreamsTF.getText().isBlank()) &&
            (selD8PCBItem || !inRasD8PTF.getText().isBlank()) && !outVecTF.getText().isBlank()) {

            String inPath = System.getProperty("user.dir") + "\\src\\main\\resources\\raster\\rasterCache\\";
            String inRasStreamPath, inRasD8PPath;

            if (selStreamsCBItem) inRasStreamPath = inPath + inRasStreamsCB.getSelectionModel().getSelectedItem();
            else inRasStreamPath = inRasStreamsTF.getText();

            if (selD8PCBItem) inRasD8PPath = inPath + inRasD8PCB.getSelectionModel().getSelectedItem();
            else inRasD8PPath = inRasD8PTF.getText();

            String outPath = outVecTF.getText();

            Service<String> service = new Service<>() {

                @Override
                protected Task<String> createTask() {

                    return new Task<>() {

                        @Override
                        protected String call() throws IOException {

                            if (RunWBT.runScript(" -r=\"RasterStreamsToVector\"", " --streams=\"" + inRasStreamPath +
                                                 "\"", " --d8_pntr=\"" + inRasD8PPath + "\"", " -o=\"" +
                                                 outPath + "\"", essRB.isSelected() ? " --esri_pntr" : "",
                                                 "", "")) {

                                finishedLB.setVisible(true); unsuccessfulLB.setVisible(false);

                                // Create a projection file so that it can be properly loaded onto the map.
                                String prjStr = "GEOGCS[\"GCS_WGS_1984\",DATUM[\"D_WGS_1984\",SPHEROID[\"WGS_1984\"," +
                                                "6378137.0,298.257223563]],PRIMEM[\"Greenwich\",0.0]," +
                                                "UNIT[\"Degree\",0.0174532925199433],AUTHORITY[\"EPSG\",4326]]";
                                Files.writeString(Path.of(outPath.replace(".shp", ".prj")), prjStr);
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