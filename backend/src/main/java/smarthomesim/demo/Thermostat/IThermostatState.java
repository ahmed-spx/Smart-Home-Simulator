package smarthomesim.demo.thermostat;

public interface IThermostatState {
  ThermostatState getState();
  void togglePower(Thermostat thermostat);
  void setMode(Thermostat thermostat, ThermostatMode mode);
  void setTemperature(Thermostat thermostat, int temperature);
  void tick(Thermostat thermostat);
}
