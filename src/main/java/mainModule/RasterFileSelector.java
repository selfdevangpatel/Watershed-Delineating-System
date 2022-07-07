package mainModule;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import raster.RasterManager;

import java.io.File;
import java.io.IOException;

public class RasterFileSelector {

    @FXML private TextField rasFilePathTF;
    @FXML private Button rasFileSelBtn, rasAddBtn;
    @FXML private Label successLB, unsuccessfulLB;

    RasterFileSelector() throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(RasterFileSelector.class.getResource("fxml/raster_file_select.fxml"));
        fxmlLoader.setController(this);

        Stage rfsStage = new Stage();
        rfsStage.setTitle("Select Raster File");
        rfsStage.setScene(new Scene(fxmlLoader.load()));
        rfsStage.setResizable(false);
        rfsStage.initOwner(MainApp.mainStage);
        rfsStage.initModality(Modality.APPLICATION_MODAL);
        rfsStage.show();

        final File[] rF = new File[1];

        rasAddBtn.setDisable(true);
        rasFilePathTF.textProperty().addListener(o -> rasAddBtn.setDisable(rasFilePathTF.getText().isBlank()));

        rasFileSelBtn.setOnAction(e -> {

            FileChooser fC = new FileChooser();
            rF[0] = fC.showOpenDialog(rfsStage);
            if (rF[0] != null) rasFilePathTF.setText(rF[0].getAbsolutePath());
        });

        rasAddBtn.setOnAction(e -> RasterManager.addRasterLayer(rasFilePathTF.getText()));
    }
}