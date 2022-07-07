package mainModule;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import raster.renderer.*;

import vector.VectorRendererController;

import java.io.IOException;

public class RendererBoxController {

    @FXML private Button blendBtn; @FXML private Button hillshadeBtn;
    @FXML private Button rgbBtn; @FXML private Button colorMapBtn;
    @FXML private Button stretchBtn; @FXML private Button vectorBtn;

    ImageView brB = new ImageView(new Image("raster/renderer/images/blend_renderer.png"));
    ImageView hsrB = new ImageView(new Image("raster/renderer/images/hillshade_renderer.png"));
    ImageView rgbrB = new ImageView(new Image("raster/renderer/images/rgb_renderer.png"));
    ImageView srB = new ImageView(new Image("raster/renderer/images/stretch_renderer.png"));
    ImageView cmrB = new ImageView(new Image("raster/renderer/images/color_map_renderer.png"));
    ImageView vrB = new ImageView(new Image("vector/renderer/images/vector_renderer.png"));

    public void initialize() {

        blendBtn.setGraphic(brB); hillshadeBtn.setGraphic(hsrB); rgbBtn.setGraphic(rgbrB);
        stretchBtn.setGraphic(srB); colorMapBtn.setGraphic(cmrB); vectorBtn.setGraphic(vrB);

        blendBtn.setOnAction(e -> {

            try {
                new BlendRendererController();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        hillshadeBtn.setOnAction(e -> {

            try {
                new HillshadeRendererController();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        rgbBtn.setOnAction(e -> {

            try {
                new RGBRendererController();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        stretchBtn.setOnAction(e -> {

            try {
                new StretchRendererController();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        colorMapBtn.setOnAction(e -> {

            try {
                new ColorMapRendererController();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        vectorBtn.setOnAction(e -> {

            try {
                new VectorRendererController();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }
}