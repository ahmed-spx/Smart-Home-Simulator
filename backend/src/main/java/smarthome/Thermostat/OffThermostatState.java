package main.java.smarthome.Thermostat;

public class OffThermostatState implements IThermostatState {
  private Thermostat thermostat;

  public OffThermostatState(Thermostat thermostat) {
    this.thermostat = thermostat;
  }

  @Override
  public ThermostatState getState() {
    return ThermostatState.Off;
  }

  @Override
  public void togglePower(Thermostat thermostat) {
    //thermostat.setMode(new IdleThermostatState(thermostat));
  }

  @Override
  public void setMode(Thermostat thermostat, ThermostatState mode) {
  }

  @Override
  public void setTemperature(Thermostat thermostat, int temperature) {
  }
  
}
//instead of toggle power, we can make it so it has options and not just toggle