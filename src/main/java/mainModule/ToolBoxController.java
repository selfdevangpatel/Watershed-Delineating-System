package mainModule;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;

import wbt.*;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ToolBoxController implements Initializable {

    @FXML private TreeView<Label> wbtTV;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Label fdLB = new Label("Fill Depressions");
        Label d8pLB = new Label("D8 Pointer");
        Label d8faLB = new Label("D8 Flow Accumulation");
        Label watershedLB = new Label("Watershed");
        Label reclassLB = new Label("Reclass");
        Label esLB = new Label("Extract Streams");
        Label rssLB = new Label("Raster Summary Stats");
        Label csvptvLB = new Label("Csv Points To Vector");
        Label rstvLB = new Label("Raster Streams To Vector");
        Label rtvpLB = new Label("Raster To Vector Polygons");

        // Root for Whitebox Tools.
        TreeItem<Label> wbtRoot = new TreeItem<>(new Label("WhiteBox Tools"));

        // Children for Whitebox Tools.
        TreeItem<Label> hydroAnalysis = new TreeItem<>(new Label("Hydrological Analysis"));

        // Children for Hydrological Analysis.
        TreeItem<Label> fillDepressions = new TreeItem<>(fdLB);
        TreeItem<Label> d8Pointer = new TreeItem<>(d8pLB);
        TreeItem<Label> d8FlowAccu = new TreeItem<>(d8faLB);
        TreeItem<Label> watershed = new TreeItem<>(watershedLB);
        hydroAnalysis.getChildren().addAll(fillDepressions, d8Pointer, d8FlowAccu, watershed);

        // Children for Whitebox Tools.
        TreeItem<Label> gisAnalysis = new TreeItem<>(new Label("GIS Analysis"));

        // Children for GIS Analysis.
        TreeItem<Label> reclass = new TreeItem<>(reclassLB);
        gisAnalysis.getChildren().add(reclass);

        // Children for Whitebox Tools.
        TreeItem<Label> streamNetAnalysis = new TreeItem<>(new Label("Stream Network Analysis"));

        // Children for Stream Network Analysis.
        TreeItem<Label> extractStreams = new TreeItem<>(esLB);
        TreeItem<Label> rasStreamsToVec = new TreeItem<>(rstvLB);
        streamNetAnalysis.getChildren().addAll(extractStreams, rasStreamsToVec);

        // Children for Whitebox Tools.
        TreeItem<Label> mathAndStats = new TreeItem<>(new Label("Math and Stats Tools"));

        // Children for Math and Stats Tools.
        TreeItem<Label> rasterSS = new TreeItem<>(rssLB);
        mathAndStats.getChildren().add(rasterSS);

        // Children for Whitebox Tools.
        TreeItem<Label> dataT = new TreeItem<>(new Label("Data Tools"));

        // Children for Data Tools.
        TreeItem<Label> csvptv = new TreeItem<>(csvptvLB);
        TreeItem<Label> rtvp = new TreeItem<>(rtvpLB);
        dataT.getChildren().addAll(csvptv, rtvp);

        // Add the children to WhiteBox Tools root.
        wbtRoot.getChildren().addAll(hydroAnalysis, gisAnalysis, streamNetAnalysis, mathAndStats, dataT);

        wbtTV.setRoot(wbtRoot);
        wbtRoot.setExpanded(true);

        // Set mouse click event for tools.
        fdLB.setOnMouseClicked(e -> {

            if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {

                try {
                    new FillDepressions();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        d8pLB.setOnMouseClicked(e -> {

            if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {

                try {
                    new D8Pointer();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        d8faLB.setOnMouseClicked(e -> {

            if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {

                try {
                    new D8FlowAccumulation();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        watershedLB.setOnMouseClicked(e -> {

            if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {

                try {
                    new Watershed();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        reclassLB.setOnMouseClicked(e -> {

            if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {

                try {
                    new Reclass();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        esLB.setOnMouseClicked(e -> {

            if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {

                try {
                    new ExtractStreams();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        rssLB.setOnMouseClicked(e -> {

            if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {

                try {
                    new RasterSummaryStats();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        csvptvLB.setOnMouseClicked(e -> {

            if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {

                try {
                    new CsvPointsToVector();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        rstvLB.setOnMouseClicked(e -> {

            if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {

                try {
                    new RasterStreamsToVector();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        rtvpLB.setOnMouseClicked(e -> {

            if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {

                try {
                    new RasterToVectorPolygons();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
}