package musicmaker.theory.instrument;

import musicmaker.theory.Chord;
import musicmaker.theory.Note;
import musicmaker.theory.Scale;
import java.lang.Integer;
import java.util.ArrayList;

public class Guitar extends StringInstrument{

   public Guitar (int frets) {
      super(frets, 6, new String[]{"E", "A", "D", "G", "B", "E"}, new int[] {3, 3, 4, 4, 4, 5});
   }

   public static void main(String[] args) {
      if (args.length != 1 && args.length != 2) {
         System.err.println("Usage: java Guitar [type] <scale(as Note-Type)|chord|note>\n\tw/ [type] = '-s', '-c', or '-n'");
         System.exit(-1);
      }

      int type = 1; // 0=scale, 1=chord, 2=note
      if (args.length == 2 && args[0].equals("-n")) {
         type = 2;
      } else if (args.length == 2 && args[0].equals("-s")) {
         type = 0;
      }

      Guitar guitar = new Guitar(21);

      ArrayList<Integer>[] frets = null;
      String cur = "";

      if (type == 0) {
         if (args[args.length-1].split("-").length != 2) {
            System.err.println("Error: Invalid scale ?<scale>=\"" + (args[args.length-1]) +"\"" +
               "\n\t<Scale> should match [A-G](##?|bb?)?-<Mode>");
            System.exit(-1);
         }
         Note key = Note.get(args[args.length-1].split("-")[0]);
         String mode = args[args.length-1].split("-")[1];
         if (key == null) {
            System.err.println("Error: Invalid key ?<key>=\"" + (args[args.length-1].split("-")[0]) +"\"" +
               "\n\t<key> should match [A-G](##?|bb?)?");
            System.exit(-1);
         }

         if (Scale.Mode.get(mode) == null) {
            System.err.println("Error: Unsupported mode ?<mode>=\"" + mode +"\"" +
               "\n\tSupported modes are: " + Scale.getValidModesAsString());
            System.exit(-1);
         }

         Scale scale = new Scale(key, Scale.Mode.get(mode).getIntervals());
         frets = guitar.findOrderedFretsForNotes(scale.getNotes());

         if(frets.length != 6)
            System.exit(-1);

         cur = "Notes in scale:  " + scale.getNotesAsString();

      } else if (type == 1) {
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
               strings[string] += fret + "     ";
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