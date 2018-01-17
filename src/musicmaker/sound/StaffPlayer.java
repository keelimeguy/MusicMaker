package musicmaker.sound;

import java.util.ArrayList;
import java.lang.Integer;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileNotFoundException;

import musicmaker.message.ISubscriber;
import musicmaker.message.Message;
import musicmaker.theory.Staff;
import musicmaker.theory.Note;
import musicmaker.theory.PitchBank;
import musicmaker.theory.Pitch;
import musicmaker.theory.Chord;
import musicmaker.theory.Metronome;
import musicmaker.sound.maxim.WavetableSynth;
import musicmaker.sound.maxim.Maxim;

public class StaffPlayer implements ISubscriber {

   private Maxim maxim;
   private Staff[] staff;
   private PitchBank[] pitchesNowPlaying;
   private int beatNumber;
   private Metronome metronome;
   private boolean looping, playing;
   ArrayList<ArrayList<WavetableSynth>> waves;

   public StaffPlayer(Maxim maxim, Staff staff) {
      this.staff = new Staff[1];
      this.staff[0] = staff;
      this.maxim = maxim;
      beatNumber = 0;
      metronome = null;
      pitchesNowPlaying = new PitchBank[1];
      pitchesNowPlaying[0] = null;
      looping = false;
      playing = false;
      waves = new ArrayList<ArrayList<WavetableSynth>>();
      waves.add(new ArrayList<WavetableSynth>());
   }

   public StaffPlayer(Maxim maxim, Staff[] staff) {
      this.staff = staff;
      this.maxim = maxim;
      beatNumber = 0;
      metronome = null;
      pitchesNowPlaying = new PitchBank[staff.length];
      for (int i = 0; i < pitchesNowPlaying.length; i ++) {
         pitchesNowPlaying[i] = null;
      }
      looping = false;
      playing = false;
      waves = new ArrayList<ArrayList<WavetableSynth>>();
      for (int i = 0; i < staff.length; i ++) {
         waves.add(new ArrayList<WavetableSynth>());
      }
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
      if (waves != null) {
         for (int i = 0; i < waves.size(); i ++) {
            for (WavetableSynth wave: waves.get(i))
               wave.stop();
         }
      }
   }

   public void play() {
      if (playing)
         return;
      metronome.setDelay((long)(60000.0/staff[0].getTempo()));
      if (looping)
         metronome.start();
      else
         metronome.start(largestBeatNumber() + 1);
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
      beatNumber = largestBeatNumber();
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
      while (getMeasure() == measure && !isBeatNumberOver()) {
         beatNumber++;
      }

      if (isBeatNumberOver())
         beatNumber = 1;
   }

   public boolean isBeatNumberOver() {
      for (int i = 0; i < staff.length; i ++) {
         if (beatNumber > staff[i].numBeats())
            return true;
      }
      return false;
   }

   public int largestBeatNumber() {
      int ret = staff[0].numBeats();
      for (int i = 1; i < staff.length; i ++) {
         if (ret < staff[i].numBeats())
            ret = staff[i].numBeats();
      }
      return ret;
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
         beatNumber = largestBeatNumber();
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
      if (beatNumber > largestBeatNumber())
         beatNumber = 1;
   }

   public void prevBeat() {
      beatNumber--;
      if (beatNumber <= 0)
         beatNumber = largestBeatNumber();
   }

   public int getBeatsPerMeasure() {
      return staff[0].beatsPerMeasure();
   }

   public int getMeasure() {
      return (getBeat()==0?0:((getBeat()-1)/getBeatsPerMeasure())+1);
   }

   public int getBeatInMeasure() { return ((getBeat()%getBeatsPerMeasure()==0)?getBeatsPerMeasure():getBeat()%getBeatsPerMeasure()); }

   public ArrayList<Pitch> getPitchListAtBeat(int beat, int thread) {
      PitchBank pitches = staff[thread].getPitchBankAtBeat(beat);
      if (pitches != null)
         return pitches.getPitchList();
      return null;
   }

   @Override
   public void notify(Message message) {
      beatNumber++;
      if (looping && beatNumber > largestBeatNumber())
         beatNumber = 1;
      else if (!looping && beatNumber > largestBeatNumber()) {
         stop();
         return;
      }
      PitchBank[] pitches = new PitchBank[staff.length];
      for (int i = 0; i < staff.length; i ++) {
         pitches[i] = staff[i].getPitchBankAtBeat(beatNumber);
         if (staff[i].isPitchRepeatedAtBeat(beatNumber) && pitches[i] != null && pitchesNowPlaying[i] != null && pitches[i].equals(pitchesNowPlaying[i]))
            continue;
         // if (pitches[i] == null) {
         //    System.out.println("Warning: Could not find beat " + beatNumber + " in StaffPlayer, thread " + i);
         //    reset();
         //    return;
         // }
         clearWaves(i);
         pitchesNowPlaying[i] = pitches[i];
         if (pitches[i] != null) {
            ArrayList<Pitch> pitchList = pitches[i].getPitchList();
            // System.out.println("Thread " + i + " (" + beatNumber + "): " + pitches[i]);
            // if (pitchList == null) {
            //    System.out.println("Warning: Could not retrieve pitchList in StaffPlayer, thread " + i);
            //    reset();
            //    return;
            // }
            for (Pitch pitch: pitchList) {
               WavetableSynth wave = maxim.createWavetableSynth(516);
               wave.setFrequency(pitch.getFreq());
               waves.get(i).add(wave);
               wave.play();
            }
         }
      }
   }

   private void clearWaves() {
      for (int i = 0; i < waves.size(); i ++) {
         for (WavetableSynth wave: waves.get(i)) {
            wave.stop();
            maxim.removeAudioPlayer(wave);
         }
         waves.get(i).clear();
      }
   }

   private void clearWaves(int i) {
      if (i < waves.size()) {
         for (WavetableSynth wave: waves.get(i)) {
            wave.stop();
            maxim.removeAudioPlayer(wave);
         }
         waves.get(i).clear();
      }
   }

   public void clear() {
      reset();
      clearWaves();
   }

   public static void main(String[] args) {
      if ((args.length < 4 || args.length % 2 != 0) && args.length != 1) {
         System.err.println("Usage: java StaffPlayer <file> \n\tor\njava StaffPlayer <tempo> <beatsPerMeasure> <chord1> <length1> <chord2> <length2> ...");
         System.exit(-1);
      }

      if (args.length!=1) {

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

      } else { // File input

         BufferedReader reader = null;
         boolean done = false;
         String[] line = null;
         int threads = 0, tempo = 0, bpm = 0;
         try {
            reader = new BufferedReader(new FileReader(args[0]));
         } catch (FileNotFoundException e) {
            System.err.println("File "+args[0]+" not found");
            System.exit(-1);
         }
         try {
            line = reader.readLine().split(" ");
            if (line.length != 3) {
               System.err.println("Invalid file format");
               System.exit(-1);
            }

            tempo = Integer.parseInt(line[0]);
            if (tempo <= 0) {
               System.err.println("Error: Invalid tempo ?<tempo>=\"" + line[0] +"\"" +
                  "\n\t<tempo> must be a number greater than 0, recommended to not exceed 468");
               System.exit(-1);
            }

            bpm = Integer.parseInt(line[1]);
            if (bpm <= 0) {
               System.err.println("Error: Invalid value ?<beatsPerMeasure>=\"" + line[1] +"\"" +
                  "\n\t<beatsPerMeasure> must be a number greater than 0");
               System.exit(-1);
            }

            threads = Integer.parseInt(line[2]);
            if (threads<=0) {
               System.err.println("Error: Invalid value ?<numThreads>=\"" + line[2] +"\"" +
                  "\n\t<numThreads> must be a number greater than 0");
               System.exit(-1);
            }
         } catch (IOException e) {
            System.err.println("Invalid file format");
            System.exit(-1);
         }

         Maxim maxim = new Maxim();
         Metronome metronome = new Metronome();

         Staff[] staff = new Staff[threads];
         for (int i = 0; i < threads; i ++)
            staff[i] = new Staff(Integer.parseInt(line[1]), 1, Integer.parseInt(line[0]));

         int curThread = 0;
         boolean valid = true;
         boolean inThreadSection = false;
         try {
            while (!done) {
               line = reader.readLine().split(" ");
               if (line.length == 1 && line[0].equals("end") && valid) {
                  done = true;
               } else if (line.length == 2 && line[0].equals("{") && valid) {
                  if (inThreadSection) {
                     System.err.println("Invalid file format");
                     System.exit(-1);
                  }
                  inThreadSection = true;
                  curThread = 0;
                  int numGroups = Integer.parseInt(line[1]);
                  if (numGroups <= 0 || numGroups > threads) {
                     System.err.println("Error: Invalid value ?<groups>=\"" + line[1] +"\"" +
                        "\n\t<groups> must be a number greater than 0 and at most <numThreads>");
                  }
               } else if (line.length == 1 && line[0].equals(",") && valid) {
                  if (!inThreadSection) {
                     System.err.println("Invalid file format");
                     System.exit(-1);
                  }
                  curThread++;
               } else if (line.length == 1 && line[0].equals("}") && valid) {
                  if (!inThreadSection) {
                     System.err.println("Invalid file format");
                     System.exit(-1);
                  }
                  for (int t = curThread + 1; t < threads; t ++) {
                     staff[t].add(new PitchBank(), staff[t].beatsPerMeasure());
                  }
                  curThread = 0;
                  inThreadSection = false;
               } else {
                  PitchBank bank = new PitchBank();
                  int length = Integer.parseInt(line[0]);
                  if (length <= 0) {
                     System.err.println("Error: Invalid value ?<length>=\"" + line[0] +"\"" +
                        "\n\t<length> must be a number greater than 0");
                     System.exit(-1);
                  }
                  for (int i = 1; i < line.length; i ++) {
                     /*octave = */bank.add(new Pitch(Note.get(line[i]), Integer.parseInt(line[++i])));
                  }
                  staff[curThread].add(bank, length);
                  valid = true;
                  if (!inThreadSection) {
                     for (int t = 1; t < threads; t ++) {
                        staff[t].add(new PitchBank(), length);
                     }
                  }
               }
            }
         } catch (IOException e) {
            System.err.println("Invalid file format");
            System.exit(-1);
         }

         StaffPlayer staffPlayer = new StaffPlayer(maxim, staff);
         staffPlayer.init(metronome);
         staffPlayer.setLooping(true);
         staffPlayer.play();
         System.out.println("\nTo stop playing: terminate the command (possibly Ctrl+C) or close the command line console");
         while (true) {
            int beat = staffPlayer.getBeatInMeasure();
            String next = staffPlayer.getMeasure() + ": " + beat + "/" + staffPlayer.getBeatsPerMeasure() + "                    \n";
            for (int i = 0; i < threads-1; i ++) {
               next += staff[i].getPitchBankAtBeat(staffPlayer.getBeat()) + "                    \n";
            }
            next += staff[threads-1].getPitchBankAtBeat(staffPlayer.getBeat()) + "                    ";
            System.out.println(next);
            int count = threads+1;
            System.out.print(String.format("\033[%dA",count)); // Move up
            System.out.print("\033[2K"); // Erase line content
         }
      }
   }
}
