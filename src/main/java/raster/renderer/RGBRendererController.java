package raster.renderer;

import com.esri.arcgisruntime.raster.MinMaxStretchParameters;
import com.esri.arcgisruntime.raster.PercentClipStretchParameters;
import com.esri.arcgisruntime.raster.StandardDeviationStretchParameters;
import com.esri.arcgisruntime.raster.StretchParameters;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import mainModule.MainApp;

import raster.RasterManager;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class RGBRendererController {

    @FXML private AnchorPane minMaxAP, percentClipAP, stdDeviationAP;
    @FXML private ComboBox<String> stretchTypeCB;
    @FXML private ComboBox<String> rasSelCB;
    @FXML private Label redLB, greenLB, blueLB, minMaxLB, factorLB;
    @FXML private Spinner<Integer> redMinSP, redMaxSP, greenMinSP, greenMaxSP, blueMinSP, blueMaxSP;
    @FXML private Spinner<Integer> mM1SP, mM2SP, factorSP;
    @FXML private Button applyBtn;

    StretchParameters stretchParameters;

    public RGBRendererController() throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(RGBRendererController.class.getResource("fxml/rgb_renderer.fxml"));
        fxmlLoader.setController(this);

        Stage rgbrStage = new Stage();
        rgbrStage.setTitle("RGB Renderer");
        rgbrStage.setScene(new Scene(fxmlLoader.load()));
        rgbrStage.setResizable(false);
        rgbrStage.initOwner(MainApp.mainStage);
        rgbrStage.initModality(Modality.APPLICATION_MODAL);
        rgbrStage.getIcons().add(new Image(String.valueOf(BlendRendererController.class.
                                 getResource("images/rgb_renderer.png"))));
        rgbrStage.show();

        rasSelCB.getItems().setAll(RasterManager.rasterNameList);

        stretchTypeCB.getItems().addAll("Min Max", "Percent Clip", "Std Deviation");
        stretchTypeCB.getSelectionModel().select(0);

        if (RasterManager.rlLL.isEmpty()) {

            rasSelCB.setPromptText("--Empty--");
            applyBtn.setDisable(true);
        } else {

            applyBtn.setDisable(false);
            rasSelCB.getSelectionModel().selectFirst();
        }

        redMinSP.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 255, 0));
        redMaxSP.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 255, 255));
        greenMinSP.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 255, 0));
        greenMaxSP.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 255, 255));
        blueMinSP.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 255, 0));
        blueMaxSP.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 255, 255));
        mM1SP.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 99, 1));
        mM2SP.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 99, 99));
        factorSP.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 3, 1));

        mM1SP.valueProperty().addListener(e -> {
            if (mM1SP.getValue() + mM2SP.getValue() > 100) mM2SP.getValueFactory().setValue(100 - mM1SP.getValue());
        });

        mM2SP.valueProperty().addListener(e -> {
            if (mM1SP.getValue() + mM2SP.getValue() > 100) mM1SP.getValueFactory().setValue(100 - mM2SP.getValue());
        });

        stretchTypeCB.getSelectionModel().selectedItemProperty().addListener(o -> {

            switch (stretchTypeCB.getSelectionModel().getSelectedItem()) {

                case "Min Max" -> {

                    minMaxAP.setVisible(true);
                    percentClipAP.setVisible(false);
                    stdDeviationAP.setVisible(false);
                }
                case "Percent Clip" -> {

                    minMaxAP.setVisible(false);
                    percentClipAP.setVisible(true);
                    stdDeviationAP.setVisible(false);
                }
                default -> {

                    minMaxAP.setVisible(false);
                    percentClipAP.setVisible(false);
                    stdDeviationAP.setVisible(true);
                }
            }
        });

        applyBtn.setOnAction(e -> {

            switch (stretchTypeCB.getSelectionModel().getSelectedItem()) {

                case "Min Max" -> {

                    List<Double> minValues = Arrays.asList(redMinSP.getValue().doubleValue(),
                                             greenMinSP.getValue().doubleValue(),
                                             blueMinSP.getValue().doubleValue());
                    List<Double> maxValues = Arrays.asList(redMaxSP.getValue().doubleValue(),
                                             greenMaxSP.getValue().doubleValue(),
                                             blueMaxSP.getValue().doubleValue());
                    stretchParameters = new MinMaxStretchParameters(minValues, maxValues);
                }

                case "Percent Clip" -> stretchParameters = new PercentClipStretchParameters(mM1SP.getValue(),
                                                                                            mM2SP.getValue());

                default -> stretchParameters = new StandardDeviationStretchParameters(factorSP.getValue());
            }

            RasterRenderer.rgbRenderer(stretchParameters, rasSelCB.getSelectionModel().getSelectedIndex(), false);
        });
    }
}