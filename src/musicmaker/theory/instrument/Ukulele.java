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
      if (args.length != 1 && args.length != 2) {
         System.err.println("Usage: java Ukulele [type] <chord|note>\n\tw/ [type] = '-c', '-n', '-lc', or '-ln' (l for low g)");
         System.exit(-1);
      }

      boolean lowg = false;
      boolean ischord = true;
      if (args.length == 2) {
         if (args[0].equals("-lc")) {
            lowg = true;
         } else if (args[0].equals("-ln")) {
            lowg = true;
            ischord = false;
         } else if (args[0].equals("-n")) {
            ischord = false;
         }
      }

      Ukulele ukulele = new Ukulele(15, lowg);

      ArrayList<Integer>[] frets = null;
      String cur = "";

      if (ischord) {
         Chord chord = new Chord(args[args.length-1]);

         frets = ukulele.findOrderedFretsForChord(chord);

         if(frets.length != 4)
            System.exit(-1);

         cur = "Notes in chord:  ";
         for (Note note: chord.getNotesList())
            cur += note + ", ";
         cur = cur.substring(0, cur.length()-2);

      } else {
         ArrayList<Note> notes = new ArrayList<Note>();
         notes.add(Note.get(args[args.length-1]));

         frets = ukulele.findOrderedFretsForNotes(notes);

         if(frets.length != 4)
            System.exit(-1);

         cur = "Note:  " + args[args.length-1];
      }

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
