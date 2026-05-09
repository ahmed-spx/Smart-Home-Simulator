package smarthomesim.demo.domain.fan;

public class OnFanState implements IFanState {

  @Override
  public FanState getState() {
    return FanState.On;
  }

  @Override
  public void togglePower(Fan fan) {
    fan.changeState(new OffFanState());
  }

  @Override
  public void setSpeed(Fan fan, Speed speed) {
    fan.setSpeed(speed);
  }

}
