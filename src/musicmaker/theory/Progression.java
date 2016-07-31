package musicmaker.theory;

import java.util.ArrayList;

public class Progression {
   private static final String NAME_SEPARATOR = "->";
   private ArrayList<Chord> chords;
   private int pos;
   private boolean loop;

   public Progression() {
      chords = new ArrayList<Chord>();
      pos = 0;
      loop = true;
   }

   public Progression(boolean loop) {
      chords = new ArrayList<Chord>();
      pos = 0;
      this.loop = loop;
   }

   public void add(Chord chord) {
      chords.add(chord);
   }

   public void clear() {
      chords.clear();
   }

   private void nextPos() {
      pos++;
      if (pos >= length()) {
         if (loop)
            pos = 0;
         else
            pos = length();
      }
   }

   public void restart(boolean loop) {
      this.loop = loop;
      restart();
   }

   public void restart() {
      pos = 0;
   }

   public int length() { return chords.size(); }

   public Chord next() {
      Chord chord = null;
      if(pos >= 0 && pos < chords.size()) {
         chord = chords.get(pos);
         nextPos();
      }
      return chord;
   }

   public String toString() {
      String str = NAME_SEPARATOR;
      if (!chords.isEmpty()) {
         str = "";
         for (Chord chord: chords) {
            str+=chord + NAME_SEPARATOR;
         }
         str = str.substring(0, str.length() - NAME_SEPARATOR.length());
      }
      return str;
   }

   public void show() {
      System.out.println(this);
   }

   public static void main(String[] args) {
      if (args.length != 3) {
         System.err.println("Usage: java Progression <key> <start> <length>");
         System.exit(-1);
      }

      Note key = Note.get(args[0]);
      if (key == null) {
         System.err.println("Error: Invalid key ?<key>=\"" + args[0] +"\"" +
            "\n\t<key> should match [A-G][#b]?");
         System.exit(-1);
      }

      ProgressionMap progressionMap = new ProgressionMap(key);

      ProgressionMap.Position start = ProgressionMap.Position.get(args[1]);
      if (start == null) {
         System.err.println("Error: Invalid position id ?<start>=\"" + args[1] +"\"" +
            "\n\tValid positions are: " + progressionMap.getValidNamesAsString());
         System.exit(-1);
      }
      progressionMap.setStart(start);

      if (args[2].matches("[0-9]+")) {
         Progression progression = progressionMap.generate(Integer.parseInt(args[2]));
         progression.show();
      } else {
         System.err.println("Error: Invalid progression length ?<length>=\"" + args[2] +"\"" +
            "\n\t<length> must be a number greater than zero");
         System.exit(-1);
      }
   }
}