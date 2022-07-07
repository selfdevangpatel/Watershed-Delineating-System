package raster;

import com.esri.arcgisruntime.layers.RasterLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.raster.MinMaxStretchParameters;
import com.esri.arcgisruntime.raster.Raster;
import com.esri.arcgisruntime.raster.StretchParameters;

import javafx.scene.control.Alert;

import mainModule.LayerManager;
import mainModule.MainApp;

import raster.renderer.RasterRenderer;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class RasterManager {

    public static LinkedList<RasterLayer> rlLL = new LinkedList<>();
    public static List<String> rasterNameList = new ArrayList<>();

    public static boolean d8Pointer = false;

    public static void addRasterLayer(String rasterPath) {

        RasterLayer rasterLayer = new RasterLayer(new Raster(rasterPath));

        String rfName = rasterPath.substring(rasterPath.lastIndexOf("\\") + 1);

        if (rasterNameList.contains(rfName)) {

            Alert alert = new Alert(Alert.AlertType.ERROR, "A raster with the name \"" + rfName +
                                    "\"" + " already exists!");
            alert.show();
        } else MainApp.map.getOperationalLayers().add(rasterLayer);

        rasterLayer.addDoneLoadingListener(() -> {

            if (rasterLayer.getLoadStatus() == LoadStatus.LOADED) {

                rasterNameList.add(rfName);
                if (RasterOperation.getBandCount(rasterPath) == 1) {

                    rlLL.add(rasterLayer);
                    if (d8Pointer) {

                        LayerManager.addToLM(rfName);
                        RasterRenderer.colormapRenderer("D8 Pointer", rlLL.lastIndexOf(rlLL.getLast()), true);
                    } else {

                        String[] minMax = RasterOperation.getMinMax(rasterPath);
                        LayerManager.addToLM(rfName, minMax[0], minMax[1]);
                        if (isDouble(minMax[0]) && isDouble(minMax[1])) {

                            StretchParameters stretchParameters = new MinMaxStretchParameters(
                                    Collections.singletonList(Double.parseDouble(minMax[0])),
                                    Collections.singletonList(Double.parseDouble(minMax[1])));

                            RasterRenderer.stretchRenderer(stretchParameters, rlLL.lastIndexOf(rlLL.getLast()), true);
                        }
                    }
                } else if (RasterOperation.getBandCount(rasterPath) == 3) {

                    rlLL.add(rasterLayer);
                    LayerManager.addToLM(rfName);
                } else {

                    LayerManager.undefinedRBC = true;
                    rlLL.add(rasterLayer);
                    LayerManager.addToLM(rfName);
                }

                try {
                    saveRasterFile(new File(rasterPath), rfName);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                MainApp.mapView.setViewpointGeometryAsync(rasterLayer.getFullExtent(), 85);
            } else {

                Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to load raster layer.");
                alert.show();
            }
        });
    }

    private static void saveRasterFile(File rasterFile, String rasterFileName) throws IOException {

        String savePath = "src/main/resources/raster/rasterCache/" + rasterFileName;

        try (FileChannel src = new FileInputStream(rasterFile).getChannel();
             FileChannel destDEM = new FileOutputStream(savePath).getChannel()) {
             destDEM.transferFrom(src, 0, src.size());
        }
    }

    private static boolean isDouble(String string) {

        try {
            Double.parseDouble(string);
        }
        catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}