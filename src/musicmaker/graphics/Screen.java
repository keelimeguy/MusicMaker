package musicmaker.graphics;

public class Screen {

   private int width, height;
   private int[] pixels;

   private int xOffset, yOffset;

   public Screen(int width, int height) {
      this.width = width;
      this.height = height;
      pixels = new int[width * height];
   }

   public void clear(int color) {
      for (int i = 0; i < pixels.length; i++) {
         pixels[i] = color;
      }
   }

   public int getWidth() {
      return width;
   }

   public int getHeight() {
      return height;
   }

   public int[] getPixels() {
      return pixels;
   }

   public int getXOffset() {
      return xOffset;
   }

   public int getYOffset() {
      return yOffset;
   }

   public void renderSprite(Sprite sprite, int xp, int yp) {
      if (sprite == null) return;
      xp -= xOffset;
      yp -= yOffset;

      for (int y = 0; y < sprite.SIZE_Y; y++) {
         int ya = y + yp;
         for (int x = 0; x < sprite.SIZE_X; x++) {
            int xa = x + xp;
            if (xa >= width || ya < 0 || ya >= height) break;
            if (xa < 0) continue;
            setPixel(sprite.pixels[x + y * sprite.SIZE_X], xa, ya);
         }
      }
   }

   public void setPixel(int col, int xa, int ya) {
      // If the color is 0xFF00FF don't render that pixel
      if (xa + ya * width >= pixels.length || xa + ya * width < 0) return;
      if (col != 0xffff00ff && (col | 0x11ffffff) == 0xffffffff)
         pixels[xa + ya * width] = col;

      else if (col != 0xffff00ff && (col | 0x00ffffff) != 0x00ffffff) {

         float alpha = (col & 0xff000000) | 0x11000000;
         float r = (col & 0xff0000) | 0x110000;
         float g = (col & 0xff00) | 0x1100;
         float b = (col & 0xff) | 0x11;

         float oldcol = pixels[xa + ya * width];
         float oldr = ((int) oldcol & 0xff0000) | 0x110000;
         float oldg = ((int) oldcol & 0xff00) | 0x1100;
         float oldb = ((int) oldcol & 0xff) | 0x11;

         alpha = ((int) alpha >> 24) & 0xff;
         r = (int) r >> 16;
         g = (int) g >> 8;
         oldr = (int) oldr >> 16;
         oldg = (int) oldg >> 8;

         alpha /= 255;

         //System.out.println(alpha + ", " + Integer.toHexString((int) r) + ", " + Integer.toHexString((int) g) + ", " + Integer.toHexString((int) b));

         int newr = (int) (alpha * r + (1 - alpha) * oldr);
         int newg = (int) (alpha * g + (1 - alpha) * oldg);
         int newb = (int) (alpha * b + (1 - alpha) * oldb);

         //System.out.println(Integer.toHexString((int) newr) + ", " + Integer.toHexString((int) newg) + ", " + Integer.toHexString((int) newb));

         pixels[xa + ya * width] = 0xff000000 | ((newr << 16)) | ((newg << 8)) | ((newb));

      }
   }

   public void setOffset(int xOffset, int yOffset) {
      this.xOffset = xOffset;
      this.yOffset = yOffset;
   }
}
