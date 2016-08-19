package musicmaker.level.entity.gui;

import musicmaker.graphics.Sprite;
import musicmaker.MusicMaker;

public class ResetButton extends Button {
   protected MusicMaker game;

   public ResetButton(int x, int y, int width, int height, int color, MusicMaker game) {
      super(x, y, width, height, color);
      setText("Reset");
      this.game = game;
   }

   public ResetButton(int x, int y, Sprite sprite, MusicMaker game) {
      super(x, y, sprite);
      setText("Reset");
      this.game = game;
   }

   public ResetButton(int x, int y, int width, int height, Sprite sprite, MusicMaker game) {
      super(x, y, width, height, sprite);
      setText("Reset");
      this.game = game;
   }

   public void press() {
      game.restoreDefault();
      game.define();
   }
}