package raster.renderer;

import com.esri.arcgisruntime.raster.*;

import mainModule.LayerManager;

import raster.RasterManager;
import raster.RasterOperation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public class RasterRenderer {

    public static void blendRenderer(String rasterName, double altitudeVal, double azimuthVal, SlopeType slopeType,
                                     int rasIndex, ColorRamp.PresetType colorRampPT, boolean baseRender) {

        String rasterPath = "src/main/resources/raster/rasterCache/" + rasterName;
        int rasBandCount = RasterOperation.getBandCount(rasterPath);

        ColorRamp colorRamp = (colorRampPT != ColorRamp.PresetType.NONE) ? new ColorRamp(colorRampPT, 800) : null;

        boolean canSetRenderer = ((colorRamp == null && rasBandCount == 3) || (colorRamp != null && rasBandCount != 3));

        if (canSetRenderer) {

            // Create blend renderer.
            BlendRenderer blendRenderer = new BlendRenderer(new Raster(rasterPath), Collections.singletonList(9.0D),
                                                            Collections.singletonList(255.0D), null, null, null, null,
                                                            colorRamp, altitudeVal, azimuthVal, 1.0D, slopeType, 1.0D,
                                                            1.0D, 8);

            RasterManager.rlLL.get(rasIndex).setRasterRenderer(blendRenderer);

            int tIRasIndex = LayerManager.tIRasIndex.get(rasIndex);

            if (!baseRender && !LayerManager.layerTILL.get(tIRasIndex).isLeaf())
                LayerManager.layerTILL.get(tIRasIndex).getChildren().remove(0);
        }
    }

    public static void hillshadeRenderer(double altitudeVal, double azimuthVal, SlopeType slopeType, int rasIndex,
                                         boolean baseRender) {

        // Create hillshade renderer.
        HillshadeRenderer hillshadeRenderer = new HillshadeRenderer(altitudeVal, azimuthVal, 0.000016, slopeType, 1, 1,
                                                                    8);

        RasterManager.rlLL.get(rasIndex).setRasterRenderer(hillshadeRenderer);

        int tIRasIndex = LayerManager.tIRasIndex.get(rasIndex);

        if (!baseRender && !LayerManager.layerTILL.get(tIRasIndex).isLeaf())
            LayerManager.layerTILL.get(tIRasIndex).getChildren().remove(0);
    }

    public static void rgbRenderer(StretchParameters stretchParameters, int rasIndex, boolean baseRender) {

        // Create rgb renderer.
        RGBRenderer rgbRenderer = new RGBRenderer(stretchParameters, Arrays.asList(0, 1, 2), null, true);

        RasterManager.rlLL.get(rasIndex).setRasterRenderer(rgbRenderer);

        int tIRasIndex = LayerManager.tIRasIndex.get(rasIndex);

        if (!baseRender && !LayerManager.layerTILL.get(tIRasIndex).isLeaf())
            LayerManager.layerTILL.get(tIRasIndex).getChildren().remove(0);
    }

    public static void stretchRenderer(StretchParameters stretchParameters, int rasIndex, boolean baseRender) {

        // Create stretch renderer.
        StretchRenderer stretchRenderer = new StretchRenderer(stretchParameters, null, true, null);

        RasterManager.rlLL.get(rasIndex).setRasterRenderer(stretchRenderer);

        int tIRasIndex = LayerManager.tIRasIndex.get(rasIndex);

        if (!baseRender && !LayerManager.layerTILL.get(tIRasIndex).isLeaf())
            LayerManager.layerTILL.get(tIRasIndex).getChildren().remove(0);
    }

    public static void colormapRenderer(String colormapType, int rasIndex, boolean baseRender) {

        List<Integer> colors = Collections.singletonList(0xFFFFFFFF);

        if (Objects.equals(colormapType, "D8 Pointer")) {

            /* Create a color map with following properties:
            1 = RED
            2 = YELLOW
            3 - 4 = MAGENTA
            6 - 8 = LT-GRAY
            9 - 16 = GREEN
            17 - 32 = DK-GRAY
            33 - 64 = BLUE
            65 - 128 = CYAN
            129 - 255 = WHITE
            */

            colors = IntStream.range(1, 255)
                    .boxed()
                    .map(i -> i == 1 ? 0xFFFF0000 : (
                            i == 2 ? 0xFFFFFF00 : (
                                    (i > 2 && i <= 4) ? 0xFFFF00FF : (
                                            (i > 4 && i <= 8) ? 0xFFCCCCCC : (
                                                    (i > 8 && i <= 16) ? 0xFF00FF00 : (
                                                            (i > 16 && i <= 32) ? 0xFF444444 : (
                                                                    (i > 32 && i <= 64) ? 0xFF0000FF : (
                                                                            (i > 64 && i <= 128) ? 0xFF00FFFF :
                                                                                                   0xFFFFFFFF
                                                                    )
                                                            )
                                                    )
                                            )
                                    )
                            )
                    )).toList();
        }

        // Create a colormap renderer.
        ColormapRenderer colormapRenderer = new ColormapRenderer(colors);

        RasterManager.rlLL.get(rasIndex).setRasterRenderer(colormapRenderer);

        int tIRasIndex = LayerManager.tIRasIndex.get(rasIndex);

        if (!baseRender && !LayerManager.layerTILL.get(tIRasIndex).isLeaf())
            LayerManager.layerTILL.get(tIRasIndex).getChildren().remove(0);

        RasterManager.d8Pointer = false;
    }
}