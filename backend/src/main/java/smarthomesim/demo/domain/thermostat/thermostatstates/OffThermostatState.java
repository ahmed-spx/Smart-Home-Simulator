package smarthomesim.demo.domain.thermostat.thermostatstates;
import smarthomesim.demo.domain.thermostat.IThermostatState;
import smarthomesim.demo.domain.thermostat.Thermostat;
import smarthomesim.demo.domain.thermostat.ThermostatMode;
import smarthomesim.demo.domain.thermostat.ThermostatState;

public class OffThermostatState implements IThermostatState {

  @Override
  public ThermostatState getState() {
    return ThermostatState.Off;
  }

  @Override
  public void togglePower(Thermostat thermostat) {
    thermostat.changeState(new IdleThermostatState());
    thermostat.setStateMode(thermostat.getMode());
  }

  @Override
  public void setMode(Thermostat thermostat, ThermostatMode mode) {
  }

  @Override
  public void setTemperature(Thermostat thermostat, int temperature) {
  }

  @Override
  public void tick(Thermostat thermostat) {
  }

}
