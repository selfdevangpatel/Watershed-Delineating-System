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
import java.util.Collections;

public class StretchRendererController {

    @FXML private AnchorPane minMaxAP, percentClipAP, stdDeviationAP;
    @FXML private ComboBox<String> stretchTypeCB;
    @FXML private ComboBox<String> rasSelCB;
    @FXML private Label minLB, maxLB, minMaxLB, factorLB;
    @FXML private Spinner<Integer> minSP, maxSP;
    @FXML private Spinner<Integer> mM1SP, mM2SP, factorSP;
    @FXML private Button applyBtn;

    StretchParameters stretchParameters;

    public StretchRendererController() throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(StretchRendererController.class.
                                               getResource("fxml/stretch_renderer.fxml"));
        fxmlLoader.setController(this);

        Stage srStage = new Stage();
        srStage.setTitle("Stretch Renderer");
        srStage.setScene(new Scene(fxmlLoader.load()));
        srStage.setResizable(false);
        srStage.initOwner(MainApp.mainStage);
        srStage.initModality(Modality.APPLICATION_MODAL);
        srStage.getIcons().add(new Image(String.valueOf(BlendRendererController.class.
                               getResource("images/stretch_renderer.png"))));
        srStage.show();

        rasSelCB.getItems().setAll(RasterManager.rasterNameList);

        if (RasterManager.rlLL.isEmpty()) {

            rasSelCB.setPromptText("--Empty--");
            applyBtn.setDisable(true);
        } else {

            applyBtn.setDisable(false);
            rasSelCB.getSelectionModel().selectFirst();
        }

        stretchTypeCB.getItems().addAll("Min Max", "Percent Clip", "Std Deviation");
        stretchTypeCB.getSelectionModel().select(0);

        minSP.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100000, 0));
        maxSP.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100000, 5000));
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

                case "Min Max" -> stretchParameters = new MinMaxStretchParameters(
                                                      Collections.singletonList(minSP.getValue().doubleValue()),
                                                      Collections.singletonList(maxSP.getValue().doubleValue()));

                case "Percent Clip" -> stretchParameters = new PercentClipStretchParameters(mM1SP.getValue(),
                                                           mM2SP.getValue());

                default -> stretchParameters = new StandardDeviationStretchParameters(factorSP.getValue());
            }

            RasterRenderer.stretchRenderer(stretchParameters, rasSelCB.getSelectionModel().getSelectedIndex(), false);
        });
    }
}