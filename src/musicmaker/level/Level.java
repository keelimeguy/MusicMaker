package musicmaker.level;

import musicmaker.graphics.Screen;
import musicmaker.entity.Entity;
import musicmaker.MusicMaker;

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

   
   public void render(int xScroll, int yScroll, Screen screen) {

      // Tells the screen how much it is to be offset
      screen.setOffset(xScroll, yScroll);

      for (int i = 0; i < entities.size(); i++) {
         entities.get(i).render(screen);
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
