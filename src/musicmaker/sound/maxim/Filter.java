package musicmaker.sound.maxim;

public interface Filter {
  public void setFilter(float f, float r);
  public float applyFilter(float in);
  default float constrain(float value, float min, float max) {
    float val = value;
    if (val < min) val = min;
    if (val > max) val = max;
    return val;
  }
  default float map(float value, float curMin, float curMax, float targetMin, float targetMax) {
    return targetMin + (targetMax - targetMin) * ((value - curMin) / (curMax - curMin));
  }
}