package musicmaker.theory;

import java.util.ArrayList;

public class PitchBank {
   public ArrayList<Pitch> pitches;

   public PitchBank() {
      pitches = new ArrayList<Pitch>();
   }

   public int size() { return pitches.size(); }

   public ArrayList<Pitch> getPitchList() { return pitches; }

   public void add(Pitch pitch) {
      pitches.add(pitch);
   }

   public int add(Chord chord, int octave) {
      ArrayList<Note> notes = chord.getNotesList();
      for (int i = 0; i< notes.size(); i++) {
         if (i > 0 && notes.get(i).compareTo(notes.get(i-1)) < 0)
            octave++;
         Pitch pitch = new Pitch(notes.get(i), octave);
         add(pitch);
      }
      return octave;
   }

   public void add(Chord chord) {
      add(chord, 4);
   }
}