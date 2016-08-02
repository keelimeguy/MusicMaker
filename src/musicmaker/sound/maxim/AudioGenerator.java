package musicmaker.sound.maxim;

/**
 * Implement this interface so the AudioThread can request samples from you
 */
public interface AudioGenerator {
  /** AudioThread calls this when it wants a sample */
  short getSample();
}
