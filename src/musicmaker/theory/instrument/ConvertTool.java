package musicmaker.theory.instrument;

import java.util.ArrayList;
import java.util.Collections;
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
import musicmaker.theory.Metronome;

public class ConvertTool implements ISubscriber {
   private Staff[] staff;
   private StringInstrument instrument;
   private PitchBank[] pitchesNowPlaying;
   private int beatNumber;
   private Metronome metronome;
   private boolean playing, finished;

   public ConvertTool(Staff[] staff, StringInstrument instrument) {
      this.staff = staff;
      this.instrument = instrument;
      beatNumber = 0;
      metronome = null;
      playing = false;
      finished = false;
      pitchesNowPlaying = new PitchBank[staff.length];
      for (int i = 0; i < pitchesNowPlaying.length; i ++) {
         pitchesNowPlaying[i] = null;
      }
   }

   public void init(Metronome ticker) {
      if (metronome != null)
         metronome.unsubscribe(this);
      metronome = ticker;
      metronome.subscribe(this);
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
      finished = true;
      System.exit(0);
   }

   public void convert() {
      if (playing)
         return;
      metronome.setDelay(1);
      metronome.start(largestBeatNumber() + 1);
      playing = true;
   }

   public int largestBeatNumber() {
      int ret = staff[0].numBeats();
      for (int i = 1; i < staff.length; i ++) {
         if (ret < staff[i].numBeats())
            ret = staff[i].numBeats();
      }
      return ret;
   }

   @Override
   public void notify(Message message) {
      beatNumber++;
      if (beatNumber > largestBeatNumber()) {
         stop();
         return;
      }
      PitchBank[] pitches = new PitchBank[staff.length];

      ArrayList<Integer>[] frets = (ArrayList<Integer>[]) new ArrayList[instrument.getNumStrings()];
      for (int i = 0; i < frets.length; i ++) {
         frets[i] = new ArrayList<Integer>();
      }
      ArrayList<Integer>[] specificFrets = (ArrayList<Integer>[]) new ArrayList[instrument.getNumStrings()];;
      for (int i = 0; i < specificFrets.length; i ++) {
         specificFrets[i] = new ArrayList<Integer>();
      }
      String cur = "M="+(beatNumber/staff[0].beatsPerMeasure()) + " b=" + (beatNumber%staff[0].beatsPerMeasure() - 1) + " : ";
      boolean[] repeat = new boolean[staff.length];
      for (int i = 0; i < staff.length; i ++) {
         repeat[i] = false;
      }

      for (int i = 0; i < staff.length; i ++) {
         pitches[i] = staff[i].getPitchBankAtBeat(beatNumber);
         if (staff[i].isPitchRepeatedAtBeat(beatNumber) && pitches[i] != null && pitchesNowPlaying[i] != null && pitches[i].equals(pitchesNowPlaying[i])) {
            repeat[i] = true;
            continue;
         }

         pitchesNowPlaying[i] = pitches[i];
         if (pitches[i] != null && pitches[i].toString() != null) {
            ArrayList<Integer>[] newSpecificFrets = instrument.findOrderedFretsForPitchBank(pitches[i]);
            ArrayList<Note> notes = new ArrayList<Note>();
            for (Pitch pitch : pitches[i].getPitchList())
               notes.add(pitch.getNote());
            ArrayList<Integer>[] newFrets = instrument.findOrderedFretsForNotes(notes);
            for (int f = 0; f < newFrets.length; f ++) {
               if (f < frets.length && newFrets[f] != null)
                  for (Integer fret : newFrets[f])
                     if (!frets[f].contains(fret))
                        frets[f].add(fret);
            }
            for (int f = 0; f < newSpecificFrets.length; f ++) {
               if (f < specificFrets.length && newSpecificFrets[f] != null)
                  specificFrets[f].addAll(newSpecificFrets[f]);
            }
            cur += pitches[i] + "   ";
         }
      }

      boolean valid = false;
      for (int i = 0; i < staff.length; i ++) {
         if (!repeat[i]) valid = true;
      }
      if (!valid) return;

      for (int string = 1; string <= instrument.getNumStrings(); string++)
         if (frets[string-1] != null)
            Collections.sort(frets[string-1]);

      String[] strings = new String[instrument.getNumStrings()];
      for (int string = 0; string < strings.length; string++) {
         strings[string] = "";
         if(frets[string] != null)
            for (Integer fret: frets[string]) {
               if (specificFrets[string].contains(fret))
                  strings[string] += fret + (fret>=10?"      ":"       ");
               else
                  strings[string] += "("+fret+")" + (fret>=10?"    ":"     ");
            }
      }

      System.out.println(cur);
      for (int s = strings.length; s > 0; s--)
         System.out.println(instrument.getNoteOnFretOfString(0, s) + ": " + strings[s-1]);
      System.out.println();
   }

   public static void main(String[] args) {
      if (args.length != 2) {
         System.err.println("Usage: java ConvertTool <instrument> <file>");
         System.exit(-1);
      }

      StringInstrument instrument = null;
      if (args[0].equals("-g"))
         instrument = new Guitar(21);
      else if (args[0].equals("-u"))
         instrument = new Ukulele(15, false);
      else if (args[0].equals("-lu"))
         instrument = new Ukulele(15, true);
      else {
         System.err.println("Error: Invalid instrument ?<instrument>=\"" + args[0] +"\"" +
            "\n\t<instrument> must be -g for guitar, -u for ukulele, -lu for low g ukulele");
         System.exit(-1);
      }

      BufferedReader reader = null;
      boolean done = false;
      String[] line = null;
      int threads = 0, tempo = 0, bpm = 0;
      try {
         reader = new BufferedReader(new FileReader(args[1]));
      } catch (FileNotFoundException e) {
         System.err.println("File "+args[1]+" not found");
         System.exit(-1);
      }
      try {
         line = reader.readLine().split(" ");
         if (line.length != 4) {
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

      ConvertTool convertTool = new ConvertTool(staff, instrument);
      convertTool.init(metronome);
      convertTool.convert();
   }
}
