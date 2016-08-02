package musicmaker.theory;
import musicmaker.sound.maxim.WavetableSynth;
import musicmaker.sound.maxim.Maxim;

import java.lang.Integer;

// See "Table of note frequencies" at https://en.wikipedia.org/wiki/Scientific_pitch_notation
public class Pitch {
   private int octave;
   private float freq;
   private Note note;

   public static final int MIDDLE_C_OFFSET = 60; // Should be 60
   public static final String SUPPORTED_RANGE_STRING = "Db-1 to Ab9";
   public static final float[] mtof = {
    0f, 8.661957f, 9.177024f, 9.722718f, 10.3f, 10.913383f, 11.562325f, 12.25f, 12.978271f, 13.75f, 14.567617f, 15.433853f, 16.351599f, 17.323914f, 18.354048f, 19.445436f, 20.601723f, 21.826765f, 23.124651f, 24.5f, 25.956543f, 27.5f, 29.135235f, 30.867706f, 32.703197f, 34.647827f, 36.708096f, 38.890873f, 41.203445f, 43.65353f, 46.249302f, 49.f, 51.913086f, 55.f, 58.27047f, 61.735413f, 65.406395f, 69.295654f, 73.416191f, 77.781746f, 82.406891f, 87.30706f, 92.498604f, 97.998856f, 103.826172f, 110.f, 116.540939f, 123.470825f, 130.81279f, 138.591309f, 146.832382f, 155.563492f, 164.813782f, 174.61412f, 184.997208f, 195.997711f, 207.652344f, 220.f, 233.081879f, 246.94165f, 261.62558f, 277.182617f, 293.664764f, 311.126984f, 329.627563f, 349.228241f, 369.994415f, 391.995422f, 415.304688f, 440.f, 466.163757f, 493.883301f, 523.25116f, 554.365234f, 587.329529f, 622.253967f, 659.255127f, 698.456482f, 739.988831f, 783.990845f, 830.609375f, 880.f, 932.327515f, 987.766602f, 1046.502319f, 1108.730469f, 1174.659058f, 1244.507935f, 1318.510254f, 1396.912964f, 1479.977661f, 1567.981689f, 1661.21875f, 1760.f, 1864.655029f, 1975.533203f, 2093.004639f, 2217.460938f, 2349.318115f, 2489.015869f, 2637.020508f, 2793.825928f, 2959.955322f, 3135.963379f, 3322.4375f, 3520.f, 3729.31f, 3951.066406f, 4186.009277f, 4434.921875f, 4698.63623f, 4978.031738f, 5274.041016f, 5587.651855f, 5919.910645f, 6271.926758f, 6644.875f, 7040.f, 7458.620117f, 7902.132812f, 8372.018555f, 8869.84375f, 9397.272461f, 9956.063477f, 10548.082031f, 11175.303711f, 11839.821289f, 12543.853516f, 13289.75f
   };

   public Pitch(Note note, int octave) {
      this.note = note;
      this.octave = octave;
      freq = findFreq(note, octave);
   }

   public static float findFreq(Note note, int octave) {
      // Gives middle C (octave 4) a value of 0
      int val = note.getValue() + (octave * 12 - 48);

      val += MIDDLE_C_OFFSET;
      if (val > 0 && val < mtof.length)
         return mtof[val];
      System.out.println("Warning: Could not find frequency of " + note + octave + " in Pitch.findFreq(..)" +
         "\n\tSupported range: " + SUPPORTED_RANGE_STRING);
      return -1;
   }

   public float getFreq() { return freq; }

   public String toString() {
      return "" + note + octave;
   }

   public static void main(String[] args) {
      if (args.length != 2) {
         System.err.println("Usage: java Pitch <note> <octave>");
         System.exit(-1);
      }
      Note note = Note.get(args[0]);
      if (note == null) {
         System.err.println("Error: Invalid note ?<note>=\"" + args[0] +"\"" +
            "\n\t<note> should match [A-G](##?|bb?)?");
         System.exit(-1);
      }
      if (args[1].matches("-?[0-9]+")) {
         Maxim maxim = new Maxim();
         WavetableSynth synth = maxim.createWavetableSynth(516); // default 516
         float freq = Pitch.findFreq(note, Integer.parseInt(args[1]));
         if(freq >= 0) {
            synth.setFrequency(freq);
            System.out.println("Pitch: " + args[0] + args[1] + "\nFrequency: " + String.format("%7.3f", freq) + "Hz");

            synth.play();

            long timeLimit = 3000;
            long curTime = System.currentTimeMillis();
            long lastTime = curTime;

            System.out.print("Playing pitch for.. " + String.format("%1.3f", ((float)timeLimit/1000f)) + "s");
            while (curTime - lastTime < timeLimit) {
               curTime = System.currentTimeMillis();
               System.out.print("\b\b\b\b\b\b" + String.format("%1.3f", ((float)(timeLimit - (curTime - lastTime))/1000f)) + "s");
            }
            System.out.println("\nDone");
            synth.stop();
         }
         System.exit(0);
      } else {
         System.err.println("Error: Invalid octave ?<octave>=\"" + args[1] +"\"" +
            "\n\t<octave> must be an integer");
         System.exit(-1);
      }
   }
}
