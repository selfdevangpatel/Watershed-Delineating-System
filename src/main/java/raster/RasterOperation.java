package raster;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class RasterOperation {

    public static int getBandCount(String rasterPath) {

        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(rasterPath));
        } catch(Exception e) {
            e.printStackTrace();
        }

        assert img != null;
        java.awt.image.Raster raster = img.getData();

        return raster.getNumBands();
    }

    public static String[] getMinMax(String rasterPath) {

        String[] minMax = new String[2];

        Process process;
        String wbtPath = "src/main/resources/wbt/executable/whitebox_tools.exe";

        try {

            process = Runtime.getRuntime().exec(wbtPath + " -r=\"RasterSummaryStats\"" + " -i=\"" + rasterPath + "\"");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {

                if (line.contains("Image minimum")) minMax[0] = line.substring(15);
                else if (line.contains("Image maximum")) minMax[1] = line.substring(15);
            }

            process.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return minMax;
    }
}