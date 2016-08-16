package musicmaker.theory.instrument;

import musicmaker.theory.Note;
import musicmaker.theory.Chord;

import java.util.Collections;
import java.util.ArrayList;
import java.lang.Integer;

public abstract class StringInstrument {
   private final int NUM_FRETS;
   private final int NUM_STRINGS;
   private Note[] tuning;

   public StringInstrument (int frets, int strings, String[] tuning) {
      if (tuning.length != strings) {
         System.err.println("Warning: Length of tuning does not match number of strings in StringIstrument(..)");
         NUM_FRETS = 0;
         NUM_STRINGS = 0;
         return;
      }
      NUM_FRETS = frets;
      NUM_STRINGS = strings;
      this.tuning = new Note[strings];
      for (int i = 0; i < tuning.length; i++)
         this.tuning[i] = Note.get(tuning[i]);
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

   public ArrayList<Integer>[] findFretsForNotes(ArrayList<Note> notes) {
      if (notes == null) {
         System.err.println("Warning: ArrayList<note> note is null in findFretsForNotes(..)");
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

   public ArrayList<Integer>[] findFretsForChord(Chord chord) {
      if (chord == null) return null;
      ArrayList<Integer>[] frets = (ArrayList<Integer>[]) new ArrayList[NUM_STRINGS];
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

   public ArrayList<Integer>[] findOrderedFretsForChord(Chord chord) {
      if (chord == null) return null;
      ArrayList<Integer>[] frets = (ArrayList<Integer>[]) new ArrayList[NUM_STRINGS];
      ArrayList<Note> notes = chord.getNotesList();
      return findOrderedFretsForNotes(notes);
   }
}