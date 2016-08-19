package musicmaker.level.entity.gui;

import musicmaker.graphics.Sprite;
import musicmaker.graphics.Screen;
import musicmaker.MusicMaker;

import java.awt.Graphics;
import java.awt.Font;
import java.awt.Color;

public class AdjusterButton extends Button {
   protected IncrementButton incrementButton;
   protected DecrementButton decrementButton;
   protected Type type;
   protected static final int ADJUST_WIDTH_HORIZ = 10;
   protected static final int ADJUST_WIDTH_VERT = 15;

   public static enum Type {
      HORIZONTAL, VERTICAL;
   }

   protected class IncrementButton extends Button {
      public IncrementButton(int x, int y, int width, int height, Sprite sprite) {
         super(x, y, width, height, sprite);
      }
   }

   protected class DecrementButton extends Button {
      public DecrementButton(int x, int y, int width, int height, Sprite sprite) {
         super(x, y, width, height, sprite);
      }
   }

   public AdjusterButton(int x, int y, int width, int height, int mainColor, int incColor, int decColor, Type type) {
      super(x, y, (type == Type.VERTICAL) ? (width - ADJUST_WIDTH_VERT - 1) : (width - 2 * ADJUST_WIDTH_HORIZ - 2), height, mainColor);
      if (type == Type.VERTICAL) {
         incrementButton = new IncrementButton(x + width - ADJUST_WIDTH_VERT, y, ADJUST_WIDTH_VERT, height/2 - 1, new Sprite(1, 1, incColor));
         decrementButton = new DecrementButton(x + width - ADJUST_WIDTH_VERT, y + height/2 + 1, ADJUST_WIDTH_VERT, height/2 - 1, new Sprite(1, 1, decColor));
      } else if (type == Type.HORIZONTAL) {
         incrementButton = new IncrementButton(x + width - ADJUST_WIDTH_HORIZ, y, ADJUST_WIDTH_HORIZ, height, new Sprite(1, 1, incColor));
         decrementButton = new DecrementButton(x + width - 2 * ADJUST_WIDTH_HORIZ - 1, y, ADJUST_WIDTH_HORIZ, height, new Sprite(1, 1, decColor));
      }
      this.type = type;
   }

   public AdjusterButton(int x, int y, int width, int height, Sprite mainSprite, Sprite incSprite, Sprite decSprite, Type type) {
      super(x, y, (type == Type.VERTICAL) ? (width - ADJUST_WIDTH_VERT - 1) : (width - 2 * ADJUST_WIDTH_HORIZ - 2), height, mainSprite);
      if (type == Type.VERTICAL) {
         incrementButton = new IncrementButton(x + width - ADJUST_WIDTH_VERT, y, ADJUST_WIDTH_VERT, height/2 - 1, incSprite);
         decrementButton = new DecrementButton(x + width - ADJUST_WIDTH_VERT, y + height/2 + 1, ADJUST_WIDTH_VERT, height/2 - 1, decSprite);
      } else if (type == Type.HORIZONTAL) {
         incrementButton = new IncrementButton(x + width - ADJUST_WIDTH_HORIZ, y, ADJUST_WIDTH_HORIZ, height, incSprite);
         decrementButton = new DecrementButton(x + width - 2 * ADJUST_WIDTH_HORIZ - 1, y, ADJUST_WIDTH_HORIZ, height, decSprite);
      }
      this.type = type;
   }

   public void press() {
   }

   public void update(MusicMaker game) {
      super.update(game);
      decrementButton.update(game);
      incrementButton.update(game);
   }

   public void render(Graphics g) {
      super.render(g);
      decrementButton.render(g);
      incrementButton.render(g);
   }

   public void render(Screen screen) {
      super.render(screen);
      decrementButton.render(screen);
      incrementButton.render(screen);
   }
}