package smarthomesim.demo.Fan;

public class OffFanState implements IFanState {
  private Fan fan;

  public OffFanState(Fan fan) {
    this.fan = fan;
  }

  @Override
  public FanState getState() {
    return FanState.Off;
  }

  @Override
  public void togglePower(Fan fan) {
    fan.changeState(new OnFanState(fan));
  }

  @Override
  public void setSpeed(Speed speed) {
    fan.setSpeed(speed);
  }
  
}
