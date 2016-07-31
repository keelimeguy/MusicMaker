package musicmaker.sound;

/**
 *
 * @author Mangusbrother 
 * with edits by keelimeguy
 */

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
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
public class SoundPlayer extends Thread implements LineListener {

   /**
    * this flag indicates whether the playback completes or not.
    */
   private boolean playCompleted;
   private String path = null;
   private boolean playing = false, looping = false;

   public void run() {
      while (true)
         if (playing) play(path);
   }

   /**
    * Play a given audio file.
    * @param audioFilePath Path of the audio file.
    */
   private void play(String audioFilePath) {
      File audioFile = new File(audioFilePath);

      try {
         AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);

         AudioFormat format = audioStream.getFormat();

         DataLine.Info info = new DataLine.Info(Clip.class, format);

         Clip audioClip = (Clip) AudioSystem.getLine(info);

         audioClip.addLineListener(this);

         audioClip.open(audioStream);

         if (looping)
            audioClip.loop(Clip.LOOP_CONTINUOUSLY);
         else
            audioClip.start();

         while (!playCompleted) {
            // wait for the playback completes
            try {
               Thread.sleep(1000);
            } catch (InterruptedException ex) {
               ex.printStackTrace();
            }
         }

         if (playCompleted) playing = false;

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
      }

   }

   public void playMusic(String audioFilePath) {
      path = audioFilePath;
      playing = true;
      looping = true;
   }

   public void playSound(String audioFilePath) {
      path = audioFilePath;
      playing = true;
      looping = false;
   }

   /**
    * Listens to the START and STOP events of the audio line.
    */
   @Override
   public void update(LineEvent event) {
      LineEvent.Type type = event.getType();

      if (type == LineEvent.Type.START) {
         System.out.println("Playback started.");

      } else if (type == LineEvent.Type.STOP) {
         playCompleted = true;
         System.out.println("Playback completed.");
      }

   }
}
