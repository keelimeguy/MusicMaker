package musicmaker.theory.instrument;

import musicmaker.theory.Note;
import musicmaker.theory.Chord;
import musicmaker.theory.Pitch;
import musicmaker.theory.PitchBank;

import java.util.Collections;
import java.util.ArrayList;
import java.lang.Integer;

public abstract class StringInstrument {
   private final int NUM_FRETS;
   private final int NUM_STRINGS;
   private Note[] tuning;
   private int[] octave;

   public StringInstrument (int frets, int strings, String[] tuning, int[] octave) {
      if (tuning.length != strings) {
         System.err.println("Warning: Length of tuning does not match number of strings in StringInstrument(..)");
         NUM_FRETS = 0;
         NUM_STRINGS = 0;
         return;
      }
      if (octave.length != strings) {
         System.err.println("Warning: Length of octave does not match number of strings in StringInstrument(..)");
         NUM_FRETS = 0;
         NUM_STRINGS = 0;
         return;
      }
      NUM_FRETS = frets;
      NUM_STRINGS = strings;
      this.tuning = new Note[strings];
      for (int i = 0; i < tuning.length; i++)
         this.tuning[i] = Note.get(tuning[i]);
      this.octave = new int[strings];
      for (int i = 0; i < octave.length; i++)
         this.octave[i] = octave[i];
   }

   public int getNumStrings() { return NUM_STRINGS; };

   public int getNumFrets() { return NUM_FRETS; };

   public int findOctave(int fret, int string) {
      if (string > NUM_STRINGS || string <= 0) {
         System.err.println("Warning: Variable ?<string>=\"" + string + "\" out of bounds in findOctave(..)");
         return 4;
      }
      if (fret > NUM_FRETS || fret < 0) {
         System.err.println("Warning: Variable ?<fret>=\"" + fret + "\" out of bounds in findOctave(..)");
         return 4;
      }
      int addOctaves = 0;
      Note note = tuning[string-1];
      for (int i = 0; i < fret; i ++) {
         note = note.halfStep(1);
         if (note.getValue() == 0) {
            addOctaves++;
         }
      }
      return octave[string-1]+addOctaves;
   }

   public Pitch getPitchOnFretOfString(int fret, int string) {
      if (string > NUM_STRINGS || string <= 0) {
         System.err.println("Warning: Variable ?<string>=\"" + string + "\" out of bounds in getNoteOnFretOfString(..)");
         return null;
      }
      if (fret > NUM_FRETS || fret < 0) {
         System.err.println("Warning: Variable ?<fret>=\"" + fret + "\" out of bounds in getNoteOnFretOfString(..)");
         return null;
      }
      return new Pitch(tuning[string-1].halfStep(fret), findOctave(fret, string));
   }

   public Note getNoteOnFretOfString(int fret, int string) {
      if (string > NUM_STRINGS || string <= 0) {
         System.err.println("Warning: Variable ?<string>=\"" + string + "\" out of bounds in getNoteOnFretOfString(..)");
         return null;
      }
      if (fret > NUM_FRETS || fret < 0) {
         System.err.println("Warning: Variable ?<fret>=\"" + fret + "\" out of bounds in getNoteOnFretOfString(..)");
         return null;
      }
      return tuning[string-1].halfStep(fret);
   }

   public ArrayList<Integer> findFretsOfNoteOnString(Note note, int string) {
      if (string > NUM_STRINGS || string <= 0) {
         System.err.println("Warning: Variable ?<string>=\"" + string + "\" out of bounds in findFretsOfNoteOnString(..)");
         return null;
      }
      ArrayList<Integer> frets = new ArrayList<Integer>();
      for (int fret = 0; fret <= NUM_FRETS; fret++)
         if (getNoteOnFretOfString(fret, string).normal() == note.normal())
            frets.add(new Integer(fret));
      return frets;
   }

   public ArrayList<Integer> findFretsOfPitchOnString(Pitch pitch, int string) {
      if (string > NUM_STRINGS || string <= 0) {
         System.err.println("Warning: Variable ?<string>=\"" + string + "\" out of bounds in findFretsOfPitchOnString(..)");
         return null;
      }
      ArrayList<Integer> frets = new ArrayList<Integer>();
      for (int fret = 0; fret <= NUM_FRETS; fret++) {
         Pitch target = getPitchOnFretOfString(fret, string);
         if (target.getNote().normal() == pitch.getNote().normal() && target.getOctave() == pitch.getOctave())
            frets.add(new Integer(fret));
      }
      return frets;
   }

   public ArrayList<Integer>[] findFretsForNotes(ArrayList<Note> notes) {
      if (notes == null) {
         System.err.println("Warning: ArrayList<Note> notes is null in findFretsForNotes(..)");
         return null;
      }
      ArrayList<Integer>[] frets = (ArrayList<Integer>[]) new ArrayList[NUM_STRINGS];
      for (Note note: notes)
         for (int string = 1; string <= NUM_STRINGS; string++) {
            if (frets[string-1] == null)
               frets[string-1] = new ArrayList<Integer>();
            frets[string-1].addAll(findFretsOfNoteOnString(note, string));
         }
      return frets;
   }

   public ArrayList<Integer>[] findFretsForPitches(ArrayList<Pitch> pitches) {
      if (pitches == null) {
         System.err.println("Warning: ArrayList<Pitch> pitches is null in findFretsForPitches(..)");
         return null;
      }
      ArrayList<Integer>[] frets = (ArrayList<Integer>[]) new ArrayList[NUM_STRINGS];
      for (Pitch pitch: pitches)
         for (int string = 1; string <= NUM_STRINGS; string++) {
            if (frets[string-1] == null)
               frets[string-1] = new ArrayList<Integer>();
            frets[string-1].addAll(findFretsOfPitchOnString(pitch, string));
         }
      return frets;
   }

   public ArrayList<Integer>[] findFretsForChord(Chord chord) {
      if (chord == null) return null;
      ArrayList<Note> notes = chord.getNotesList();
      return findFretsForNotes(notes);
   }

   public ArrayList<Integer>[] findOrderedFretsForNotes(ArrayList<Note> notes) {
      ArrayList<Integer>[] frets = findFretsForNotes(notes);
      for (int string = 1; string <= NUM_STRINGS; string++)
         if (frets[string-1] != null)
            Collections.sort(frets[string-1]);
      return frets;
   }

   public ArrayList<Integer>[] findOrderedFretsForPitches(ArrayList<Pitch> pitches) {
      ArrayList<Integer>[] frets = findFretsForPitches(pitches);
      for (int string = 1; string <= NUM_STRINGS; string++)
         if (frets[string-1] != null)
            Collections.sort(frets[string-1]);
      return frets;
   }

   public ArrayList<Integer>[] findOrderedFretsForChord(Chord chord) {
      if (chord == null) return null;
      ArrayList<Note> notes = chord.getNotesList();
      return findOrderedFretsForNotes(notes);
   }

   public ArrayList<Integer>[] findOrderedFretsForPitchBank(PitchBank pitchBank) {
      if (pitchBank == null) return null;
      ArrayList<Pitch> pitches = pitchBank.getPitchList();
      return findOrderedFretsForPitches(pitches);
   }
}