package smarthomesim.demo.Thermostat.ThermostatStates;
import smarthomesim.demo.Thermostat.IThermostatState;
import smarthomesim.demo.Thermostat.Thermostat;
import smarthomesim.demo.Thermostat.ThermostatState;
import smarthomesim.demo.Thermostat.ThermostatMode;

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
