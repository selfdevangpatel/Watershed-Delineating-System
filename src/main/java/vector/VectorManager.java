package vector;

import com.esri.arcgisruntime.data.ShapefileFeatureTable;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TreeItem;

import mainModule.LayerManager;
import mainModule.MainApp;

import java.util.LinkedList;

public class VectorManager {

    public static LinkedList<FeatureLayer> vlLL = new LinkedList<>();
    public static LinkedList<String> vectorNameList = new LinkedList<>();
    public static LinkedList<CheckBox> vCBXLL = new LinkedList<>();

    public static void addVectorLayer(String vFPath) {

        ShapefileFeatureTable sFVT = new ShapefileFeatureTable(vFPath);
        FeatureLayer vectorLayer = new FeatureLayer(sFVT);

        String vFName = vFPath.substring(vFPath.lastIndexOf("\\") + 1);

        if (vectorNameList.contains(vFName)) {

            Alert alert = new Alert(Alert.AlertType.ERROR, "A vector with the name \"" + vFName + "\"" +
                                    " already exists!");
            alert.show();
        } else MainApp.map.getOperationalLayers().add(vectorLayer);

        vectorLayer.addDoneLoadingListener(() -> {

            if (vectorLayer.getLoadStatus() == LoadStatus.LOADED) {
                // Zoom to the area containing the layer's vector.
                MainApp.mapView.setViewpointGeometryAsync(vectorLayer.getFullExtent());

                vectorNameList.add(vFName);
                vlLL.add(vectorLayer);
                vCBXLL.add(new CheckBox(vectorNameList.getLast()));
                vCBXLL.getLast().setSelected(true);

                int cbIndex = vCBXLL.indexOf(vCBXLL.getLast());
                int vlIndex = vlLL.indexOf(vlLL.getLast());

                // Listener for vector layer checkbox.
                vCBXLL.get(cbIndex).selectedProperty().addListener(

                        (ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {

                            if(vCBXLL.get(cbIndex).isSelected() && !MainApp.map.getOperationalLayers().
                                    contains(vlLL.get(vlIndex)))
                                MainApp.map.getOperationalLayers().add(vlLL.get(vlIndex));
                            else if(!vCBXLL.get(cbIndex).isSelected()) MainApp.map.getOperationalLayers().
                                    remove(vlLL.get(vlIndex));
                        }
                );

                LayerManager.layerTILL.add(new TreeItem<>(vCBXLL.getLast()));

                MainApp.layerTV.setShowRoot(true);
                MainApp.layerTV.getTreeItem(0).getChildren().add(LayerManager.layerTILL.getLast());
                MainApp.layerTV.setShowRoot(false);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, vectorLayer.getLoadError().getMessage());
                alert.show();
            }
        });
    }
}