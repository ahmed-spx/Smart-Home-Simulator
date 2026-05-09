package smarthomesim.demo.thermostat;

// State strategy for a Thermostat. Implementations represent the four
// runtime states (Off, Idle, Heating, Cooling) and define behaviour for each.


public interface IThermostatState {
  public ThermostatState getState();
  public void togglePower(Thermostat thermostat);
  public void setMode(Thermostat thermostat, ThermostatMode mode);
  public void setTemperature(Thermostat thermostat, int temperature);
  public void tick(Thermostat thermostat);
}
