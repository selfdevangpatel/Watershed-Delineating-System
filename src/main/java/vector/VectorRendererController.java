package vector;

import com.esri.arcgisruntime.symbology.*;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.stage.Modality;
import javafx.stage.Stage;

import mainModule.MainApp;

import java.io.IOException;
import java.util.Objects;

public class VectorRendererController {

    @FXML private ComboBox<String> typeCB;
    @FXML private ComboBox<String> colorCB;
    @FXML private ComboBox<String> vecSelCB;
    @FXML private Slider sizeSL;
    @FXML private Button applyBtn;

    int selColor = 0xFFFF0000;

    public VectorRendererController() throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(VectorRendererController.class.getResource(
                                               "renderer/fxml/vector_renderer.fxml"));
        fxmlLoader.setController(this);

        Stage brStage = new Stage();
        brStage.setTitle("Vector Renderer");
        brStage.setScene(new Scene(fxmlLoader.load()));
        brStage.setResizable(false);
        brStage.initOwner(MainApp.mainStage);
        brStage.initModality(Modality.APPLICATION_MODAL);
        brStage.show();

        typeCB.setItems(FXCollections.observableArrayList("Point", "Polyline", "Polygon"));
        typeCB.getSelectionModel().select(0);
        colorCB.setItems(FXCollections.observableArrayList("Red", "Green", "Blue", "White", "Black"));
        colorCB.getSelectionModel().select(0);
        vecSelCB.getItems().setAll(VectorManager.vectorNameList);

        if (VectorManager.vlLL.isEmpty()) {

            vecSelCB.setPromptText("--Empty--");
            applyBtn.setDisable(true);
        } else {

            applyBtn.setDisable(false);
            vecSelCB.getSelectionModel().selectFirst();
        }

        typeCB.getSelectionModel().selectedItemProperty().addListener(o ->
                sizeSL.setDisable(Objects.equals(typeCB.getSelectionModel().getSelectedItem(), "Polygon")));

        colorCB.getSelectionModel().selectedItemProperty().addListener(o -> {

            switch (colorCB.getSelectionModel().getSelectedItem()) {

                case "Red" -> selColor = 0xFFFF0000;
                case "Green" -> selColor = 0xFF00FF00;
                case "Blue" -> selColor = 0xFF0000FF;
                case "White" -> selColor = 0xFFFFFFFF;
                case "Black" -> selColor = 0xFF000000;
            }
        });

        applyBtn.setOnAction(e -> {

            String selType = typeCB.getSelectionModel().getSelectedItem();
            int selVec = vecSelCB.getSelectionModel().getSelectedIndex();

            switch (selType) {

                case "Point" -> {

                    SimpleMarkerSymbol simpleMarkerSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE,
                                                                                   selColor,
                                                                                   (float) sizeSL.getValue());
                    SimpleRenderer pointRenderer = new SimpleRenderer(simpleMarkerSymbol);
                    VectorManager.vlLL.get(selVec).setRenderer(pointRenderer);
                }

                case "Polyline" -> {

                    SimpleLineSymbol simpleLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, selColor,
                                                                             (float) sizeSL.getValue());
                    SimpleRenderer polylineRenderer = new SimpleRenderer(simpleLineSymbol);
                    VectorManager.vlLL.get(selVec).setRenderer(polylineRenderer);
                }

                case "Polygon" -> {

                    SimpleFillSymbol simpleFillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.VERTICAL, selColor,
                                                        new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, selColor,
                                                                             2));
                    SimpleRenderer polygonRenderer = new SimpleRenderer(simpleFillSymbol);
                    VectorManager.vlLL.get(selVec).setRenderer(polygonRenderer);
                }
            }
        });
    }
}