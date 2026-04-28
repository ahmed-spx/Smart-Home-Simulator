package main.java.smarthome.Thermostat;

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
  public void setMode(Thermostat thermostat, ThermostatState mode) {
  }

  @Override
  public void setTemperature(Thermostat thermostat, int temperature) {
  }  
}
