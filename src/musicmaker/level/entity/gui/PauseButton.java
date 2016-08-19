package musicmaker.level.entity.gui;

import musicmaker.graphics.Sprite;
import musicmaker.MusicMaker;

public class PauseButton extends Button {
   protected MusicMaker game;

   public PauseButton(int x, int y, int width, int height, int color, MusicMaker game) {
      super(x, y, width, height, color);
      setText("Pause");
      this.game = game;
   }

   public PauseButton(int x, int y, Sprite sprite, MusicMaker game) {
      super(x, y, sprite);
      setText("Pause");
      this.game = game;
   }

   public PauseButton(int x, int y, int width, int height, Sprite sprite, MusicMaker game) {
      super(x, y, width, height, sprite);
      setText("Pause");
      this.game = game;
   }

   public void press() {
      game.pausePlayer();
   }
}