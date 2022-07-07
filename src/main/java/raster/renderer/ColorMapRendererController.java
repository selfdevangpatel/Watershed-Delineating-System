package raster.renderer;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import mainModule.MainApp;

import raster.RasterManager;

import java.io.IOException;

public class ColorMapRendererController {

    @FXML private ComboBox<String> rasSelCB;
    @FXML private RadioButton d8pntrRB;
    @FXML private Button applyBtn;

    public ColorMapRendererController() throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(ColorMapRendererController.class.
                                               getResource("fxml/color_map_renderer.fxml"));
        fxmlLoader.setController(this);

        Stage cmrStage = new Stage();
        cmrStage.setTitle("Color Map Renderer");
        cmrStage.setScene(new Scene(fxmlLoader.load()));
        cmrStage.setResizable(false);
        cmrStage.initOwner(MainApp.mainStage);
        cmrStage.initModality(Modality.APPLICATION_MODAL);
        cmrStage.getIcons().add(new Image(String.valueOf(BlendRendererController.class.
                                getResource("images/color_map_renderer.png"))));
        cmrStage.show();

        rasSelCB.getItems().setAll(RasterManager.rasterNameList);

        if (RasterManager.rlLL.isEmpty()) {

            rasSelCB.setPromptText("--Empty--");
            applyBtn.setDisable(true);
        } else {

            applyBtn.setDisable(false);
            rasSelCB.getSelectionModel().selectFirst();
        }

        applyBtn.setOnAction(e -> {

            String selCMType = d8pntrRB.isSelected() ? d8pntrRB.getText() : "";

            RasterRenderer.colormapRenderer(selCMType, rasSelCB.getSelectionModel().getSelectedIndex(), false);
        });
    }
}