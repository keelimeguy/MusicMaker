package musicmaker.sound;

/**
 *
 * @author Mangusbrother
 * with edits by keelimeguy
 */

import musicmaker.MusicMaker;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * This is an example program that demonstrates how to play back an audio file
 * using the Clip in Java Sound API.
 * @author www.codejava.net
 *
 */
public class MusicPlayer extends Thread implements LineListener {

   private URL location = null;
   private File file = null;

   /**
    * this flag indicates whether the playback completes or not.
    */
   private boolean playCompleted, flag;
   private int start = 0, end = 0;
   private String path = null;
   private boolean playing = false, looping = false, sound = false, reset = false;
   private Clip audioClip;

   public void run() {
      while (true)
         if (playing) play(path);
   }

   /**
    * Play a given audio file.
    * @param audioFilePath Path of the audio file.
    */
   private void play(String audioFilePath) {
      //File audioFile = new File(audioFilePath);

      try {
         if (location == null)
            location = MusicMaker.class.getProtectionDomain().getCodeSource().getLocation();
         if (file == null)
            file = new File(location.getFile()).getParentFile();
         AudioInputStream audioStream = AudioSystem.getAudioInputStream((new File(file + audioFilePath)).toURI().toURL());//audioFile);
         System.out.println("Playing: " + (new File(file + audioFilePath)).toString());
         AudioFormat format = audioStream.getFormat();

         DataLine.Info info = new DataLine.Info(Clip.class, format);

         audioClip = (Clip) AudioSystem.getLine(info);

         audioClip.addLineListener(this);

         audioClip.open(audioStream);

         if (reset) reset = false;

         if (looping) {
            FloatControl gainControl = (FloatControl) audioClip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(-20.0f);
            //System.out.println(start);
            if (start != end) audioClip.setLoopPoints(start, end);
            //System.out.println(audioClip.getFrameLength());
            audioClip.loop(Clip.LOOP_CONTINUOUSLY);
         } else {
            audioClip.start();
         }
         while (!playCompleted) {
            if (sound) {
               sound = false;
               play(path);
            }
            // wait for the playback completes
            try {
               Thread.sleep(2);
            } catch (InterruptedException ex) {
               ex.printStackTrace();
            }
            if (reset) break;

         }
         if (playCompleted || reset) {
            playing = false;
            audioClip.removeLineListener(this);
         }
         audioClip.close();

      } catch (UnsupportedAudioFileException ex) {
         System.out.println("The specified audio file is not supported.");
         ex.printStackTrace();
      } catch (LineUnavailableException ex) {
         System.out.println("Audio line for playing back is unavailable.");
         ex.printStackTrace();
      } catch (IOException ex) {
         System.out.println("Error playing the audio file.");
         ex.printStackTrace();
      } catch (Exception e){
         System.out.println("Error playing the audio file.");
         e.printStackTrace();
      }

   }

   public void playMusic(String audioFilePath) {
      path = audioFilePath;
      playing = true;
      playCompleted = false;
      looping = true;
      start = end = 0;
   }

   public void playMusic(String audioFilePath, int start, int end) {
      path = audioFilePath;
      playing = true;
      playCompleted = false;
      looping = true;
      this.start = start;
      this.end = end;
   }

   public void playSound(String audioFilePath) {
      sound = true;
      path = audioFilePath;
      playCompleted = false;
      playing = true;
      looping = false;
   }

   public void reset() {
      reset = true;
      try {
         System.out.println("RESET");
         Thread.sleep(2);
      } catch (InterruptedException ex) {
         ex.printStackTrace();
      }
   }

   /**
    * Listens to the START and STOP events of the audio line.
    */
   @Override
   public void update(LineEvent event) {
      LineEvent.Type type = event.getType();

      if (type == LineEvent.Type.START) {
         flag = false;
         //System.out.println("Playback started.");

      } else if (type == LineEvent.Type.STOP) {
         if (!flag) {
            flag = true;
            playCompleted = true;
         }
         //System.out.println("Playback completed.");
      }

   }
}
