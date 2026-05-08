package smarthomesim.demo.thermostat;

public interface IThermostatState {
  public ThermostatState getState();
  public void togglePower(Thermostat thermostat);
  public void setMode(Thermostat thermostat, ThermostatMode mode);
  public void setTemperature(Thermostat thermostat, int temperature);
  public void tick(Thermostat thermostat);
}
