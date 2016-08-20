package musicmaker.level.entity;

import musicmaker.graphics.Screen;
import musicmaker.graphics.Sprite;

import java.util.ArrayList;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.Color;

import musicmaker.theory.instrument.Ukulele;
import musicmaker.theory.Pitch;
import musicmaker.theory.Note;
import musicmaker.level.Level;
import musicmaker.MusicMaker;
import musicmaker.sound.StaffPlayer;
import musicmaker.message.ISubscriber;
import musicmaker.message.Message;

public class UkuleleEntity extends Entity implements ISubscriber {

   Ukulele ukulele;
   StaffPlayer player;
   ArrayList<Note> notes;
   ArrayList<Integer>[] frets;
   String cur = "0";

   public UkuleleEntity(int x, int y, Ukulele ukulele, StaffPlayer player) {
      super(x, y);
      this.ukulele = ukulele;
      this.player = player;
      notes = new ArrayList<Note>();
      frets = (ArrayList<Integer>[]) new ArrayList[4];
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
      g.drawString("Ukulele fingerings: " + cur, xx, yy);
      if (notes != null) {
         if(frets.length != 4) return;
         String[] strings = new String[4];
         for (int string = 0; string < 4; string++) {
            strings[string] = "";
            if(frets[string] != null)
               for (Integer fret: frets[string]) {
                  strings[string] += fret + "       ";
                  if (fret < 10) strings[string] += "  ";
               }
         }
         g.drawString("A: " + strings[3], xx, yy + 20);
         g.drawString("E: " + strings[2], xx, yy + 40);
         g.drawString("C: " + strings[1], xx, yy + 60);
         g.drawString("G: " + strings[0], xx, yy + 80);
      }
   }

   public void notify(Message message) {
      ArrayList<Pitch> pitches = player.getPitchListAtBeat(player.getBeat());
      cur = "";
      notes.clear();
      if (pitches == null || pitches.isEmpty()) return;
      for (Pitch pitch: pitches) {
         cur += pitch.getNote() + ", ";
         notes.add(pitch.getNote());
      }
      cur = cur.substring(0, cur.length()-2);
      frets = ukulele.findOrderedFretsForNotes(notes);
   }
}
