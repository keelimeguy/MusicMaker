package musicmaker.theory;

public class Scale {

   protected Note[] notes;
   protected int[] intervals;

   public enum Mode {
      // Selected preset modes for convenience. Note that most jazz scales
      // are defined (including as chord symbols) as alterations of the
      // Ionian scale.
      Ionian(new int[] { 0, 2, 4, 5, 7, 9, 11 }),
      Aeolian(new int[] {0, 2, 3, 5, 7, 8, 10}),
      HarmonicMinor(new int[] { 0, 2, 3, 5, 7, 8, 11 }),
      MelodicMinor(new int[] { 0, 2, 3, 5, 7, 9, 11 }),
      HarmonicMajor(new int[] { 0, 1, 4, 5, 7, 8, 11 }),
      WholeTone(new int[] { 0, 2, 4, 6, 8, 10 }),
      WHDiminished(new int[] { 0, 2, 3, 5, 6, 8, 9, 11 }),
      HWDiminished(new int[] { 0, 1, 3, 4, 6, 7, 9, 10 }),
      Pentatonic(new int[] { 0, 2, 4, 7, 9 }),
      Chromatic(new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 });

      protected final int[] intervals;

      Mode(int[] intervals) {
         this.intervals = intervals;
      }

      public int[] getIntervals() {
         return intervals;
      }
   }

   public Scale(Note root, int[] intervals) {
      // Make a scale with the given intervals, starting from root.
      // Intervals must include 0 (or 12) explicitly.
      // For example, to get a whole-half diminished scale, you could do:
      // [0, 2, 3, 5, 6, 8, 9, 11],
      // representing the same thing that

      // The beauty of not checking the parameters for "correctness" here
      // is that you can make multi-octave scales. This allows oddities
      // like arpeggios-as-scales, which is a bad idea, and Lydian #15, which
      // is the best idea.
      if (intervals.length > 0)
         if (intervals[0] != 0)
            System.out.println("Warning: scale created that does not begin at 0");
      notes = new Note[intervals.length];
      for (int i: intervals) {
         notes[i] = root.halfStep(i);
      }
      this.intervals = intervals;
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
}
