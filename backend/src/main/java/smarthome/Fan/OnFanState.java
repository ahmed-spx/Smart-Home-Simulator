package main.java.smarthome.Fan;

public class OnFanState implements IFanState {
  private Fan fan;

  public OnFanState(Fan fan) {
    this.fan = fan;
  }

  @Override
  public FanState getState() {
    return FanState.On;
  }

  @Override
  public void togglePower(Fan fan) {
    fan.changeState(new OffFanState(fan));
  }

  @Override
  public void setSpeed(Speed speed) {
    fan.setSpeed(speed);
  }

}
