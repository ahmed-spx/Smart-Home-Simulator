package smarthomesim.demo.thermostat.modes;

import smarthomesim.demo.thermostat.Thermostat;
import smarthomesim.demo.thermostat.ThermostatMode;

public interface ThermostatModeStrategy {
  ThermostatMode mode();
  public void evaluate(Thermostat thermostat);
}