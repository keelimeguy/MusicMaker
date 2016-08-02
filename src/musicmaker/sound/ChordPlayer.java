package musicmaker.sound;

import musicmaker.sound.maxim.WavetableSynth;
import musicmaker.sound.maxim.Maxim;
import musicmaker.theory.Chord;
import musicmaker.theory.Pitch;

public class ChordPlayer {
   Maxim maxim;
   WavetableSynth[] waves;
   Chord chord;

   public ChordPlayer(Maxim maxim, Chord chord) {
      this.maxim = maxim;
      this.chord = chord;
      waves = new WavetableSynth[chord.size()];
      for (int i = 0; i< waves.length; i++) {
         waves[i] = maxim.createWavetableSynth(516);
         float freq = Pitch.findFreq(chord.getNote(i), 4);
         waves[i].setFrequency(freq);
      }
   }

   public void play() {
      for (WavetableSynth wave: waves)
         wave.play();
   }

   public void stop() {
      for (WavetableSynth wave: waves)
         wave.stop();
   }

   public void remove() {
      stop();
      for (WavetableSynth wave: waves)
         maxim.removeAudioPlayer(wave);
   }

   public Chord getChord() { return chord; }
}
