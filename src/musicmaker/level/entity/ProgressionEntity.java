package musicmaker.level.entity;

import musicmaker.graphics.Screen;
import musicmaker.graphics.Sprite;

import java.awt.Graphics;
import java.awt.Font;
import java.awt.Color;

import musicmaker.theory.Progression;
import musicmaker.MusicMaker;
import musicmaker.sound.StaffPlayer;
import musicmaker.message.ISubscriber;
import musicmaker.message.Message;

public class ProgressionEntity extends Entity implements ISubscriber {

   Progression progression;
   StaffPlayer player;
   String cur = "0";

   public ProgressionEntity(int x, int y, Progression progression, StaffPlayer player) {
      super(x, y);
      this.progression = progression;
      this.player = player;
   }

   public void update(MusicMaker game) {
      super.update(game);
   }

   public void render(int xOff, int yOff, Graphics g) {
      int xx = x - xOff;
      int yy = y - yOff;
      g.setFont(new Font("Verdana", Font.BOLD, 15));
      g.setColor(Color.yellow);
      if (isClicked())
         g.setColor(Color.red);
      g.drawString(progression + "", xx, yy);
      g.drawString("Playing: " + cur, xx, yy + 20);
   }

   public void notify(Message message) {
      cur = "" + progression.get(player.getMeasure() - 1);
   }
}
