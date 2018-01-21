package musicmaker.theory.instrument;

import musicmaker.theory.Chord;
import musicmaker.theory.Note;
import java.lang.Integer;
import java.util.ArrayList;

public class Guitar extends StringInstrument{

   public Guitar (int frets) {
      super(frets, 6, new String[]{"E", "A", "D", "G", "B", "E"}, new int[] {3, 3, 4, 4, 4, 5});
   }

   public static void main(String[] args) {
      if (args.length != 1 && args.length != 2) {
         System.err.println("Usage: java Guitar [type] <chord|note>\n\tw/ [type] = '-c' or '-n'");
         System.exit(-1);
      }

      boolean ischord = !(args.length == 2 && args[0].equals("-n"));

      Guitar guitar = new Guitar(21);

      ArrayList<Integer>[] frets = null;
      String cur = "";

      if (ischord) {
         Chord chord = new Chord(args[args.length-1]);

         frets = guitar.findOrderedFretsForChord(chord);

         if(frets.length != 6)
            System.exit(-1);

         cur = "Notes in chord:  ";
         for (Note note: chord.getNotesList())
            cur += note + ", ";
         cur = cur.substring(0, cur.length()-2);

      } else {
         ArrayList<Note> notes = new ArrayList<Note>();
         notes.add(Note.get(args[args.length-1]));

         frets = guitar.findOrderedFretsForNotes(notes);

         if(frets.length != 6)
            System.exit(-1);

         cur = "Note:  " + args[args.length-1];
      }

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