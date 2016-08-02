package musicmaker.graphics;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import musicmaker.MusicMaker;

import javax.imageio.ImageIO;

public class Sprite {

   public final int SIZE_X, SIZE_Y;
   protected int x, y;
   protected int[] pixels;
   protected SpriteSheet sheet;
   protected String path;

   public Sprite(int size, int x, int y, SpriteSheet sheet) {
      SIZE_X = SIZE_Y = size;
      this.x = x * size;
      this.y = y * size;
      this.sheet = sheet;
      pixels = new int[SIZE_X * SIZE_Y];
      load();
   }

   public Sprite(int sizex, int sizey, int x, int y, SpriteSheet sheet) {
      SIZE_X = sizex;
      SIZE_Y = sizey;
      this.x = x * sizex;
      this.y = y * sizey;
      this.sheet = sheet;
      pixels = new int[SIZE_X * SIZE_Y];
      load();
   }

   public Sprite(int size, int color) {
      SIZE_X = SIZE_Y = size;
      pixels = new int[SIZE_X * SIZE_Y];
      setColor(color);
   }

   public Sprite(int sizex, int sizey, int color) {
      SIZE_X = sizex;
      SIZE_Y = sizey;
      pixels = new int[SIZE_X * SIZE_Y];
      setColor(color);
   }

   public Sprite(int sizex, int sizey, String path) {
      SIZE_X = sizex;
      SIZE_Y = sizey;
      this.path = path;
      pixels = new int[SIZE_X * SIZE_Y];
      loadFromPath();
   }

   public Sprite(Sprite sprite) {
      SIZE_X = sprite.SIZE_X;
      SIZE_Y = sprite.SIZE_Y;
      x = sprite.x;
      y = sprite.y;
      pixels = new int[sprite.pixels.length];
      System.arraycopy(sprite.pixels, 0, pixels, 0, sprite.pixels.length);
      sheet = sprite.sheet;
      path = sprite.path;
   }

   public Sprite(int sizex, int sizey, Sprite sprite) {
      SIZE_X = sizex;
      SIZE_Y = sizey;
      pixels = resizeSprite(sprite, sizex, sizey);
   }

   protected int[] resizeSprite(Sprite sprite, int sizex, int sizey) {
      int[] data = getData(sprite);
      int[] newData = new int[sizex * sizey * 4];
      double scalex = (double) sizex / (double) sprite.SIZE_X;
      double scaley = (double) sizey / (double) sprite.SIZE_Y;
      for (int y = 0; y < sprite.SIZE_Y * scaley; y++)
         for (int x = 0; x < sprite.SIZE_X * scalex; x++) {
            int pxl = y * sizex * 4 + x * 4;
            int near = (int) (y / scaley) * (int) (sizex / scalex) * 4 + (int) (x / scalex) * 4;
            for (int i = 0; i < 4; i++)
               newData[pxl + i] = data[near + i];
         }

      return getPixelsFromData(newData, sizex, sizey);
   }

   protected int[] getData(Sprite sprite) {
      int[] data = new int[sprite.SIZE_X * sprite.SIZE_Y * 4];
      for (int y = 0; y < sprite.SIZE_Y; y++)
         for (int x = 0; x < sprite.SIZE_X; x++) {
            int col = sprite.pixels[x + y * sprite.SIZE_X];
            Color c = new Color(col, true);
            int a = c.getAlpha();
            int r = c.getRed();
            int g = c.getGreen();
            int b = c.getBlue();
            data[y * 4 * sprite.SIZE_X + x * 4] = a;
            data[y * 4 * sprite.SIZE_X + x * 4 + 1] = r;
            data[y * 4 * sprite.SIZE_X + x * 4 + 2] = g;
            data[y * 4 * sprite.SIZE_X + x * 4 + 3] = b;
         }
      return data;
   }

   protected int[] getPixelsFromData(int[] data, int sizex, int sizey) {
      int[] pixels = new int[sizex * sizey];
      for (int y = 0; y < sizey; y++)
         for (int x = 0; x < sizex; x++)
            pixels[y * sizex + x] = (new Color(data[y * sizex * 4 + x * 4 + 1], data[y * sizex * 4 + x * 4 + 2], data[y * sizex * 4 + x * 4 + 3], data[y * sizex * 4 + x * 4]).hashCode());
      return pixels;
   }

   protected int[] getColorsFromData(int[] data, int sizex, int sizey) {
      ArrayList<Integer> colors = new ArrayList<Integer>();
      for (int y = 0; y < sizey; y++)
         for (int x = 0; x < sizex; x++) {
            int color = (new Color(data[y * sizex * 4 + x * 4 + 1], data[y * sizex * 4 + x * 4 + 2], data[y * sizex * 4 + x * 4 + 3], data[y * sizex * 4 + x * 4]).hashCode());
            if (!colors.contains(new Integer(color))) colors.add(new Integer(color));
         }
      int[] ret = new int[colors.size()];
      for (int k = 0; k < colors.size(); k++)
         ret[k] = colors.get(k);
      return ret;
   }

   protected int[][] getDensityColorsFromData(int[] data, int sizex, int sizey) {
      ArrayList<Integer> colors = new ArrayList<Integer>();
      ArrayList<Integer> density = new ArrayList<Integer>();
      for (int y = 0; y < sizey; y++)
         for (int x = 0; x < sizex; x++) {
            int color = (new Color(data[y * sizex * 4 + x * 4 + 1], data[y * sizex * 4 + x * 4 + 2], data[y * sizex * 4 + x * 4 + 3], data[y * sizex * 4 + x * 4]).hashCode());
            if (!colors.contains(new Integer(color))) {
               colors.add(new Integer(color));
               density.add(new Integer(1));
            } else
               density.set(colors.indexOf(new Integer(color)), new Integer(density.get(colors.indexOf(new Integer(color))).intValue() + 1));
         }
      int[][] ret = new int[2][colors.size()];
      for (int k = 0; k < colors.size(); k++) {
         ret[0][k] = colors.get(k);
         ret[1][k] = density.get(k);
      }
      return ret;
   }

   public int[] getColors() {
      return getColorsFromData(getData(this), SIZE_X, SIZE_Y);
   }

   public int[] getTopColors() {
      return getTopColors(-1, new int[] {}, 0, 0, false);
   }

   public int[] getTopColors(int freq) {
      return getTopColors(freq, new int[] {}, 0, 0, false);
   }

   public int[] getTopColors(int freq, int[] include) {
      return getTopColors(freq, include, 0, 0, false);
   }

   public int[] getTopColors(int freq, int[] include, int skip) {
      return getTopColors(freq, include, skip, 0, false);
   }

   public int[] getTopColors(int freq, int[] include, int skip, int alpha, boolean alphaIgnore) {
      int[] ret = null;
      int size = freq;
      if (freq != -1) ret = new int[freq];

      int[][] colorDensity = getDensityColorsFromData(getData(this), SIZE_X, SIZE_Y);
      ArrayList<Integer> density = new ArrayList<Integer>();
      for (int k = 0; k < colorDensity[1].length; k++)
         if (!alphaIgnore || colorDensity[0][k] != alpha) density.add(colorDensity[1][k]);
      ArrayList<Integer> colors = new ArrayList<Integer>();
      for (int k = 0; k < colorDensity[0].length; k++)
         if (!alphaIgnore || colorDensity[0][k] != alpha) colors.add(colorDensity[0][k]);

      if (freq == -1) {
         size = colorDensity[0].length;
         ret = new int[size];
      }

      Integer max = Collections.max(density);
      int i = density.indexOf(new Integer(max));
      ret[0] = colors.remove(i).intValue();
      density.remove(i);

      for (int n = 1; n <= include.length; n++) {
         if (n >= ret.length) return ret;
         ret[n] = include[n - 1];
         size--;
         if (size == 0) return ret;
      }

      for (int n = 0; n < skip; n++) {
         max = Collections.max(density);
         i = density.indexOf(new Integer(max));
         colors.remove(i).intValue();
         density.remove(i);
      }

      for (int n = include.length + 1; n < size; n++) {
         max = Collections.max(density);
         i = density.indexOf(new Integer(max));
         ret[n] = colors.remove(i).intValue();
         density.remove(i);
      }

      return ret;
   }

   public String[] getHexColors() {
      ArrayList<String> colors = new ArrayList<String>();
      for (int i : getColorsFromData(getData(this), SIZE_X, SIZE_Y))
         if (!colors.contains(Integer.toHexString(i))) colors.add(Integer.toHexString(i));

      return colors.toArray(new String[0]);
   }

   public String[] getTopHexColors(int freq) {
      String[] ret = new String[freq];

      int[][] colorDensity = getDensityColorsFromData(getData(this), SIZE_X, SIZE_Y);
      ArrayList<Integer> density = new ArrayList<Integer>();
      for (int k = 0; k < colorDensity[1].length; k++)
         density.add(colorDensity[1][k]);

      ArrayList<Integer> colors = new ArrayList<Integer>();
      for (int k = 0; k < colorDensity[0].length; k++)
         colors.add(colorDensity[0][k]);

      for (int n = 0; n < freq; n++) {
         Integer max = Collections.max(density);
         int i = density.indexOf(new Integer(max));
         ret[n] = Integer.toHexString(colors.remove(i).intValue());
         density.remove(i);
      }

      return ret;
   }

   public static Sprite changeColorSkin(Sprite s, int dr, int dg, int db, int[] exclude) {
      Sprite sprite = new Sprite(s.SIZE_X, s.SIZE_Y, 0);
      for (int i = 0; i < sprite.SIZE_X; i++) {
         for (int j = 0; j < sprite.SIZE_Y; j++) {

            Color color = new Color(s.pixels[i + j * sprite.SIZE_X], true);
            int r = (color.getRed() + dr);
            int g = (color.getGreen() + dg);
            int b = (color.getBlue() + db);
            if (r > 255) r = 255;
            if (r < 0) r = 0;
            if (g > 255) g = 255;
            if (g < 0) g = 0;
            if (b > 255) b = 255;
            if (b < 0) b = 0;

            int ncolor = new Color(r, g, b, color.getAlpha()).getRGB();

            boolean found = false;
            for (int x : exclude)
               if (x == color.getRGB()) found = true;

            if (!found)
               sprite.pixels[i + j * sprite.SIZE_X] = ncolor;
            else
               sprite.pixels[i + j * sprite.SIZE_X] = color.getRGB();
         }
      }
      return sprite;
   }

   public static Sprite changeColorSkin(Sprite s, int dr, int dg, int db) {
      Sprite sprite = new Sprite(s.SIZE_X, s.SIZE_Y, 0);
      for (int i = 0; i < sprite.SIZE_X; i++) {
         for (int j = 0; j < sprite.SIZE_Y; j++) {

            Color color = new Color(s.pixels[i + j * sprite.SIZE_X], true);
            int r = (color.getRed() + dr);
            int g = (color.getGreen() + dg);
            int b = (color.getBlue() + db);
            if (r > 255) r = 255;
            if (r < 0) r = 0;
            if (g > 255) g = 255;
            if (g < 0) g = 0;
            if (b > 255) b = 255;
            if (b < 0) b = 0;

            sprite.pixels[i + j * sprite.SIZE_X] = new Color(r, g, b, color.getAlpha()).getRGB();
         }
      }
      return sprite;
   }

   public static Sprite[] changeColorSkin(Sprite[] s, int dr, int dg, int db, int[] exclude) {
      Sprite[] sprite = new Sprite[s.length];
      for (int i = 0; i < sprite.length; i++) {
         sprite[i] = changeColorSkin(s[i], dr, dg, db, exclude);
      }
      return sprite;
   }

   public static Sprite[] changeColorSkin(Sprite[] s, int dr, int dg, int db) {
      Sprite[] sprite = new Sprite[s.length];
      for (int i = 0; i < sprite.length; i++) {
         sprite[i] = changeColorSkin(s[i], dr, dg, db);
      }
      return sprite;
   }

   public static Sprite[][] changeColorSkin(Sprite[][] s, int dr, int dg, int db, int[] exclude) {
      Sprite[][] sprite = new Sprite[s.length][s[0].length];
      for (int i = 0; i < sprite.length; i++) {
         for (int j = 0; j < sprite[0].length; j++) {
            sprite[i][j] = changeColorSkin(s[i][j], dr, dg, db, exclude);
         }
      }
      return sprite;
   }

   public static Sprite[][] changeColorSkin(Sprite[][] s, int dr, int dg, int db) {
      Sprite[][] sprite = new Sprite[s.length][s[0].length];
      for (int i = 0; i < sprite.length; i++) {
         for (int j = 0; j < sprite[0].length; j++) {
            sprite[i][j] = changeColorSkin(s[i][j], dr, dg, db);
         }
      }
      return sprite;
   }

   protected static Sprite rotateSprite(Sprite s, double angle) {
      Sprite sprite = new Sprite(s.SIZE_X, s.SIZE_Y, 0);
      sprite.pixels = rotate(s.pixels, s.SIZE_X, s.SIZE_Y, angle);
      return sprite;
   }

   protected static Sprite[] roll(Sprite sprite, int angleStep) {
      Sprite[] sprites = new Sprite[360 / angleStep];
      for (int k = 0; k < 360 / angleStep; k++) {
         sprites[k] = new Sprite(sprite.SIZE_X, sprite.SIZE_Y, 0);
         sprites[k].pixels = rotate(sprite.pixels, sprite.SIZE_X, sprite.SIZE_Y, angleStep * k);
      }
      return sprites;
   }

   protected static int[] rotate(int[] pixels, int width, int height, double angle) {
      int[] result = new int[width * height];

      double nx_x = rotX(-angle, 1.0, 0.0);
      double nx_y = rotY(-angle, 1.0, 0.0);
      double ny_x = rotX(-angle, 0.0, 1.0);
      double ny_y = rotY(-angle, 0.0, 1.0);

      double x0 = rotX(-angle, -width / 2.0, -height / 2.0) + width / 2.0;
      double y0 = rotY(-angle, -width / 2.0, -height / 2.0) + height / 2.0;

      for (int y = 0; y < height; y++) {
         double x1 = x0;
         double y1 = y0;
         for (int x = 0; x < width; x++) {
            int xx = (int) x1;
            int yy = (int) y1;
            int col = 0;
            if (xx < 0 || xx >= width || yy < 0 || yy >= height) {
               col = 0xffff00ff;
            } else {
               col = pixels[xx + yy * width];

            }
            result[x + y * width] = col;
            x1 += nx_x;
            y1 += nx_y;
         }
         x0 += ny_x;
         y0 += ny_y;
      }

      return result;
   }

   protected static double rotX(double angle, double x, double y) {
      double cos = Math.cos(angle);
      double sin = Math.sin(angle);
      return x * cos + y * -sin;
   }

   protected static double rotY(double angle, double x, double y) {
      double cos = Math.cos(angle);
      double sin = Math.sin(angle);
      return x * sin + y * cos;
   }

   protected static Sprite replaceColor(Sprite s, int colorOld, int colorNew) {
      Sprite sprite = new Sprite(s);
      for (int k = 0; k < sprite.pixels.length; k++)
         if (sprite.pixels[k] == colorOld) sprite.pixels[k] = colorNew;
      return sprite;
   }

   protected static Sprite replaceColors(Sprite s, int[] oldColors, int[] newColors) {
      Sprite sprite = new Sprite(s);
      for (int c = 0; c < oldColors.length; c++)
         if (c < newColors.length) for (int k = 0; k < sprite.pixels.length; k++)
            if (sprite.pixels[k] == oldColors[c]) sprite.pixels[k] = newColors[c];
      return sprite;
   }

   protected void setColor(int color) {
      for (int i = 0; i < SIZE_X * SIZE_Y; i++) {
         pixels[i] = color;
      }
   }

   protected void load() {
      for (int y = 0; y < SIZE_Y; y++) {
         for (int x = 0; x < SIZE_X; x++) {
            pixels[x + y * SIZE_X] = sheet.pixels[(x + this.x) + (y + this.y) * sheet.SIZE_X];
         }
      }
   }

   protected void loadFromPath() {
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
