package main.java.smarthome.Fan;

public interface IFanState {
  public FanState getState();
  public void togglePower(Fan fan);
  public void setSpeed(Speed speed);
}
