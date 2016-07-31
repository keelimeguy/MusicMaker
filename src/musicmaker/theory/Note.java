package musicmaker.theory;

public enum Note {
   // Ordered so that flats have precedence over sharps
   A(0, "A"), BFLAT(1, "Bb"), ASHARP(1, "A#"), B(2, "B"), CFLAT(2, "Cb"), C(3, "C"), BSHARP(3, "B#"), DFLAT(4, "Db"), CSHARP(4, "C#"), D(5, "D"), EFLAT(6, "Eb"),
    DSHARP(6, "D#"), E(7, "E"), FFLAT(7, "Fb"), F(8, "F"), ESHARP(8, "E#"), GFLAT(9, "Gb"), FSHARP(9, "F#"), G(10, "G"), AFLAT(11, "Ab"), GSHARP(11, "G#");

   public static final int NUM_NOTES = 12;
   private final int val;
   private final String name;

   private Note(int val, String name) {
      this.val = val;
      this.name = name;
   }

   public Note flat() {
      return halfStep(-1);
   }

   public Note sharp() {
      return halfStep(1);
   }

   // Return first note of the same value (to clean odd cases e.g. B# -> C)
   public Note normal() {
      return flat().sharp();
   }

   // Return first note of a given number of halfsteps away
   public Note halfStep(int halfSteps) {
      return get(this.val + halfSteps);
   }

   // Return first note of the given value
   public static Note get(int i) {
      int val = i % NUM_NOTES;
      if (i < 0 && val != 0) // Adjust for negative halfstep
         val += NUM_NOTES;
      for (Note note: Note.values())
         if (note.val == val)
            return note;
      return null;
   }

   // Return note of given name
   public static Note get(String name) {
      for (Note note: Note.values())
         if (note.name.equals(name))
            return note;
      return null;
   }

   public String toString() { return name; }
}