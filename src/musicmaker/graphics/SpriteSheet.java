package musicmaker.graphics;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import musicmaker.MusicMaker;
import javax.imageio.ImageIO;

public class SpriteSheet {

   protected String path;
   public final int SIZE_X, SIZE_Y;
   protected final int[] pixels;

   //public static SpriteSheet map = new SpriteSheet("/Res/Maps/citybackground.bmp", 1600, 1600);

   public SpriteSheet(String path, int sizex, int sizey) {
      this.path = path;
      SIZE_X = sizex;
      SIZE_Y = sizey;
      pixels = new int[SIZE_X * SIZE_Y];
      load();
   }

   protected void load() {

      try {
         URL location = MusicMaker.class.getProtectionDomain().getCodeSource().getLocation();
         File file = new File(location.getFile());
         BufferedImage image = ImageIO.read(new File(file.getParentFile() + path));
         //BufferedImage image = ImageIO.read(MusicMaker.class.getResource(path));
         int w = image.getWidth();
         int h = image.getHeight();
         image.getRGB(0, 0, w, h, pixels, 0, w);
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

}
