package musicmaker.level;

import musicmaker.graphics.Screen;
import musicmaker.level.entity.Entity;
import musicmaker.MusicMaker;

import java.awt.Graphics;
import java.awt.image.DataBufferInt;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Level {

   protected int width, height;

   protected List<Entity> entities = new ArrayList<Entity>();

   public Level(int width, int height) {
      this.width = width;
      this.height = height;
   }

   public List<Entity> getEntities() {
      return entities;
   }

   public int getWidth() {
      return width;
   }

   public int getHeight() {
      return height;
   }

   public void update(int xScroll, int yScroll, MusicMaker game) {
      for (int i = 0; i < entities.size(); i++) {
         entities.get(i).update(game);
      }
   }

   protected void time() {
   }

   public void render(int xScroll, int yScroll, MusicMaker game) {

      Screen screen = game.getScreen();

      // Tells the screen how much it is to be offset
      screen.setOffset(xScroll, yScroll);

      for (int i = 0; i < entities.size(); i++) {
         entities.get(i).render(screen);
      }

      BufferedImage image = new BufferedImage(screen.getWidth(), screen.getHeight(), BufferedImage.TYPE_INT_RGB);
      int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

      System.arraycopy(screen.getPixels(), 0, pixels, 0, pixels.length);

      Graphics g = image.getGraphics();

      render(xScroll, yScroll, g);

      g.dispose();

      System.arraycopy(pixels, 0, screen.getPixels(), 0, screen.getPixels().length);
   }

   public void render(int xOff, int yOff, Graphics g) {
      for (int i = 0; i < entities.size(); i++) {
         entities.get(i).render(xOff, yOff,  g);
      }
   }

   public void add(Entity e) {
      entities.add(e);
      e.init(this);
   }

   public void empty() {
      entities.clear();
   }
}
