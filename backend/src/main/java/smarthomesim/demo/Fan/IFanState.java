package smarthomesim.demo.fan;

// State strategy for a Fan. Implementations represent each power state
// and define how the fan responds to commands in that state.


public interface IFanState {
  public FanState getState();
  public void togglePower(Fan fan);
  public void setSpeed(Fan fan, Speed speed);
}
