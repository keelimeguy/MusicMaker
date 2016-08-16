package musicmaker.entity;

import musicmaker.graphics.Screen;
import musicmaker.graphics.Sprite;

import java.util.Random;

import java.awt.Graphics;
import musicmaker.level.Level;
import musicmaker.MusicMaker;

public class Entity {

   protected int x, y;
   protected boolean removed = false;
   protected Level level;
   protected Sprite sprite;
   protected final Random random = new Random();

   public Entity(int x, int y) {
      this.x = x;
      this.y = y;
   }

   public void update(MusicMaker game) {
   }

   public void render(Screen screen) {
      if (sprite == null) return;
      // Offset the position to center the entity
      int xx = x - sprite.SIZE_X / 2;
      int yy = y - sprite.SIZE_Y / 2;

      // Render the entity sprite
      screen.renderSprite(sprite, xx, yy);
   }

   public void render(Graphics g) {
   }

   public void remove() {
      removed = true;
   }

   public int getX() {
      return x;
   }

   public int getY() {
      return y;
   }

   public boolean isRemoved() {
      return removed;
   }

   public void init(Level level) {
      this.level = level;
   }
}
