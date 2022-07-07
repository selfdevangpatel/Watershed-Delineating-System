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

public class Reclass {

    @FXML private Label finishedLB, unsuccessfulLB;
    @FXML private ComboBox<String> inRasCB;
    @FXML private TextField inRasTF, outRasTF, c1nvTF, c2nvTF, c1fvTF, c2fvTF, c1tltvTF, c2tltvTF;
    @FXML private CheckBox addToMapCBX;
    @FXML private ProgressIndicator progressInd;
    @FXML private Button inRasBtn, outRasBtn, applyBtn;

    boolean selCBItem = false;

    public Reclass() throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(Reclass.class.getResource("fxml/reclass.fxml"));
        fxmlLoader.setController(this);

        Stage rStage = new Stage();
        rStage.setTitle("Reclass");
        rStage.setScene(new Scene(fxmlLoader.load()));
        rStage.setResizable(false);
        rStage.initOwner(MainApp.mainStage);
        rStage.initModality(Modality.APPLICATION_MODAL);
        rStage.show();

        inRasCB.getItems().setAll(FXCollections.observableArrayList(RasterManager.rasterNameList));
        inRasCB.getSelectionModel().selectedItemProperty().addListener(o -> {

            selCBItem = !inRasCB.getSelectionModel().isEmpty();
            inRasTF.setDisable(selCBItem);
            inRasBtn.setDisable(selCBItem);
        });

        inRasBtn.setOnAction(e -> {

            try {

                FileChooser fC = new FileChooser();
                File rF = fC.showOpenDialog(rStage);
                if (rF != null) inRasTF.setText(rF.getAbsolutePath());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        outRasBtn.setOnAction(e -> {

            try {

                FileChooser fC = new FileChooser();
                File rF = fC.showSaveDialog(rStage);
                if (rF != null) outRasTF.setText(rF.getAbsolutePath());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        applyBtn.setOnAction(e -> runReclassTool());
    }

    private void runReclassTool() {

        finishedLB.setVisible(false); unsuccessfulLB.setVisible(false);

        if ((selCBItem || !inRasTF.getText().isBlank())  && !outRasTF.getText().isBlank()) {

            String inPath = System.getProperty("user.dir") + "\\src\\main\\resources\\raster\\rasterCache\\";
            String outPath = outRasTF.getText();

            if (selCBItem) inPath = inPath + inRasCB.getSelectionModel().getSelectedItem();
            else inPath = inRasTF.getText();

            double c1nv = Double.parseDouble(c1nvTF.getText()), c2nv = Double.parseDouble(c2nvTF.getText());
            double c1fv = Double.parseDouble(c1fvTF.getText()), c2fv = Double.parseDouble(c2fvTF.getText());
            double c1tlt = Double.parseDouble(c1tltvTF.getText()), c2tlt = Double.parseDouble(c2tltvTF.getText());

            String rcValStr = c1nv + ";" + c1fv + ";" + c1tlt + ";" + c2nv + ";" + c2fv + ";" + c2tlt;

            String finalInPath = inPath;
            Service<String> service = new Service<>() {

                @Override
                protected Task<String> createTask() {

                    return new Task<>() {

                        @Override
                        protected String call() {

                            if (RunWBT.runScript(" -r=\"Reclass\"", " -i=\"" + finalInPath + "\"", " -o=\"" + outPath +
                                                 "\"", " --reclass_vals=\"" + rcValStr + "\"", "", "", "")) {

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