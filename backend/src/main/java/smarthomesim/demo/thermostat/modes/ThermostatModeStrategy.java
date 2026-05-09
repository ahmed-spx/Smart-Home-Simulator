package smarthomesim.demo.thermostat.modes;

import smarthomesim.demo.thermostat.Thermostat;
import smarthomesim.demo.thermostat.ThermostatMode;

// Strategy for a thermostat operating mode (Auto, Heating, Cooling).
// Invoked when desired temperature or mode changes; consults ambient/desired
// to decide whether the thermostat should transition into Heating, Cooling, or
// stay in its current state.

// Implementations are auto-discovered by {@link ThermostatModes} via Spring DI.

public interface ThermostatModeStrategy {
  ThermostatMode mode();
  public void evaluate(Thermostat thermostat);
}