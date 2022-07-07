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

public class FillDepressions {

    @FXML private Label finishedLB, unsuccessfulLB;
    @FXML private ComboBox<String> inRasCB;
    @FXML private TextField inRasTF, outRasTF;
    @FXML private CheckBox ffCBX, fiCBX, mdCBX, addToMapCBX;
    @FXML private Spinner<Double> fiSP, mdSP;
    @FXML private ProgressIndicator progressInd;
    @FXML private Button inRasBtn, outRasBtn, applyBtn;

    boolean selCBItem = false;

    public FillDepressions() throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(FillDepressions.class.getResource("fxml/fill_depressions.fxml"));
        fxmlLoader.setController(this);

        Stage fdStage = new Stage();
        fdStage.setTitle("Fill Depressions");
        fdStage.setScene(new Scene(fxmlLoader.load()));
        fdStage.setResizable(false);
        fdStage.initOwner(MainApp.mainStage);
        fdStage.initModality(Modality.APPLICATION_MODAL);
        fdStage.show();

        inRasCB.getItems().setAll(FXCollections.observableArrayList(RasterManager.rasterNameList));
        inRasCB.getSelectionModel().selectedItemProperty().addListener(o -> {

            selCBItem = !inRasCB.getSelectionModel().isEmpty();
            inRasTF.setDisable(selCBItem);
            inRasBtn.setDisable(selCBItem);
        });

        inRasBtn.setOnAction(e -> {

            try {

                FileChooser fC = new FileChooser();
                File rF = fC.showOpenDialog(fdStage);
                if (rF != null) inRasTF.setText(rF.getAbsolutePath());
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        });

        outRasBtn.setOnAction(e -> {

            try {

                FileChooser fC = new FileChooser();
                File rF = fC.showSaveDialog(fdStage);
                if (rF != null) outRasTF.setText(rF.getAbsolutePath());
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        });

        fiSP.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 1000000.0, 0.0));
        mdSP.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 1000000.0, 0.0));

        applyBtn.setOnAction(e -> runFDTool());
    }

    private void runFDTool() {

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

                            if (RunWBT.runScript(" -r=\"FillDepressions\"", " -i=\"" + finalInPath + "\"", " -o=\"" +
                                                 outPath + "\"", ffCBX.isSelected() ? " --fix_flats" : "",
                                                 fiCBX.isSelected() ? " --flat_increment=" + fiSP.getValue() : "",
                                                 mdCBX.isSelected() ? " --max_depth=" + mdSP.getValue() : "", "")) {

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