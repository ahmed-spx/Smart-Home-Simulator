package main.java.smarthome.Thermostat;

public interface IThermostatState {
  ThermostatState getState();
  void togglePower(Thermostat thermostat);
  void setMode(Thermostat thermostat, ThermostatState mode);
  void setTemperature(Thermostat thermostat, int temperature);
}
