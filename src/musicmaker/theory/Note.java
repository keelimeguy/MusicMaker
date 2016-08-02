package musicmaker.theory;

public enum Note {
   // Ordered so that flats have precedence over sharps (but not over naturals)
   C(0, "C"), BSHARP(0, "B#"), DFLATFLAT(0, "Dbb"), DFLAT(1, "Db"), CSHARP(1, "C#"), BSHARPSHARP(1, "B##"),
      D(2, "D"), EFLATFLAT(2, "Ebb"), CSHARPSHARP(2, "C##"), EFLAT(3, "Eb"), DSHARP(3, "D#"), FFLATFLAT(3, "Fbb"),
      E(4, "E"), FFLAT(4, "Fb"), DSHARPSHARP(4, "D##"), F(5, "F"), ESHARP(5, "E#"), GFLATFLAT(5, "Gbb"), GFLAT(6, "Gb"),
      FSHARP(6, "F#"), ESHARPSHARP(6, "E##"), G(7, "G"), AFLATFLAT(7, "Abb"), FSHARPSHARP(7, "F##"),
      AFLAT(8, "Ab"), GSHARP(8, "G#"), A(9, "A"), BFLATFLAT(9, "Bbb"), GSHARPSHARP(9, "G##"), BFLAT(10, "Bb"),
      ASHARP(10, "A#"), CFLATFLAT(10, "Cbb"), B(11, "B"), CFLAT(11, "Cb"), ASHARPSHARP(11, "A##");

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

   // public Note natural() {
   //    return null;
   // }

   public Note doubleSharp() {
       return halfStep(2);
   }

   // Return first note of the same value (to clean odd cases e.g. B# -> C)
   public Note normal() {
      return flat().sharp();
   }

   // Return first note of a given number of halfsteps away
   public Note halfStep(int halfSteps) {
      return get(val + halfSteps);
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
       switch (this) {
       case AFLAT:
	   return GSHARP;
       case BFLAT:
	   return ASHARP;
       case CFLAT:
	   return B;
       case DFLAT:
	   return CSHARP;
       case EFLAT:
	   return DSHARP;
       default:
	   return this;
       }
   }

   public Note asSharpStrong() {
       Note asw = asSharpWeak();
       if (asw != this) {
	   return asw;
       }
       else {
	   switch (this) {
	   case C:
	       return BSHARP;
	   case F:
	       return ESHARP;
	   default:
	       return this;
	   }
       }
   }

   public Note asFlatWeak() {
       // Changing this may change asFlatStrong()
       switch (this) {
       case ASHARP:
	   return BFLAT;
       case BSHARP:
	   return C;
       case CSHARP:
	   return DFLAT;
       case DSHARP:
	   return EFLAT;
       case ESHARP:
	   return F;
       case FSHARP:
	   return GFLAT;
       case GSHARP:
	   return AFLAT;
       default:
	   return this;
       }
   }

   public Note asFlatStrong() {
       Note afw = asFlatWeak();
       if (afw != this) {
	   return afw;
       }
       else {
	   switch (this) {
	   case B:
	       return CFLAT;
	   case E:
	       return FFLAT;
	   default:
	       return this;
	   }
       }
   }
