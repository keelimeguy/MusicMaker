package musicmaker.level.entity.gui;

import musicmaker.graphics.Sprite;
import musicmaker.theory.Note;
import musicmaker.graphics.Screen;
import musicmaker.MusicMaker;

import java.awt.Graphics;
import java.awt.Font;
import java.awt.Color;

public class BeatAdjuster extends AdjusterButton {
   protected MusicMaker game;
   protected BeatIncrementButton incrementButton;
   protected BeatDecrementButton decrementButton;

   protected class BeatIncrementButton extends AdjusterButton.IncrementButton {
      protected MusicMaker game;

      public BeatIncrementButton(int x, int y, int width, int height, Sprite sprite, MusicMaker game) {
         super(x, y, width, height, sprite);
         this.game = game;
      }

      public void press() {
         game.nextBeat();
      }
   }

   protected class BeatDecrementButton extends AdjusterButton.DecrementButton {
      protected MusicMaker game;

      public BeatDecrementButton(int x, int y, int width, int height, Sprite sprite, MusicMaker game) {
         super(x, y, width, height, sprite);
         this.game = game;
      }

      public void press() {
         game.prevBeat();
      }
   }

   public BeatAdjuster(int x, int y, int width, int height, int mainColor, int incColor, int decColor, Type type, MusicMaker game) {
      super(x, y, width, height, mainColor, incColor, decColor, type);
      if (type == Type.VERTICAL) {
         incrementButton = new BeatIncrementButton(x + width - ADJUST_WIDTH_VERT, y, ADJUST_WIDTH_VERT, height/2 - 1, new Sprite(1, 1, incColor), game);
         decrementButton = new BeatDecrementButton(x + width - ADJUST_WIDTH_VERT, y + height/2 + 1, ADJUST_WIDTH_VERT, height/2 - 1, new Sprite(1, 1, decColor), game);
      } else if (type == Type.HORIZONTAL) {
         incrementButton = new BeatIncrementButton(x + width - ADJUST_WIDTH_HORIZ, y, ADJUST_WIDTH_HORIZ, height, new Sprite(1, 1, incColor), game);
         decrementButton = new BeatDecrementButton(x + width - 2 * ADJUST_WIDTH_HORIZ - 1, y, ADJUST_WIDTH_HORIZ, height, new Sprite(1, 1, decColor), game);
      }
      this.type = type;
      this.game = game;
   }

   public BeatAdjuster(int x, int y, int width, int height, Sprite mainSprite, Sprite incSprite, Sprite decSprite, Type type, MusicMaker game) {
      super(x, y, width, height, mainSprite, incSprite, decSprite, type);
      if (type == Type.VERTICAL) {
         incrementButton = new BeatIncrementButton(x + width - ADJUST_WIDTH_VERT, y, ADJUST_WIDTH_VERT, height/2 - 1, incSprite, game);
         decrementButton = new BeatDecrementButton(x + width - ADJUST_WIDTH_VERT, y + height/2 + 1, ADJUST_WIDTH_VERT, height/2 - 1, decSprite, game);
      } else if (type == Type.HORIZONTAL) {
         incrementButton = new BeatIncrementButton(x + width - ADJUST_WIDTH_HORIZ, y, ADJUST_WIDTH_HORIZ, height, incSprite, game);
         decrementButton = new BeatDecrementButton(x + width - 2 * ADJUST_WIDTH_HORIZ - 1, y, ADJUST_WIDTH_HORIZ, height, decSprite, game);
      }
      this.type = type;
      this.game = game;
   }

   public void press() {
      game.playCurBeat();
   }

   public void update(MusicMaker game) {
      super.update(game);
      incrementButton.update(game);
      decrementButton.update(game);
      setText("Play Beat");
   }

   public void render(Graphics g) {
      super.render(g);
   }

   public void render(Screen screen) {
      super.render(screen);
   }
}
