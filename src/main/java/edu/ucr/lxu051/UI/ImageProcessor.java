package edu.ucr.lxu051.UI;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.image.PNGTranscoder;

import java.awt.image.BufferedImage;

public class ImageProcessor {

    public static BufferedImage loadImage(String svgFile, float width, float height) {
        try {
            BufferedImageTranscoder imageTranscoder = new BufferedImageTranscoder();

            imageTranscoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, width);
            imageTranscoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, height);

            TranscoderInput input = new TranscoderInput(svgFile);
            imageTranscoder.transcode(input, null);

            return imageTranscoder.getBufferedImage();
        } catch (TranscoderException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static BufferedImage rotate( BufferedImage img) {
        int width  = img.getWidth();
        int height = img.getHeight();
        BufferedImage newImage = new BufferedImage(height, width, img.getType());

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                newImage.setRGB(height - 1 - j, i, img.getRGB(i, j));
            }
        }
        return newImage;
    }
}
