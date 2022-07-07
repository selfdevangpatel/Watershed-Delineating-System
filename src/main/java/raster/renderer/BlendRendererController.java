package raster.renderer;

import com.esri.arcgisruntime.raster.ColorRamp;
import com.esri.arcgisruntime.raster.SlopeType;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import mainModule.MainApp;

import raster.RasterManager;

import java.io.IOException;

public class BlendRendererController {

    @FXML private ComboBox<SlopeType> slopeTypeCB;
    @FXML private ComboBox<ColorRamp.PresetType> colorRampCB;
    @FXML private ComboBox<String> rasSelCB;
    @FXML private Slider azimuthSL;
    @FXML private Slider altitudeSL;
    @FXML private Button applyBtn;

    public BlendRendererController() throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(BlendRendererController.class.getResource("fxml/blend_renderer.fxml"));
        fxmlLoader.setController(this);

        Stage brStage = new Stage();
        brStage.setTitle("Blend Renderer");
        brStage.setScene(new Scene(fxmlLoader.load()));
        brStage.setResizable(false);
        brStage.initOwner(MainApp.mainStage);
        brStage.initModality(Modality.APPLICATION_MODAL);
        brStage.getIcons().add(new Image(String.valueOf(BlendRendererController.class.
                               getResource("images/blend_renderer.png"))));
        brStage.show();

        slopeTypeCB.getItems().setAll(SlopeType.values());
        slopeTypeCB.getSelectionModel().select(SlopeType.NONE);
        colorRampCB.getItems().setAll(ColorRamp.PresetType.values());
        colorRampCB.getSelectionModel().select(ColorRamp.PresetType.NONE);
        rasSelCB.getItems().setAll(RasterManager.rasterNameList);

        if (RasterManager.rlLL.isEmpty()) {

            rasSelCB.setPromptText("--Empty--");
            applyBtn.setDisable(true);
        } else {

            applyBtn.setDisable(false);
            rasSelCB.getSelectionModel().selectFirst();
        }

        applyBtn.setOnAction(e -> RasterRenderer.blendRenderer(rasSelCB.getSelectionModel().getSelectedItem(),
                                                               altitudeSL.getValue(), azimuthSL.getValue(),
                                                               slopeTypeCB.getSelectionModel().getSelectedItem(),
                                                               rasSelCB.getSelectionModel().getSelectedIndex(),
                                                               colorRampCB.getSelectionModel().getSelectedItem(),
                                                               false));
    }
}