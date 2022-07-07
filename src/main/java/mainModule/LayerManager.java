package mainModule;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;

import raster.RasterManager;

import java.util.ArrayList;
import java.util.LinkedList;

public class LayerManager {

    public static LinkedList<TreeItem<Object>> layerTILL = new LinkedList<>();
    static LinkedList<CheckBox> layerCBXLL = new LinkedList<>();
    static LinkedList<VBox> layerInfoVBox = new LinkedList<>();

    public static ArrayList<Integer> tIRasIndex = new ArrayList<>();

    public static boolean undefinedRBC = false;

    // Method to add layer info for raster with band count 1.
    public static void addToLM(String layerName, String minVal, String maxVal) {

        // Create layer checkbox and add it to linked list.
        layerCBXLL.add(new CheckBox(layerName));
        layerCBXLL.getLast().setSelected(true);
        layerTILL.add(new TreeItem<>(layerCBXLL.getLast()));

        // Add the raster index.
        tIRasIndex.add(layerTILL.indexOf(layerTILL.getLast()));

        // Add listener for checkbox to show/hide layers.
        addCBXListener();

        // Add checkbox tree item in treeview.
        MainApp.layerTV.setShowRoot(true);
        MainApp.layerTV.getTreeItem(0).getChildren().add(layerTILL.getLast());
        MainApp.layerTV.setShowRoot(false);

        // Create rectangular grayscale linear gradient.
        Stop[] stops = new Stop[]{new Stop(0, Color.BLACK), new Stop(1, Color.WHITE)};
        LinearGradient gradient = new LinearGradient(1, 1, 1, 0, true, CycleMethod.NO_CYCLE, stops);
        Rectangle rect = new Rectangle();
        rect.setWidth(20); rect.setHeight(50);
        rect.setFill(gradient);

        // Add required leaf nodes in tree item.
        layerInfoVBox.add(new VBox());
        layerInfoVBox.getLast().getChildren().addAll(new Label("Band 1 (Gray)"), new Label(maxVal), rect,
                                                     new Label(minVal));
        layerTILL.getLast().getChildren().add(new TreeItem<>(layerInfoVBox.getLast()));

        // Set the tree item to expanded.
        layerTILL.getLast().setExpanded(true);
    }

    // Method to add layer info for raster with band count 3 or other except 1.
    public static void addToLM(String layerName) {

        // Create layer checkbox and add it to linked list.
        layerCBXLL.add(new CheckBox(layerName));
        layerCBXLL.getLast().setSelected(true);
        layerTILL.add(new TreeItem<>(layerCBXLL.getLast()));

        // Add the raster index.
        tIRasIndex.add(layerTILL.indexOf(layerTILL.getLast()));

        // Add listener for checkbox to show/hide layers.
        addCBXListener();

        // Add checkbox tree item in treeview.
        MainApp.layerTV.setShowRoot(true);
        MainApp.layerTV.getTreeItem(0).getChildren().add(layerTILL.getLast());
        MainApp.layerTV.setShowRoot(false);

        // Add required leaf nodes in tree item.
        if (RasterManager.d8Pointer && !undefinedRBC) {

            layerInfoVBox.add(new VBox());
            layerInfoVBox.getLast().getChildren().addAll(new Label("D8 Pointer Color Map:"),
                          new Label("1", new ImageView("mainModule/images/d8_pointer/Red.png")),
                          new Label("2", new ImageView("mainModule/images/d8_pointer/Yellow.png")),
                          new Label("3 - 4", new ImageView("mainModule/images/d8_pointer/Magenta.png")),
                          new Label("6 - 8", new ImageView("mainModule/images/d8_pointer/LtGray.png")),
                          new Label("9 - 16", new ImageView("mainModule/images/d8_pointer/Green.png")),
                          new Label("17 - 32", new ImageView("mainModule/images/d8_pointer/DkGray.png")),
                          new Label("33 - 64", new ImageView("mainModule/images/d8_pointer/Blue.png")),
                          new Label("65 - 128", new ImageView("mainModule/images/d8_pointer/Cyan.png")),
                          new Label("Other", new ImageView("mainModule/images/d8_pointer/White.png")));
        } else if (!undefinedRBC) {

            layerInfoVBox.add(new VBox());
            layerInfoVBox.getLast().getChildren().addAll(new Label("RGB Band"),
                          new Label("Band 1 (Red)", new ImageView("mainModule/images/d8_pointer/Red.png")),
                          new Label("Band 2 (Green)", new ImageView("mainModule/images/d8_pointer/Green.png")),
                          new Label("Band 3 (Blue)", new ImageView("mainModule/images/d8_pointer/Blue.png")));
        }

        layerTILL.getLast().getChildren().add(new TreeItem<>(layerInfoVBox.getLast()));

        // Set the tree item to expanded.
        layerTILL.getLast().setExpanded(true);
    }

    private static void addCBXListener() {

        int cBXIndex = layerCBXLL.indexOf(layerCBXLL.getLast());
        int rLLLIndex = RasterManager.rlLL.indexOf(RasterManager.rlLL.getLast());

        layerCBXLL.getLast().selectedProperty().addListener(
                (ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {

                    if (layerCBXLL.get(cBXIndex).isSelected() && !MainApp.map.getOperationalLayers().
                            contains(RasterManager.rlLL.get(rLLLIndex)))
                        MainApp.map.getOperationalLayers().add(RasterManager.rlLL.get(rLLLIndex));
                    else if (!layerCBXLL.get(cBXIndex).isSelected())
                        MainApp.map.getOperationalLayers().remove(RasterManager.rlLL.get(rLLLIndex));
                }
        );
    }
}