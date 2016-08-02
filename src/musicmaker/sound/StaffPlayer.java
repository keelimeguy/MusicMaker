package musicmaker.sound;

import java.util.ArrayList;

import musicmaker.message.ISubscriber;
import musicmaker.message.Message;
import musicmaker.theory.Staff;
import musicmaker.theory.PitchBank;
import musicmaker.theory.Pitch;
import musicmaker.theory.Metronome;
import musicmaker.sound.maxim.WavetableSynth;
import musicmaker.sound.maxim.Maxim;

public class StaffPlayer implements ISubscriber {

   private Maxim maxim;
   private Staff staff;
   private int beatNumber;
   private Metronome metronome;
   private boolean looping, playing;
   ArrayList<WavetableSynth> waves;

   public StaffPlayer(Maxim maxim, Staff staff) {
      this.staff = staff;
      this.maxim = maxim;
      beatNumber = 0;
      metronome = null;
      looping = false;
      playing = false;
      waves = new ArrayList<WavetableSynth>();
   }

   public void init(Metronome ticker) {
      if (metronome != null)
         metronome.unsubscribe(this);
      metronome = ticker;
      metronome.subscribe(this);
   }

   public void start() {
      reset();
      play();
   }

   public void reset() {
      stop();
      beatNumber = 0;
   }

   public void stop() {
      if (metronome == null)
         return;
      metronome.stop();
      playing = false;
   }

   public void play() {
      if (playing)
         return;
      metronome.setDelay((long)(60000.0/staff.getTempo()));
      if (looping)
         metronome.start();
      else
         metronome.start(staff.numBeats());
      playing = true;
   }

   public void setLooping(boolean looping) {
      this.looping = looping;
   }

   public void setBeat(int beat) {
      beatNumber = beat;
   }

   public int getBeat() {
      return beatNumber;
   }

   public int getBeatsPerMeasure() {
      return staff.beatsPerMeasure();
   }

   public ArrayList<Pitch> getPitchListAtBeat(int beat) {
      PitchBank pitches = staff.getPitchBankAtBeat(beat);
      if (pitches != null)
         return pitches.getPitchList();
      return null;
   }

   @Override
   public void notify(Message message) {
      beatNumber++;
      if (looping && beatNumber > staff.numBeats())
         beatNumber = 1;
      PitchBank pitches = staff.getPitchBankAtBeat(beatNumber);
      if (pitches == null) {
         System.out.println("Warning: Could not find beat " + beatNumber + " in StaffPlayer");
         reset();
         return;
      }
      clearWaves();
      ArrayList<Pitch> pitchList = pitches.getPitchList();
      if (pitchList == null) {
         System.out.println("Warning: Could not retrieve pitchList in StaffPlayer");
         reset();
         return;
      }
      for (Pitch pitch: pitchList) {
         WavetableSynth wave = maxim.createWavetableSynth(516);
         wave.setFrequency(pitch.getFreq());
         waves.add(wave);
         wave.play();
      }
   }

   private void clearWaves() {
      for (WavetableSynth wave: waves) {
         wave.stop();
         maxim.removeAudioPlayer(wave);
      }
      waves.clear();
   }

   public void clear() {
      reset();
      clearWaves();
   }
}
