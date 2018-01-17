package musicmaker.theory.instrument;

import musicmaker.theory.Chord;
import musicmaker.theory.Note;
import java.lang.Integer;
import java.util.ArrayList;

public class Ukulele extends StringInstrument{

   public Ukulele (int frets, boolean lowG) {
      super(frets, 4, new String[]{"G", "C", "E", "A"}, new int[] {lowG?3:4, 4, 4, 4});
   }

   public static void main(String[] args) {
      if (args.length != 1) {
         System.err.println("Usage: java Ukulele <chord>");
         System.exit(-1);
      }
      Ukulele ukulele = new Ukulele(15, false);

      Chord chord = new Chord(args[0]);

      ArrayList<Integer>[] frets = ukulele.findOrderedFretsForChord(chord);
      if(frets.length != 4)
         System.exit(-1);

      String cur = "Notes in chord:  ";
      for (Note note: chord.getNotesList())
         cur += note + ", ";
      cur = cur.substring(0, cur.length()-2);

      String[] strings = new String[4];
      for (int string = 0; string < 4; string++) {
         strings[string] = "";
         if(frets[string] != null)
            for (Integer fret: frets[string]) {
               strings[string] += fret + "       ";
            }
      }
      System.out.println(cur);
      System.out.println("A: " + strings[3]);
      System.out.println("E: " + strings[2]);
      System.out.println("C: " + strings[1]);
      System.out.println("G: " + strings[0]);
   }
}