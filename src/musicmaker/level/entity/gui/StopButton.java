package musicmaker.level.entity.gui;

import musicmaker.graphics.Sprite;
import musicmaker.MusicMaker;

public class StopButton extends Button {
   protected MusicMaker game;

   public StopButton(int x, int y, int width, int height, int color, MusicMaker game) {
      super(x, y, width, height, color);
      setText("Stop");
      this.game = game;
   }

   public StopButton(int x, int y, Sprite sprite, MusicMaker game) {
      super(x, y, sprite);
      setText("Stop");
      this.game = game;
   }

   public StopButton(int x, int y, int width, int height, Sprite sprite, MusicMaker game) {
      super(x, y, width, height, sprite);
      setText("Stop");
      this.game = game;
   }

   public void press() {
      game.stopPlayer();
   }
}