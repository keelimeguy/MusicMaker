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
      int octave = 3;
      for (int i = 0; i< waves.length; i++) {
         waves[i] = maxim.createWavetableSynth(516);
         if (i > 0 && chord.getNote(i).compareTo(chord.getNote(i-1)) < 0) {
            //System.out.println(chord.getNote(i) + "<" + chord.getNote(i-1));
            octave++;
         }
         float freq = Pitch.findFreq(chord.getNote(i), octave);
         waves[i].setFrequency(freq);
      }
   }

   public void setVolume(float volume) {
      for (WavetableSynth wave: waves)
         wave.volume(volume);
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
