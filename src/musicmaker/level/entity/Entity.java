package musicmaker.level.entity;

import musicmaker.graphics.Screen;
import musicmaker.graphics.Sprite;
import musicmaker.input.Mouse;

import java.util.Random;

import java.awt.Graphics;
import musicmaker.level.Level;
import musicmaker.MusicMaker;

public class Entity {

   protected int x, y;
   protected boolean removed = false, clicked = false;
   protected Level level;
   protected Sprite sprite;
   protected final Random random = new Random();

   public Entity(int x, int y) {
      this.x = x;
      this.y = y;
   }

   public void update(MusicMaker game) {
      checkIfClicked(game.getWindowWidth(), game.getWindowHeight(), game.getScreen());
   }

   public void render(Screen screen) {
      if (sprite == null) return;
      // Offset the position to center the entity
      int xx = x;// - sprite.SIZE_X / 2;
      int yy = y;// - sprite.SIZE_Y / 2;

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

   public boolean isClicked() {
      return clicked;
   }

   protected void checkIfClicked(int height, int width, Screen screen) {
      if (Mouse.getB() < 0 || sprite == null || screen == null)
         return;
      if (Mouse.getX() > (x - screen.getXOffset() ) && Mouse.getX() < (sprite.SIZE_X + x - screen.getXOffset() ) && Mouse.getY() > (y - screen.getYOffset() ) && Mouse.getY() < (sprite.SIZE_Y - screen.getYOffset() + y))
         clicked = true;
      else
         clicked = false;
   }

   public void init(Level level) {
      this.level = level;
   }
}
