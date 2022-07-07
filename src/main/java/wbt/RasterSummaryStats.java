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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class RasterSummaryStats {

    @FXML private Label finishedLB, unsuccessfulLB;
    @FXML private ComboBox<String> inRasCB;
    @FXML private TextField inRasTF;
    @FXML private TextArea rasterSSTA;
    @FXML private ProgressIndicator progressInd;
    @FXML private Button inRasBtn, applyBtn;

    boolean selCBItem = false;

    public RasterSummaryStats() throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(RasterSummaryStats.class.getResource("fxml/raster_ss.fxml"));
        fxmlLoader.setController(this);

        Stage rssStage = new Stage();
        rssStage.setTitle("Raster Summary Stats");
        rssStage.setScene(new Scene(fxmlLoader.load()));
        rssStage.setResizable(false);
        rssStage.initOwner(MainApp.mainStage);
        rssStage.initModality(Modality.APPLICATION_MODAL);
        rssStage.show();

        inRasCB.getItems().setAll(FXCollections.observableArrayList(RasterManager.rasterNameList));
        inRasCB.getSelectionModel().selectedItemProperty().addListener(o -> {

            selCBItem = !inRasCB.getSelectionModel().isEmpty();
            inRasTF.setDisable(selCBItem);
            inRasBtn.setDisable(selCBItem);
        });

        inRasBtn.setOnAction(e -> {

            try {

                FileChooser fC = new FileChooser();
                File rF = fC.showOpenDialog(rssStage);
                if (rF != null) inRasTF.setText(rF.getAbsolutePath());
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        });

        applyBtn.setOnAction(e -> runRSSTool());
    }

    private void runRSSTool() {

        finishedLB.setVisible(false); unsuccessfulLB.setVisible(false);

        if (selCBItem || !inRasTF.getText().isBlank()) {

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

                            try {

                                String args = "src/main/resources/wbt/executable/whitebox_tools.exe" +
                                              " -r=\"RasterSummaryStats\"" + " -i=\"" + finalInPath + "\"";
                                Process process = Runtime.getRuntime().exec(args);
                                BufferedReader reader = new BufferedReader(new InputStreamReader(process.
                                                                                                 getInputStream()));
                                String line; StringBuilder rssSB = new StringBuilder();

                                while ((line = reader.readLine()) != null) rssSB.append(line).append("\n");

                                rasterSSTA.setText(rssSB.toString());

                                process.destroy();

                                finishedLB.setVisible(process.exitValue() == 0);
                                unsuccessfulLB.setVisible(!finishedLB.isVisible());

                            } catch (IOException e) {
                                e.printStackTrace();
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