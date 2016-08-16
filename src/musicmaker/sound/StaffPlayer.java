package musicmaker.sound;

import java.util.ArrayList;
import java.lang.Integer;

import musicmaker.message.ISubscriber;
import musicmaker.message.Message;
import musicmaker.theory.Staff;
import musicmaker.theory.PitchBank;
import musicmaker.theory.Pitch;
import musicmaker.theory.Chord;
import musicmaker.theory.Metronome;
import musicmaker.sound.maxim.WavetableSynth;
import musicmaker.sound.maxim.Maxim;

public class StaffPlayer implements ISubscriber {

   private Maxim maxim;
   private Staff staff;
   private PitchBank pitchesNowPlaying;
   private int beatNumber;
   private Metronome metronome;
   private boolean looping, playing;
   ArrayList<WavetableSynth> waves;

   public StaffPlayer(Maxim maxim, Staff staff) {
      this.staff = staff;
      this.maxim = maxim;
      beatNumber = 0;
      metronome = null;
      pitchesNowPlaying = null;
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

   public void pause() {
      if (metronome == null)
         return;
      metronome.stop();
      playing = false;
   }

   public void stop() {
      pause();
      pitchesNowPlaying = null;
      if (waves != null)
         for (WavetableSynth wave: waves)
            wave.stop();
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

   public boolean isPlaying() {
      return playing;
   }

   public int getBeat() {
      return beatNumber;
   }

   public int numMeasures() {
      int beat = beatNumber;
      beatNumber = staff.numBeats();
      int measure = getMeasure();
      beatNumber = beat;
      return measure;
   }

   public void nextMeasure() {
      if (numMeasures() <= 1) {
         if (beatNumber != 0)
            beatNumber = 1;
         return;
      }

      int measure = getMeasure();
      while (getMeasure() == measure && beatNumber <= staff.numBeats())
         beatNumber++;

      if (beatNumber > staff.numBeats())
         beatNumber = 1;
   }

   public void prevMeasure() {
      if (numMeasures() <= 1) {
         if (beatNumber != 0)
            beatNumber = 1;
         return;
      }
      int measure = getMeasure();
      while (getMeasure() == measure && beatNumber > 0)
         beatNumber--;

      if (beatNumber <= 0)
         beatNumber = staff.numBeats();
      resetMeasure();
   }

   public void resetMeasure() {
      if (numMeasures() <= 1) {
         if (beatNumber != 0)
            beatNumber = 1;
         return;
      }
      int measure = getMeasure();
      while (getMeasure() == measure && beatNumber > 0)
         beatNumber--;

      if (beatNumber <= 0) {
         beatNumber = 1;
      }
      else
         beatNumber ++;
   }

   public void nextBeat() {
      beatNumber++;
      if (beatNumber > staff.numBeats())
         beatNumber = 1;
   }

   public void prevBeat() {
      beatNumber--;
      if (beatNumber <= 0)
         beatNumber = staff.numBeats();
   }

   public int getBeatsPerMeasure() {
      return staff.beatsPerMeasure();
   }

   public int getMeasure() {
      return (getBeat()==0?0:((getBeat()-1)/getBeatsPerMeasure())+1);
   }

   public int getBeatInMeasure() { return ((getBeat()%getBeatsPerMeasure()==0)?getBeatsPerMeasure():getBeat()%getBeatsPerMeasure()); }

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
      if (staff.isPitchRepeatedAtBeat(beatNumber) && pitches != null && pitchesNowPlaying != null && pitches.equals(pitchesNowPlaying))
         return;
      if (pitches == null) {
         System.out.println("Warning: Could not find beat " + beatNumber + " in StaffPlayer");
         reset();
         return;
      }
      clearWaves();
      pitchesNowPlaying = pitches;
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

   public static void main(String[] args) {
      if (args.length < 4 || args.length % 2 != 0) {
         System.err.println("Usage: java StaffPlayer <tempo> <beatsPerMeasure> <chord1> <length1> <chord2> <length2> ...");
         System.exit(-1);
      }

      if (!args[0].matches("[0-9]+") || Integer.parseInt(args[0]) == 0) {
         System.err.println("Error: Invalid tempo ?<tempo>=\"" + args[0] +"\"" +
            "\n\t<tempo> must be a number greater than 0, recommended to not exceed 468");
         System.exit(-1);
      }

      if (!args[1].matches("[0-9]+") || Integer.parseInt(args[1]) == 0) {
         System.err.println("Error: Invalid value ?<beatsPerMeasure>=\"" + args[1] +"\"" +
            "\n\t<beatsPerMeasure> must be a number greater than 0");
         System.exit(-1);
      }

      Maxim maxim = new Maxim();
      Metronome metronome = new Metronome();

      Staff staff = new Staff(Integer.parseInt(args[1]), 1, Integer.parseInt(args[0]));
      int octave = 4;

      for (int i = 0; i < args.length/2 - 1; i ++) {
         PitchBank bank = new PitchBank();
         /*octave = */bank.add(new Chord(args[(i + 1)*2]), octave);
         String length = args[(i + 1)*2 + 1];
         if (!length.matches("[0-9]+") || Integer.parseInt(length) == 0) {
            System.err.println("Error: Invalid length ?<length>=\"" + length +"\" for argument "+ ((i + 2)*2) +
               "\n\t<length> must be a number greater than 0");
            System.exit(-1);
         }
         staff.add(bank, Integer.parseInt(length));
      }

      StaffPlayer staffPlayer = new StaffPlayer(maxim, staff);
      staffPlayer.init(metronome);
      staffPlayer.setLooping(true);
      staffPlayer.play();
      System.out.println("\nTo stop playing: terminate the command (possibly Ctrl+C) or close the command line console");
      System.out.print("Currently playing beat ");
      while (true) {
         int beat = staffPlayer.getBeatInMeasure();
         String next = beat + "/" + staffPlayer.getBeatsPerMeasure() + " of measure " + staffPlayer.getMeasure() + " containing: " + staff.getPitchBankAtBeat(staffPlayer.getBeat()) +
            "                    ";
         System.out.print(next);
         for (int i = 0; i < next.length(); i++)
            System.out.print("\b");
      }
   }
}
