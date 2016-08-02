package musicmaker.theory;

public enum Note {
   // Ordered so that flats have precedence over sharps
   A(0, "A"), BFLATFLAT(0, "Bbb"), GSHARPSHARP(0, "G##"), BFLAT(1, "Bb"), ASHARP(1, "A#"), CFLATFLAT(1, "Cbb"),
      B(2, "B"), CFLAT(2, "Cb"), ASHARPSHARP(2, "A##"), C(3, "C"), BSHARP(3, "B#"), DFLATFLAT(3, "Dbb"),
      DFLAT(4, "Db"), CSHARP(4, "C#"), BSHARPSHARP(4, "B##"), D(5, "D"), EFLATFLAT(5, "Ebb"), CSHARPSHARP(5, "C##"),
      EFLAT(6, "Eb"), DSHARP(6, "D#"), FFLATFLAT(6, "Fbb"), E(7, "E"), FFLAT(7, "Fb"), DSHARPSHARP(7, "D##"),
      F(8, "F"), ESHARP(8, "E#"), GFLATFLAT(8, "Gbb"), GFLAT(9, "Gb"), FSHARP(9, "F#"), ESHARPSHARP(9, "E##"),
      G(10, "G"), AFLATFLAT(10, "Abb"), FSHARPSHARP(10, "F##"), AFLAT(11, "Ab"), GSHARP(11, "G#");

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

   public Note doubleFlat() {
       return halfStep(-2);
   }

   public Note sharp() {
      return halfStep(1);
   }

   public Note doubleSharp() {
       return halfStep(2);
   }

   public Note flatflat() {
      return halfStep(-2);
   }

   public Note sharpsharp() {
      return halfStep(2);
   }

   // public Note natural() {
   //    return null;
   // }

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

   public int getValue() { return val; }

   public String getName() { return name; }

   public String toString() { return name; }

   // The following switch statements seem fine for now.
   // It's possible they'll get way uglier if double-sharpness

   public Note asSharpWeak() {
       // Changing this may change asSharpStrong()
       switch (get(this.getValue())) {
       case BFLAT:
	   return ASHARP;
       case DFLAT:
	   return CSHARP;
       case EFLAT:
	   return DSHARP;
       case GFLAT:
	   return FSHARP;
       case AFLAT:
	   return GSHARP;
       default:
	   return get(this.getValue());
       }

   }

   public Note asSharpStrong() {
       Note asw = asSharpWeak();
       if (asw != this) {
	   return asw.asSharpStrong(); // This should only recur once.
       }
       else {
	   switch (get(this.getValue())) {
	   case A:
	       return GSHARPSHARP;
	   case B:
	       return ASHARPSHARP;
	   case C:
	       return BSHARP;
	   case D:
	       return CSHARPSHARP;
	   case E:
	       return DSHARPSHARP;
	   case F:
	       return ESHARP;
	   case G:
	       return FSHARPSHARP;
	   default:
	       return this; // should never happen
	       // TODO: put a way to detect this case when debugging.
	   }
       }
   }

   public Note asFlatWeak() {
       // Changing this may change asFlatStrong()
       // Note that this depends on the flat-preferring behavior of normal().
       return this.normal();
   }

   public Note asFlatStrong() {
       Note afw = asFlatWeak();
       if (afw != this) {
	   return afw.asFlatStrong(); // Will only recur once.
       }
       else {
	   switch (this) {
	   case A:
	       return BFLATFLAT;
	   case B:
	       return CFLAT;
	   case C:
	       return DFLATFLAT;
	   case D:
	       return EFLATFLAT;
	   case E:
	       return FFLAT;
	   case F:
	       return GFLATFLAT;
	   case G:
	       return AFLATFLAT;
	   default:
	       return this; // should never happen
	       // TODO: put in a way to detect this case when debugging.
	   }
       }
   }
}
