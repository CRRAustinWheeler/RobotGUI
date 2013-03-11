/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imageprocessing;

import java.awt.Image;
import java.awt.image.BufferedImage;

/**
 *
 * @author laptop
 */
public class RawImageProcessor {

    int[][] data;
    public static double thresholdA = 0.12;

    public static int[] ProcessImage(BufferedImage img) {
        int w = img.getWidth(null);
        int h = img.getHeight(null);
        double pixelCount = w * h;
        if (w != -1 && h != -1) {
            int[] thresholdData = new int[765];
            int[][] data = new int[w][h];
            for (int i = 0; i < data.length; i++) {
                for (int j = 0; j < data[i].length; j++) {
                    data[i][j] = getPixelData(img, i, j);
                    thresholdData[data[i][j]]++;
                }
            }
            double currentCount = 0;
            int fromThresholdA;
            for (fromThresholdA = 764; currentCount / pixelCount < thresholdA;
                    fromThresholdA--) {
                currentCount += thresholdData[fromThresholdA];
            }
            fromThresholdA = 763-fromThresholdA;
            
        }

        return null;
    }

    private static int getPixelData(BufferedImage img, int x, int y) {
        int argb = img.getRGB(x, y);
        int value = ((argb >> 16) & 0xff)
                + ((argb >> 8) & 0xff) + ((argb) & 0xff);
        return value;
    }
}
