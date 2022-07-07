package mainModule;

import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.mapping.view.IdentifyGraphicsOverlayResult;
import com.esri.arcgisruntime.mapping.view.SketchCreationMode;
import com.esri.arcgisruntime.mapping.view.SketchEditor;
import com.esri.arcgisruntime.symbology.SimpleFillSymbol;
import com.esri.arcgisruntime.symbology.SimpleLineSymbol;
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol;

public class SketchOnMap {

    @FXML private Button redoBtn; @FXML private Button undoBtn;
    @FXML private Button clearBtn; @FXML private Button saveBtn;
    @FXML private Button editBtn; @FXML private Button cancelBtn;
    @FXML private Button pointBtn; @FXML private Button multipointBtn;
    @FXML private Button polylineBtn; @FXML private Button polygonBtn;
    @FXML private Button fhPolylineBtn; @FXML private Button fhPolygonBtn;

    private SketchEditor sketchEditor;
    public static GraphicsOverlay graphicsOverlay;
    private Graphic graphic;
    private SimpleFillSymbol fillSymbol;
    private SimpleLineSymbol lineSymbol;
    private SimpleMarkerSymbol pointSymbol;

    ImageView cB = new ImageView(new Image("mainModule/images/sketch_box/clear.png"));
    ImageView eB = new ImageView(new Image("mainModule/images/sketch_box/edit.png"));
    ImageView mP = new ImageView(new Image("mainModule/images/sketch_box/multipoint.png"));
    ImageView po = new ImageView(new Image("mainModule/images/sketch_box/point.png"));
    ImageView pG = new ImageView(new Image("mainModule/images/sketch_box/polygon.png"));
    ImageView pL = new ImageView(new Image("mainModule/images/sketch_box/polyline.png"));
    ImageView fPg = new ImageView(new Image("mainModule/images/sketch_box/brush.png"));
    ImageView fPl = new ImageView(new Image("mainModule/images/sketch_box/freehand-polyline.png"));
    ImageView rB = new ImageView(new Image("mainModule/images/sketch_box/redo.png"));
    ImageView uB = new ImageView(new Image("mainModule/images/sketch_box/undo.png"));
    ImageView clearB = new ImageView(new Image("mainModule/images/sketch_box/trash-can-outline.png"));
    ImageView saveB = new ImageView(new Image("mainModule/images/sketch_box/save.png"));

    public void initialize() {

        redoBtn.setDisable(true); undoBtn.setDisable(true);
        clearBtn.setDisable(true); editBtn.setDisable(true);
        cancelBtn.setDisable(true); saveBtn.setDisable(true);

        // Create a graphics overlay for the graphics.
        graphicsOverlay = new GraphicsOverlay();

        // Add the graphics overlay to the map view.
        MainApp.mapView.getGraphicsOverlays().add(graphicsOverlay);

        // Create a new sketch editor and add it to the map view.
        sketchEditor = new SketchEditor();
        MainApp.mapView.setSketchEditor(sketchEditor);

        // Red square for points.
        pointSymbol = new SimpleMarkerSymbol(SimpleMarkerSymbol.Style.SQUARE, 0xFFFF0000, 20);

        // Thin green line for polylines.
        lineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFF64c113, 4);

        // Blue outline for polygons.
        SimpleLineSymbol polygonLineSymbol = new SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, 0xFF1396c1, 4);

        // Cross-hatched interior for polygons.
        fillSymbol = new SimpleFillSymbol(SimpleFillSymbol.Style.CROSS, 0x40FFA9A9, polygonLineSymbol);

        // Add a listener for when sketch geometry is changed.
        sketchEditor.addGeometryChangedListener(SketchGeometryChangedListener -> {

            cancelBtn.setDisable(false);
            // Save button enable depends on if the sketch is valid.
            // If the sketch is valid then set disable opposite of true.
            saveBtn.setDisable(!sketchEditor.isSketchValid());
            undoBtn.setDisable(!sketchEditor.canUndo());
            redoBtn.setDisable(!sketchEditor.canUndo());
        });

        cancelBtn.setGraphic(cB); editBtn.setGraphic(eB);
        multipointBtn.setGraphic(mP); pointBtn.setGraphic(po);
        polygonBtn.setGraphic(pG); polylineBtn.setGraphic(pL);
        fhPolygonBtn.setGraphic(fPg); fhPolylineBtn.setGraphic(fPl);
        redoBtn.setGraphic(rB); undoBtn.setGraphic(uB);
        clearBtn.setGraphic(clearB); saveBtn.setGraphic(saveB);

        editBtn.setOnAction(e -> {

            cancelBtn.setDisable(false);
            saveBtn.setDisable(true);

            /* If the graphics overlay contains graphics, select the first graphic
               and start the sketch editor based on that graphic's geometry. */
            if (!graphicsOverlay.getSelectedGraphics().isEmpty()) {

                graphic = graphicsOverlay.getSelectedGraphics().get(0);
                sketchEditor.start(graphic.getGeometry());
            }
        });

        cancelBtn.setOnAction(e -> {

            sketchEditor.stop();
            graphicsOverlay.clearSelection();
            disableButtons();
            // Set text to inform the user the sketch is disabled.
            cancelBtn.setDisable(false);

            // Allow graphics to be selected after stop button is used.
            selectGraphic();
        });

        pointBtn.setOnAction(e -> {

            graphicsOverlay.clearSelection();
            sketchEditor.start(SketchCreationMode.POINT);
        });

        multipointBtn.setOnAction(e -> {

            graphicsOverlay.clearSelection();
            sketchEditor.start(SketchCreationMode.MULTIPOINT);
        });

        polylineBtn.setOnAction(e -> {

            graphicsOverlay.clearSelection();
            sketchEditor.start(SketchCreationMode.POLYLINE);
        });

        polygonBtn.setOnAction(e -> {

            graphicsOverlay.clearSelection();
            sketchEditor.start(SketchCreationMode.POLYGON);
        });

        fhPolylineBtn.setOnAction(e -> {

            graphicsOverlay.clearSelection();
            sketchEditor.start(SketchCreationMode.POLYGON);
        });

        fhPolygonBtn.setOnAction(e -> {

            graphicsOverlay.clearSelection();
            sketchEditor.start(SketchCreationMode.FREEHAND_POLYGON);
        });

        undoBtn.setOnAction(e -> {

            if (sketchEditor.canUndo()) {
                sketchEditor.undo();
            }
        });

        redoBtn.setOnAction(e -> {

            if (sketchEditor.canRedo()) {
                sketchEditor.redo();
            }
        });

        saveBtn.setOnAction(e -> {

            Geometry sketchGeometry = sketchEditor.getGeometry();

            /* If an existing graphic is being edited: Get the selected graphic, set its geometry to that of the
               sketch editor geometry. If a new graphic: Create a new graphic based on the sketch editor geometry
               and set symbol depending on geometry type. */
            if (sketchGeometry != null) {

                if (!graphicsOverlay.getSelectedGraphics().isEmpty()) {

                    graphic = graphicsOverlay.getSelectedGraphics().get(0);
                    graphic.setGeometry(sketchGeometry);
                } else {

                    graphic = new Graphic(sketchGeometry);

                    switch (sketchGeometry.getGeometryType()) {

                        case POLYGON -> graphic.setSymbol(fillSymbol);
                        case POLYLINE -> graphic.setSymbol(lineSymbol);
                        case POINT, MULTIPOINT -> graphic.setSymbol(pointSymbol);
                    }
                    graphicsOverlay.getGraphics().add(graphic);
                }
            }
            sketchEditor.stop();

            // Allow the user to select a graphic from the map view.
            selectGraphic();
            graphicsOverlay.clearSelection();
            disableButtons();

            if (!graphicsOverlay.getGraphics().isEmpty()) clearBtn.setDisable(false);
            cancelBtn.setDisable(true);
        });

        clearBtn.setOnAction(e -> {

            graphicsOverlay.getGraphics().clear();
            sketchEditor.stop();
            disableButtons();
        });
    }

    private void selectGraphic() {

        MainApp.mapView.setOnMouseClicked(e -> {

            graphicsOverlay.clearSelection();
            Point2D mapViewPoint = new Point2D(e.getX(), e.getY());

            // Get graphics near the clicked location.
            ListenableFuture<IdentifyGraphicsOverlayResult> identifyGraphics;
            identifyGraphics = MainApp.mapView.identifyGraphicsOverlayAsync(graphicsOverlay, mapViewPoint, 10, false);

            identifyGraphics.addDoneListener(() -> {

                try {

                    if (!identifyGraphics.get().getGraphics().isEmpty()) {
                        // Store the selected graphic.
                        graphic = identifyGraphics.get().getGraphics().get(0);
                        graphic.setSelected(true);
                        editBtn.setDisable(false);
                    } else editBtn.setDisable(true);
                } catch (Exception x) {
                    x.printStackTrace();
                }
            });
        });
    }

    private void disableButtons() {

        clearBtn.setDisable(true);
        redoBtn.setDisable(true);
        undoBtn.setDisable(true);
        editBtn.setDisable(true);
        saveBtn.setDisable(true);
        cancelBtn.setDisable(true);
    }
}