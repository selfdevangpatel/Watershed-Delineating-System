package mainModule;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.tasks.geocode.GeocodeParameters;
import com.esri.arcgisruntime.tasks.geocode.GeocodeResult;
import com.esri.arcgisruntime.tasks.geocode.LocatorTask;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SearchController {

    @FXML private Button searchBtn;
    @FXML private TextField searchKeyTF;

    private LocatorTask locatorTask;
    private static PictureMarkerSymbol pinSymbol;

    public void initialize() {

        // Set the callout's default style.
        Callout callout = MainApp.mapView.getCallout();
        callout.setLeaderPosition(Callout.LeaderPosition.BOTTOM);

        // Create a locatorTask.
        locatorTask = new LocatorTask("https://geocode-api.arcgis.com/arcgis/rest/services/World/GeocodeServer");

        // Create geocode task parameters.
        GeocodeParameters geocodeParameters = new GeocodeParameters();
        // Return all attributes.
        geocodeParameters.getResultAttributeNames().add("*");
        geocodeParameters.setMaxResults(1); // Get the closest match.
        geocodeParameters.setOutputSpatialReference(MainApp.mapView.getSpatialReference());

        // Create a pin graphic.
        pinSymbol = new PictureMarkerSymbol("mainModule/images/search/pin.png");
        pinSymbol.loadAsync();

        // Event to get geocode when query is submitted.
        searchBtn.setOnAction(e -> {
            // Get the user's query.
            String query = searchKeyTF.getText();

            if (!query.equals("")) {
                // Hide callout if showing.
                MainApp.mapView.getCallout().dismiss();

                // Run the locatorTask geocode task.
                ListenableFuture<List<GeocodeResult>> results = locatorTask.geocodeAsync(query, geocodeParameters);

                // Add a listener to display the result when loaded.
                results.addDoneListener(new ResultsLoadedListener(results));
            }
        });
    }


    // Runnable listener to update marker and callout when new results are loaded.
    private record ResultsLoadedListener(ListenableFuture<List<GeocodeResult>> results) implements Runnable {

        /**
         * Constructs a runnable listener for the geocode results.
         *
         * @param results results from a {@link LocatorTask#geocodeAsync} task.
         */
        private ResultsLoadedListener {}

        @Override
        public void run() {

            try {

                List<GeocodeResult> geocodes = results.get();

                if (geocodes.size() > 0) {
                    // Get the top result.
                    GeocodeResult geocode = geocodes.get(0);

                    // Get attributes from the result for the callout.
                    String addrType = geocode.getAttributes().get("Addr_type").toString();
                    String placeName = geocode.getAttributes().get("PlaceName").toString();
                    String placeAddr = geocode.getAttributes().get("Place_addr").toString();
                    String matchAddr = geocode.getAttributes().get("Match_addr").toString();
                    String locType = geocode.getAttributes().get("Type").toString();

                    // Format callout details.
                    String title, detail, matchAddrSubStr;
                    matchAddrSubStr = matchAddr.substring(matchAddr.indexOf(", ") + 2);

                    switch (addrType) {

                        case "POI":

                            title = placeName.equals("") ? "" : placeName;
                            if (!placeAddr.equals("")) detail = placeAddr;
                            else if (!locType.equals("")) detail = !matchAddr.contains(",") ? locType : matchAddrSubStr;
                            else detail = "";
                            break;

                        case "StreetName":

                        case "PointAddress":

                        case "Postal":

                            if (matchAddr.contains(",")) {
                                title = matchAddr.split(",")[0];
                                detail = matchAddrSubStr;
                                break;
                            }

                        default:

                            title = "";
                            detail = matchAddr;
                    }

                    HashMap<String, Object> attributes = new HashMap<>();
                    attributes.put("title", title);
                    attributes.put("detail", detail);

                    // Create the marker.
                    Graphic marker = new Graphic(geocode.getDisplayLocation(), attributes, pinSymbol);

                    // Set the viewpoint to the marker.
                    Point location = geocodes.get(0).getDisplayLocation();
                    MainApp.mapView.setViewpointCenterAsync(location, 10000);

                    // Update the marker.
                    Platform.runLater(() -> {
                        // Clear out previous results.
                        SketchOnMap.graphicsOverlay.getGraphics().clear();

                        // Add the marker to the graphics overlay.
                        SketchOnMap.graphicsOverlay.getGraphics().add(marker);

                        // Display the callout.
                        Callout callout = MainApp.mapView.getCallout();
                        callout.setTitle(marker.getAttributes().get("title").toString());
                        callout.setDetail(marker.getAttributes().get("detail").toString());
                        callout.showCalloutAt(location, new Point2D(0, -24), Duration.ZERO);
                    });
                }

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
}