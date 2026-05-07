package smarthomesim.demo.fan;

public interface IFanState {
  public FanState getState();
  public void togglePower(Fan fan);
  public void setSpeed(Fan fan, Speed speed);
}
