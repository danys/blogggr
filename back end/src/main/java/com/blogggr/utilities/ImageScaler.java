package com.blogggr.utilities;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import javax.imageio.ImageIO;

/**
 * Created by Daniel Sunnen on 15.09.17.
 */
public class ImageScaler {

  private ImageScaler(){
    //hide otherwise implicit public constructor
  }

  public static void scaleImageFile(Path inPath, Path outPath, int width, int height) throws IOException {
    BufferedImage originalImage = ImageIO.read(inPath.toFile());
    BufferedImage scaledImage = scaleImage(originalImage, width, height);
    ImageIO.write(scaledImage, "png", outPath.toFile());
  }

  private static BufferedImage scaleImage(BufferedImage originalImage, int width, int height) {
    BufferedImage scaledImage = new BufferedImage(width, height, originalImage.getType());
    Graphics2D g = scaledImage.createGraphics();
    g.drawImage(originalImage, 0, 0, width, height, null);
    g.dispose();
    return scaledImage;
  }

  public static class ImageSize{
    private int width;
    private int height;

    public ImageSize(int width, int height){
      this.width = width;
      this.height = height;
    }

    public int getWidth(){
      return width;
    }

    public int getHeight(){
      return height;
    }
  }

  public static ImageSize getImageSize(Path inPath) throws IOException{
    BufferedImage image = ImageIO.read(inPath.toFile());
    return new ImageSize(image.getWidth(), image.getHeight());
  }

}
