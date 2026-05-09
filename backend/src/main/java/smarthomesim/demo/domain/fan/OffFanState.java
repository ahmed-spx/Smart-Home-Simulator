package smarthomesim.demo.domain.fan;

public class OffFanState implements IFanState {

  @Override
  public FanState getState() {
    return FanState.Off;
  }

  @Override
  public void togglePower(Fan fan) {
    fan.changeState(new OnFanState());
  }

  @Override
  public void setSpeed(Fan fan, Speed speed) {
  }
  
}
