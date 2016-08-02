package musicmaker.sound.maxim;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.AudioFormat;
import java.util.ArrayList;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;

public class AudioThread extends Thread {
  private int minSize;
  //private AudioTrack track;
  private short[] bufferS;
  private byte[] bOutput;
  private ArrayList audioGens;
  private boolean running;

  private FFT fft;
  private float[] fftFrame;
  private SourceDataLine sourceDataLine;
  private int blockSize;

  public AudioThread(float samplingRate, int blockSize) {
    this(samplingRate, blockSize, false);
  }

  public AudioThread(float samplingRate, int blockSize, boolean enableFFT)
  {
    this.blockSize = blockSize;
    audioGens = new ArrayList();
    // we'll do our dsp in shorts
    bufferS = new short[blockSize];
    // but we'll convert to bytes when sending to the sound card
    bOutput = new byte[blockSize * 2];
    AudioFormat audioFormat = new AudioFormat(samplingRate, 16, 1, true, false);
    DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);

    sourceDataLine = null;
    // here we try to initialise the audio system. try catch is exception handling, i.e.
    // dealing with things not working as expected
    try {
      sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
      sourceDataLine.open(audioFormat, bOutput.length);
      sourceDataLine.start();
      running = true;
    }
    catch (LineUnavailableException lue) {
      // it went wrong!
      lue.printStackTrace(System.err);
      System.out.println("Could not initialise audio. check above stack trace for more info");
      //System.exit(1);
    }


    if (enableFFT) {
      try {
        fft = new FFT();
      }
      catch(Exception e) {
        System.out.println("Error setting up the audio analyzer");
        e.printStackTrace();
      }
    }
  }

  // overidden from Thread
  public void run() {
    running = true;
    while (running) {
      //System.out.println("AudioThread : ags  "+audioGens.size());
      for (int i=0;i<bufferS.length;i++) {
        // we add up using a 32bit int
        // to prevent clipping
        int val = 0;
        if (audioGens.size() > 0) {
          for (int j=0;j<audioGens.size(); j++) {
            AudioGenerator ag = (AudioGenerator)audioGens.get(j);
            if (ag != null)
              val += ag.getSample();
          }
          val /= audioGens.size();
        }
        bufferS[i] = (short) val;
      }
      // send it to the audio device!
      sourceDataLine.write(shortsToBytes(bufferS, bOutput), 0, bOutput.length);
    }
  }

  public void addAudioGenerator(AudioGenerator ag) {
    if (ag != null)
      audioGens.add(ag);
  }

  public void removeAudioGenerator(AudioGenerator ag) {
    if (ag != null)
      audioGens.remove(ag);
  }

  /**
   * converts an array of 16 bit samples to bytes
   * in little-endian (low-byte, high-byte) format.
   */
  private byte[] shortsToBytes(short[] sData, byte[] bData) {
    int index = 0;
    short sval;
    for (int i = 0; i < sData.length; i++) {
      //short sval = (short) (fData[j][i] * ShortMaxValueAsFloat);
      sval = sData[i];
      bData[index++] = (byte) (sval & 0x00FF);
      bData[index++] = (byte) ((sval & 0xFF00) >> 8);
    }
    return bData;
  }

  /**
   * Returns a recent snapshot of the power spectrum
   */
  public float[] getPowerSpectrum() {
    // process the last buffer that was calculated
    if (fftFrame == null) {
      fftFrame = new float[bufferS.length];
    }
    for (int i=0;i<fftFrame.length;i++) {
      fftFrame[i] = ((float) bufferS[i] / 32768f);
    }
    return fft.process(fftFrame, true);
    //return powerSpectrum;
  }
}
