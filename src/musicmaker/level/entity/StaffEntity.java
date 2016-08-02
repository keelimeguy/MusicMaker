package musicmaker.entity;

import musicmaker.graphics.Screen;
import musicmaker.graphics.Sprite;

import java.util.Random;
import java.util.ArrayList;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.Color;

import musicmaker.sound.StaffPlayer;
import musicmaker.theory.Pitch;
import musicmaker.message.ISubscriber;
import musicmaker.message.Message;
import musicmaker.level.Level;
import musicmaker.MusicMaker;

public class StaffEntity extends Entity implements ISubscriber {

   StaffPlayer player;
   String cur = "0";

   public StaffEntity(int x, int y, StaffPlayer player) {
      super(x, y);
      this.player = player;
   }

   public void update(MusicMaker game) {
   }

   public void render(Graphics g) {
      g.setFont(new Font("Verdana", Font.BOLD, 15));
      g.setColor(Color.yellow);
      g.drawString("Notes Currently Playing: " + cur, 50, 100);
      // Yeah its awful but its temporary
      g.drawString("Beat: " + ((player.getBeat()%player.getBeatsPerMeasure()==0)?4:player.getBeat()%player.getBeatsPerMeasure()) + "   Measure: " + (((player.getBeat()-1)/player.getBeatsPerMeasure())+1), 50, 20);
   }

   public void notify(Message message) {
      ArrayList<Pitch> pitches = player.getPitchListAtBeat(player.getBeat());
      cur = "";
      if (pitches == null || pitches.isEmpty()) return;
      for (Pitch pitch: pitches) {
         cur+=pitch + ", ";
      }
      cur = cur.substring(0, cur.length()-2);
   }
}
