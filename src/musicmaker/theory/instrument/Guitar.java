package musicmaker.theory.instrument;

import musicmaker.theory.Chord;
import musicmaker.theory.Note;
import java.lang.Integer;
import java.util.ArrayList;

public class Guitar extends StringInstrument{

   public Guitar (int frets) {
      super(frets, 6, new String[]{"E", "A", "D", "G", "B", "E"});
   }

   public static void main(String[] args) {
      if (args.length != 1) {
         System.err.println("Usage: java Guitar <chord>");
         System.exit(-1);
      }
      Guitar guitar = new Guitar(21);

      Chord chord = new Chord(args[0]);

      ArrayList<Integer>[] frets = guitar.findOrderedFretsForChord(chord);
      if(frets.length != 6)
         System.exit(-1);

      String cur = "Notes in chord:  ";
      for (Note note: chord.getNotesList())
         cur += note + ", ";
      cur = cur.substring(0, cur.length()-2);

      String[] strings = new String[6];
      for (int string = 0; string < 6; string++) {
         strings[string] = "";
         if(frets[string] != null)
            for (Integer fret: frets[string]) {
               strings[string] += fret + "       ";
            }
      }
      System.out.println(cur);
      System.out.println("E: " + strings[5]);
      System.out.println("B: " + strings[4]);
      System.out.println("G: " + strings[3]);
      System.out.println("D: " + strings[2]);
      System.out.println("A: " + strings[1]);
      System.out.println("E: " + strings[0]);
   }
}