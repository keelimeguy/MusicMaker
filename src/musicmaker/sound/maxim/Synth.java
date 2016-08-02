package musicmaker.sound.maxim;

public interface Synth {
  public void volume(float volume);
  public void ramp(float val, float timeMs);
  public void setDelayTime(float delayMs);
  public void setDelayFeedback(float fb);
  public void setFilter(float cutoff, float resonance);
  public void setAnalyzing(boolean analysing);
  public float getAveragePower();
  public float[] getPowerSpectrum();
}
