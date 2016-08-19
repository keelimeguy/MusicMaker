package musicmaker.level.entity.gui;

import musicmaker.graphics.Sprite;
import musicmaker.MusicMaker;

public class PlayButton extends Button {
   protected MusicMaker game;

   public PlayButton(int x, int y, int width, int height, int color, MusicMaker game) {
      super(x, y, width, height, color);
      setText("Play");
      this.game = game;
   }

   public PlayButton(int x, int y, Sprite sprite, MusicMaker game) {
      super(x, y, sprite);
      setText("Play");
      this.game = game;
   }

   public PlayButton(int x, int y, int width, int height, Sprite sprite, MusicMaker game) {
      super(x, y, width, height, sprite);
      setText("Play");
      this.game = game;
   }

   public void press() {
      game.startPlayer();
   }
}