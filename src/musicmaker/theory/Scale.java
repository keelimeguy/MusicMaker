package musicmaker.theory;

import musicmaker.sound.maxim.Maxim;
import musicmaker.sound.StaffPlayer;
import java.util.ArrayList;

public class Scale {

   protected Note[] notes;
   protected int[] intervals;

   public enum Mode {
      // Note that many scales, foreign especially, have conflicting descriptions online
      // Scales with differing ascending and descending lines are not supported

      Ionian("Ionian", new int[] { 0, 2, 4, 5, 7, 9, 11 }),
      Dorian("Dorian", new int[] { 0, 2, 3, 5, 7, 9, 10 }),
      Phrygian("Phrygian", new int[] { 0, 1, 3, 5, 7, 8, 10 }),
      Lydian("Lydian", new int[] { 0, 2, 4, 6, 7, 9, 11 }),
      Mixolydian("Mixolydian", new int[] { 0, 2, 4, 5, 7, 9, 10 }),
      Aeolian("Aeolian", new int[] {0, 2, 3, 5, 7, 8, 10}),
      Locrian("Locrian", new int[] { 0, 1, 3, 5, 6, 8, 10 }),

      Major("Major", new int[] { 0, 2, 4, 5, 7, 9, 11 }),
      NaturalMinor("NaturalMinor", new int[] {0, 2, 3, 5, 7, 8, 10 }),
      HarmonicMinor("HarmonicMinor", new int[] { 0, 2, 3, 5, 7, 8, 11 }),
      MelodicMinor("MelodicMinor", new int[] { 0, 2, 3, 5, 7, 9, 11 }),

      // Foreign scales from www.medianmusic.com/ScaleForeign.html
      Algerian("Algerian", new int[] { 0, 2, 3, 5, 6, 7, 8, 11 }),
      Arabian("Arabian", new int[] { 0, 2, 4, 5, 6, 8, 10 }),
      Balinese("Balinese", new int[] { 0, 1, 3, 7, 8 }),
      Byzantine("Byzantine", new int[] { 0, 1, 4, 5, 7, 8, 11 }),
      Egyptian("Egyptian", new int[] { 0, 2, 5, 7, 10 }),
      Ethiopian("Ethiopian", new int[] { 0, 2, 3, 5, 7, 8, 10 }),
      Hungarian("Hungarian", new int[] { 0, 3, 4, 6, 7, 9, 10 }),
      Israeli("Israeli", new int[] { 0, 1, 4, 5, 7, 8, 10 }),
      Japanese("Japanese", new int[] { 0, 1, 5, 7, 8 }),
      Javanese("Javanese", new int[] { 0, 1, 3, 5, 7, 9, 10 }),
      Mongolian("Mongolian", new int[] { 0, 2, 4, 7, 9 }),
      Persian("Persian", new int[] { 0, 1, 4, 5, 6, 8, 11 }),
      Spanish("Spanish", new int[] { 0, 1, 4, 5, 7, 8, 10 }),

      InSen("InSen", new int[] { 0, 1, 5, 7, 10 }),
      Yo("Yo", new int[] { 0, 2, 5, 7, 9 }),
      // naming of Hirajoshi modes from www.shredaholic.com/user48
      Hirajoshi("Hirajoshi", new int[] { 0, 2, 3, 7, 8 }),
      Iwato("Iwato", new int[] { 0, 1, 5, 6, 10 }),
      Kumoi("Kumoi", new int[] { 0, 4, 5, 9, 11 }),
      HonKumoi("HonKumoi", new int[] { 0, 1, 5, 7, 8 }),
      Chinese("Chinese", new int[] { 0, 4, 6, 7, 11 }),

      // Bebop scales from www.mattwarnockguitar.com/bebop-scale
      BebopDominant("BebopDominant", new int[] { 0, 2, 4, 5, 7, 9, 10, 11 }),
      BebopMinor("BebopMinor", new int[] { 0, 2, 3, 5, 7, 9, 10, 11 }),
      BebopMajor("BebopMajor", new int[] { 0, 2, 4, 5, 7, 8, 9, 11 }),
      BebopLydianDominant("BebopLydianDominant", new int[] { 0, 2, 4, 6, 7, 9, 10, 11 }),
      BebopAltered("BebopAltered", new int[] { 0, 1, 4, 5, 7, 8, 10, 11 }),
      BebopiiV("BebopiiV", new int[] { 0, 2, 4, 5, 6, 7, 9, 10, 11 }),
      BebopAllanHoldsworth("BebopAllanHoldsworth", new int[] { 0, 2, 3, 4, 5, 7, 9, 10, 11 }),

      HarmonicMajor("HarmonicMajor", new int[] { 0, 2, 4, 5, 7, 8, 11 }),
      DoubleHarmonic("DoubleHarmonic", new int[] { 0, 1, 4, 5, 7, 8, 11 }),
      Augmented("Augmented", new int[] { 0, 3, 4, 7, 8, 11 }),
      Acoustic("Acoustic", new int[] { 0, 2, 4, 6, 7, 9, 10 }),
      LydianAugmented("LydianAugmented", new int[] { 0, 2, 4, 6, 8, 9, 11 }),
      LydianDominant("LydianDominant", new int[] { 0, 2, 4, 6, 7, 9, 10 }),
      MajorBlues("MajorBlues", new int[] { 0, 2, 3, 4, 7, 9 }),
      MinorBlues("MinorBlues", new int[] { 0, 3, 5, 6, 7, 10 }),
      Neapolitan("Neapolitan", new int[] { 0, 1, 3, 5, 7, 9, 11 }),
      NeapolitanMinor("NeapolitanMinor", new int[] { 0, 1, 3, 5, 7, 8, 11 }),

      WholeTone("WholeTone", new int[] { 0, 2, 4, 6, 8, 10 }),
      TriTone("TriTone", new int[] { 0, 1, 4, 6, 7, 10 }),
      WHDiminished("WHDiminished", new int[] { 0, 2, 3, 5, 6, 8, 9, 11 }),
      HWDiminished("HWDiminished", new int[] { 0, 1, 3, 4, 6, 7, 9, 10 }),
      Pentatonic("Pentatonic", new int[] { 0, 2, 4, 7, 9 }),
      PentatonicMinor("PentatonicMinor", new int[] { 0, 2, 3, 7, 8 }),
      Chromatic("Chromatic", new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 });

      protected final int[] intervals;
      protected final String name;

      Mode(String name, int[] intervals) {
         this.intervals = intervals;
         this.name = name;
      }

      public int[] getIntervals() {
         return intervals;
      }

      public String getName() {
         return name;
      }

      public static Mode get(int ordinal) {
         if (ordinal >= 0 && ordinal < Mode.values().length)
            return Mode.values()[ordinal];
         return null;
      }

      public static Mode get(String name) {
         for (Mode mode: Mode.values())
            if (mode.getName().equals(name))
               return mode;
         return null;
      }

      public static String[] getValidNames() {
         String[] arr = new String[Mode.values().length];
         for (int i = 0; i < Mode.values().length; i++) {
            arr[i] = Mode.get(i).toString();
         }
         return arr;
      }

      public String toString() {
         return name;
      }
   }

   public Scale(Note root, int[] intervals) {
      // Make a scale with the given intervals, starting from root.
      // Intervals must include 0 (or 12) explicitly.
      // For example, to get a whole-half diminished scale, you could do:
      // [0, 2, 3, 5, 6, 8, 9, 11]

      // The beauty of not checking the parameters for "correctness" here
      // is that you can make multi-octave scales. This allows oddities
      // like arpeggios-as-scales, which is a bad idea, and Lydian #15, which
      // is the best idea.
      if (intervals.length > 0)
         if (intervals[0] != 0)
            System.out.println("Warning: scale created that does not begin at 0");
      notes = new Note[intervals.length];
      int j = 0;
      for (int i: intervals) {
         notes[j] = root.halfStep(i);
         j++;
      }
      this.intervals = intervals;
   }


   public ArrayList<Note> getNotes() {
      ArrayList<Note> ret = new ArrayList<Note>();
      for (int i = 0; i < notes.length; i ++)
         ret.add(notes[i]);
      return ret;
   }

   public Note getNote(int degree) {
      // Get the degree'th note of the scale. Per music theory, this is indexed
      // from 1. So for church modes (7 notes) the root is at 1, 8, 15, etc.
      // Behavior is undefined if (degree < 1)
      return notes[(degree - 1) % notes.length];
   }

   public void setNote(Note note, int degree) {
      // You might use this to call Note.asFlatStrong on each note in the
      // scale, for example.
      // You would also use it to build the scale corresponding to a
      // "Cmaj(b6/#9)" chord from an Ionian scale.
      // Like getNote, this is indexed from 1 according to musical conventions.
      notes[(degree - 1) % notes.length] = note;
   }

   public int numNotes() {
      return notes.length;
   }

   // Calculate needed halfsteps to reach given note of scale
   public int findStep (int notePos) {
      int degree = notePos;
      int step = 0;
      if (degree > notes.length) { // Adjust for octaves
         step = (notePos / (notes.length + 1)) * 12;
         degree = (notePos % (notes.length + 1)) + 1;
      }
      if (degree == notes.length + 1) {
         step += 12;
         degree = 1;
      }
      step += intervals[degree - 1];
      return step;
   }

   public String getNotesAsString() {
      String namesList = "\"";
      for (int i = 0; i < notes.length; i++) {
         namesList += "" + notes[i];
         if (i != notes.length - 1) namesList += "\", \"";
      }
      namesList += "\"";
      return namesList;
   }

   public static String getValidModesAsString() {
      String[] validNames = Mode.getValidNames();
      String namesList = "\"";
      for (int i = 0; i < validNames.length; i++) {
         namesList += validNames[i];
         if (i != validNames.length - 1) namesList += "\", \"";
      }
      namesList += "\"";
      return namesList;
   }

   public static void main(String[] args) {
      if (args.length != 2) {
         System.err.println("Usage: java Scale <key> <mode>");
         System.exit(-1);
      }

      Note key = Note.get(args[0]);
      if (key == null) {
         System.err.println("Error: Invalid key ?<key>=\"" + args[0] +"\"" +
            "\n\t<key> should match [A-G](##?|bb?)?");
         System.exit(-1);
      }

      if (Mode.get(args[1]) == null) {
         System.err.println("Error: Unsupported mode ?<mode>=\"" + args[1] +"\"" +
            "\n\tSupported modes are: " + Scale.getValidModesAsString());
         System.exit(-1);
      }

      Scale scale = new Scale(key, Mode.get(args[1]).getIntervals());

      Staff staff = new Staff(1, 1, 160);
      int octave = 4;
      // Creates an ascending then descending scale
      staff.add(new Pitch(scale.getNote(1), octave), 2);
      for (int i = 2; i <= scale.numNotes(); i++) {
         if (scale.getNote(i).compareTo(scale.getNote(i-1)) < 0)
            octave++;
         staff.add(new Pitch(scale.getNote(i), octave), 1);
      }
      if (scale.getNote(1).compareTo(scale.getNote(scale.numNotes())) < 0)
            octave++;
      staff.add(new Pitch(scale.getNote(1), octave), 2);
      for (int i = scale.numNotes(); i >= 2; i--) {
         if (scale.getNote(i).compareTo(scale.getNote(i+1)) > 0)
            octave--;
         staff.add(new Pitch(scale.getNote(i), octave), 1);
      }
      if (scale.getNote(1).compareTo(scale.getNote(2)) > 0)
            octave--;
      staff.add(new Pitch(scale.getNote(1), octave), 2);

      // Play the scale
      Maxim maxim = new Maxim();
      Metronome metronome = new Metronome();

      StaffPlayer staffPlayer = new StaffPlayer(maxim, staff);
      staffPlayer.init(metronome);
      staffPlayer.setLooping(false);
      staffPlayer.play();
      System.out.println("\n" + args[1] + " Scale in " + args[0] + ": " + scale.getNotesAsString());
      System.out.print("Currently playing: ");
      while (staffPlayer.isPlaying()) {
         int beat = staffPlayer.getBeatInMeasure();
         String next = "" + staff.getPitchBankAtBeat(staffPlayer.getBeat()) +
            "                    ";
         System.out.print(next);
         for (int i = 0; i < next.length(); i++)
            System.out.print("\b");
      }
      System.out.println("Done.");
      System.exit(0);
   }
}
