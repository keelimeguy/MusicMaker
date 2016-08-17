package musicmaker.theory;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class Staff {
   private int beatsPerMeasure;
   private int beatType;
   private int numBeats;
   private int tempo;

   private ArrayList<Integer> beats;
   private ArrayList<PitchBank> pitches;

   public Staff(int beatsPerMeasure, int beatType, int tempo) {
      this.beatsPerMeasure = beatsPerMeasure;
      this.beatType = beatType;
      this.tempo = tempo;
      numBeats = 0;
      beats = new ArrayList<Integer>();
      pitches = new ArrayList<PitchBank>();
   }

   public void setTempo(int tempo) {
      if (tempo <= 0) {
         System.out.println("Warning: Cannot set tempo to " + tempo + " in Staff" +
            "\n\tTempo must be a positive integer.");
         return;
      }
      this.tempo = tempo; // in BPM
   }

   public int getTempo() { return tempo; }

   public void add(PitchBank pitchBank, int beatLength) {
      beats.add(numBeats + 1);
      pitches.add(pitchBank);
      numBeats += beatLength;
   }

   public void add(Pitch pitch, int beatLength) {
      PitchBank pitchBank = new PitchBank();
      pitchBank.add(pitch);
      beats.add(numBeats + 1);
      pitches.add(pitchBank);
      numBeats += beatLength;
   }

   public int numBeats() { return numBeats; }
   public int beatsPerMeasure() { return beatsPerMeasure; }
   public int beatType() { return beatType; }

   public boolean isPitchRepeatedAtBeat(int beat) {
      if (beat <= 0 || beat > numBeats) return false;
      return getPitchBankAtBeat(beat).equals(getPitchBankAtBeat(beat-1));
   }

   public PitchBank getPitchBankAtBeat(int beat) {
      if (beats.size() != pitches.size()) {
         System.out.println("Warning: Sizes of pitches and beats are not synchronized in the Staff");
         return null;
      }
      PitchBank bank = null;
      if (beat > 0 && beat <= numBeats) {
         int index = -1;
         for (int i = 0; i < beats.size(); i++) {
            int next = ((Integer)beats.get(i)).intValue();
            // System.out.println("next: "+next);
            // System.out.println("Curbeat: "+beat);
            if (next <= beat)
               index = i;
         }
         if (index >= 0)
            bank = pitches.get(index);
      }
      return bank;
   }
}