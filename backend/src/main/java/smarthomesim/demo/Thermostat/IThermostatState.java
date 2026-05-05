package smarthomesim.demo.Thermostat;

public interface IThermostatState {
  ThermostatState getState();
  void togglePower(Thermostat thermostat);
  void setMode(Thermostat thermostat, ThermostatMode mode);
  void setTemperature(Thermostat thermostat, int temperature);
}
