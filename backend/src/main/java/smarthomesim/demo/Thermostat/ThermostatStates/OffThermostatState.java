package smarthomesim.demo.thermostat.thermostatstates;
import smarthomesim.demo.thermostat.IThermostatState;
import smarthomesim.demo.thermostat.Thermostat;
import smarthomesim.demo.thermostat.ThermostatState;
import smarthomesim.demo.thermostat.ThermostatMode;

public class OffThermostatState implements IThermostatState {

  @Override
  public ThermostatState getState() {
    return ThermostatState.Off;
  }

  @Override
  public void togglePower(Thermostat thermostat) {
    thermostat.changeState(new IdleThermostatState());
  }

  @Override
  public void setMode(Thermostat thermostat, ThermostatMode mode) {
  }

  @Override
  public void setTemperature(Thermostat thermostat, int temperature) {
  }

}
