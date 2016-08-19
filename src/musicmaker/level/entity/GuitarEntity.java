package musicmaker.level.entity;

import musicmaker.graphics.Screen;
import musicmaker.graphics.Sprite;

import java.util.ArrayList;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.Color;

import musicmaker.theory.instrument.Guitar;
import musicmaker.theory.Pitch;
import musicmaker.theory.Note;
import musicmaker.level.Level;
import musicmaker.MusicMaker;
import musicmaker.sound.StaffPlayer;
import musicmaker.message.ISubscriber;
import musicmaker.message.Message;

public class GuitarEntity extends Entity implements ISubscriber {

   Guitar guitar;
   StaffPlayer player;
   ArrayList<Note> notes;
   ArrayList<Integer>[] frets;
   String cur = "0";

   public GuitarEntity(int x, int y, Guitar guitar, StaffPlayer player) {
      super(x, y);
      this.guitar = guitar;
      this.player = player;
      notes = new ArrayList<Note>();
      frets = (ArrayList<Integer>[]) new ArrayList[6];
   }

   public void update(MusicMaker game) {
      super.update(game);
   }

   public void render(Graphics g) {
      g.setFont(new Font("Verdana", Font.BOLD, 15));
      g.setColor(Color.yellow);
      if (isClicked())
         g.setColor(Color.red);
      g.drawString("Guitar fingerings: " + cur, x, y);
      if (notes != null) {
         if(frets.length != 6) return;
         String[] strings = new String[6];
         for (int string = 0; string < 6; string++) {
            strings[string] = "";
            if(frets[string] != null)
               for (Integer fret: frets[string]) {
                  strings[string] += fret + "       ";
                  if (fret < 10) strings[string] += "  ";
               }
         }
         g.drawString("E: " + strings[5], x, y + 20);
         g.drawString("B: " + strings[4], x, y + 40);
         g.drawString("G: " + strings[3], x, y + 60);
         g.drawString("D: " + strings[2], x, y + 80);
         g.drawString("A: " + strings[1], x, y + 100);
         g.drawString("E: " + strings[0], x, y + 120);
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
      frets = guitar.findOrderedFretsForNotes(notes);
   }
}
