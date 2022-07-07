package mainModule;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.geometry.CoordinateFormatter;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.MapView;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import launch.LaunchApp;

import java.io.File;
import java.io.IOException;

public class MainApp extends Application {

    @FXML private StackPane stackPane;
    @FXML private ComboBox<BasemapStyle> basemapCB;
    @FXML private ComboBox<Integer> setScaleCB;
    @FXML private MenuItem addRasterMI, addVectorMI, exitMI;
    @FXML private VBox layerVBox;
    @FXML private Label welcomeUserLB;

    public String loggedInUser = "Guest";
    public static Stage mainStage;
    public static ArcGISMap map;
    public static MapView mapView = new MapView();
    public static TreeView<Object> layerTV = new TreeView<>();

    @Override
    public void start(Stage mainStage) throws IOException {

        MainApp.mainStage = mainStage;

        mainStage.setOnCloseRequest(e -> {

            e.consume();
            exit_platform();
        });

        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("fxml/main_home.fxml"));
        fxmlLoader.setController(this);

        mainStage.setTitle("Watershed Delineating System");
        mainStage.setScene(new Scene(fxmlLoader.load()));
        mainStage.getIcons().add(new Image(String.valueOf(LaunchApp.class.getResource("images/WDS_Logo.png"))));
        mainStage.show();

        welcomeUserLB.setText("Welcome, " + loggedInUser);

        /* Set your API Key. It is required to enable access to services, web maps, and web scenes
           hosted in ArcGIS Online. */
        String myApiKey = "";
        ArcGISRuntimeEnvironment.setApiKey(myApiKey);

        // Create a new ArcGISMap.
        map = new ArcGISMap(BasemapStyle.ARCGIS_TOPOGRAPHIC);

        // Set the ArcGISMap onto the mapview and add the mapview to stack pane.
        mapView.setMap(map);
        mapView.setViewpoint(new Viewpoint(21.772105, 72.888442, 3700000));
        stackPane.getChildren().add(mapView);
        mapView.toBack();

        // Set up basemap combobox to select different basemap styles.
        basemapCB.setItems(FXCollections.observableArrayList(BasemapStyle.values()));
        basemapCB.getSelectionModel().selectedItemProperty().addListener(o -> {

            BasemapStyle selBasemapStyle = basemapCB.getSelectionModel().getSelectedItem();
            Basemap basemap = new Basemap(selBasemapStyle);
            map.setBasemap(basemap);
        });

        // Integer array for mapview's zoom scale levels.
        Integer[] mapScale = {10000000, 5000000, 2500000, 1000000, 500000, 250000, 100000,
                              50000, 25000, 10000, 5000, 2500, 1000, 500, 250};

        // Set up the scale combo box to select different zoom scales of mapview.
        setScaleCB.setItems(FXCollections.observableArrayList(mapScale));
        setScaleCB.getSelectionModel().selectedItemProperty().addListener(o -> {

            Point centerPoint = mapView.getCurrentViewpoint(Viewpoint.Type.CENTER_AND_SCALE).getTargetGeometry().
                                getExtent().getCenter();
            mapView.setViewpoint(new Viewpoint(centerPoint, setScaleCB.getSelectionModel().getSelectedItem()));
        });

        // Event handler for Add raster layer menu item.
        addRasterMI.setOnAction(e -> {

            try {
                new RasterFileSelector();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        // Event handler for Add feature layer menu item.
        addVectorMI.setOnAction(e -> {

            try {
                new VectorFileSelector();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        // Add root node to tree view.
        layerTV.setShowRoot(false); layerTV.setRoot(new TreeItem<>());
        layerTV.setPrefHeight(600);
        layerVBox.getChildren().add(layerTV);

        // Get coordinates of a point on map.
        mapView.setOnMouseClicked(e -> {
            // Check that the primary mouse button was clicked and user is not panning.
            if (e.isStillSincePress() && e.getButton() == MouseButton.PRIMARY) {
                // Get the map point where the user clicked.
                Point2D point = new Point2D(e.getX(), e.getY());
                Point mapPoint = mapView.screenToLocation(point);
                // Show the callout at the point with the different coordinate format strings.
                showCalloutWithLocationCoordinates(mapPoint);
            } else if (e.getButton() == MouseButton.SECONDARY) mapView.getCallout().dismiss();
        });

        // Event handler for Exit menu item.
        exitMI.setOnAction(e -> exit_platform());
    }

    private void showCalloutWithLocationCoordinates(Point location) {

        Callout callout = mapView.getCallout();
        callout.setTitle("Location:");
        String latLonDecimalDegrees = CoordinateFormatter.toLatitudeLongitude(location,
                                      CoordinateFormatter.LatitudeLongitudeFormat.DECIMAL_DEGREES, 6);
        callout.setDetail("Decimal Degrees: " + latLonDecimalDegrees);
        mapView.getCallout().showCalloutAt(location, new Duration(500));
    }

    // Exit function.
    public void exit_platform() {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setHeaderText("You're about to exit.");
        alert.setContentText("Are you sure?");

        if (alert.showAndWait().orElse(null) == ButtonType.OK) {

            deleteFiles();
            Platform.exit();
        }
    }

    // Delete rasterCache files.
    private void deleteFiles() {

        File folder = new File(System.getProperty("user.dir") + "\\src\\main\\resources\\raster\\rasterCache\\");
        File[] filesList = folder.listFiles();

        boolean filesDeleted = false;

        assert filesList != null;
        for (File i : filesList) {
            if (i.isFile() && !i.getName().equals("Sample.md")) filesDeleted = i.delete();
        }

        if (filesDeleted) System.out.println("Files successfully deleted from 'rasterCache' directory.");
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}