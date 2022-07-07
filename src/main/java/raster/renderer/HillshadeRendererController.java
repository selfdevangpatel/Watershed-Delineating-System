package raster.renderer;

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

public class HillshadeRendererController {

    @FXML private ComboBox<SlopeType> slopeTypeCB;
    @FXML private ComboBox<String> rasSelCB;
    @FXML private Slider azimuthSL;
    @FXML private Slider altitudeSL;
    @FXML private Button applyBtn;

    public HillshadeRendererController() throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(HillshadeRendererController.class.
                                               getResource("fxml/hillshade_renderer.fxml"));
        fxmlLoader.setController(this);

        Stage hsrStage = new Stage();
        hsrStage.setTitle("Hillshade Renderer");
        hsrStage.setScene(new Scene(fxmlLoader.load()));
        hsrStage.setResizable(false);
        hsrStage.initOwner(MainApp.mainStage);
        hsrStage.initModality(Modality.APPLICATION_MODAL);
        hsrStage.getIcons().add(new Image(String.valueOf(BlendRendererController.class.
                                getResource("images/hillshade_renderer.png"))));
        hsrStage.show();

        slopeTypeCB.getItems().setAll(SlopeType.values());
        slopeTypeCB.getSelectionModel().select(SlopeType.NONE);
        rasSelCB.getItems().setAll(RasterManager.rasterNameList);

        if (RasterManager.rlLL.isEmpty()) {

            rasSelCB.setPromptText("--Empty--");
            applyBtn.setDisable(true);
        } else {

            applyBtn.setDisable(false);
            rasSelCB.getSelectionModel().selectFirst();
        }

        applyBtn.setOnAction(e -> RasterRenderer.hillshadeRenderer(altitudeSL.getValue(), azimuthSL.getValue(),
                                                                   slopeTypeCB.getSelectionModel().getSelectedItem(),
                                                                   rasSelCB.getSelectionModel().getSelectedIndex(),
                                                                   false));
    }
}