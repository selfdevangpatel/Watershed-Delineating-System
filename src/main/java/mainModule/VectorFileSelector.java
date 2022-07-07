package mainModule;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import vector.VectorManager;

import java.io.File;
import java.io.IOException;

public class VectorFileSelector {

    @FXML private TextField vecFilePathTF;
    @FXML private Button vecFileSelBtn, vecAddBtn;

    VectorFileSelector() throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(VectorFileSelector.class.getResource("fxml/vector_file_select.fxml"));
        fxmlLoader.setController(this);

        Stage vfsStage = new Stage();
        vfsStage.setTitle("Select Vector File");
        vfsStage.setScene(new Scene(fxmlLoader.load()));
        vfsStage.setResizable(false);
        vfsStage.initOwner(MainApp.mainStage);
        vfsStage.initModality(Modality.APPLICATION_MODAL);
        vfsStage.show();

        final File[] vF = new File[1];

        vecAddBtn.setDisable(true);
        vecFilePathTF.textProperty().addListener(o -> vecAddBtn.setDisable(vecFilePathTF.getText().isBlank()));

        vecFileSelBtn.setOnAction(e -> {

            FileChooser fC = new FileChooser();
            vF[0] = fC.showOpenDialog(vfsStage);
            if (vF[0] != null) vecFilePathTF.setText(vF[0].getAbsolutePath());
        });

        vecAddBtn.setOnAction(e -> VectorManager.addVectorLayer(vecFilePathTF.getText()));
    }
}