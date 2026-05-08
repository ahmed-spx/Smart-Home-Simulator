package smarthomesim.demo.thermostat.modes;

import org.springframework.stereotype.Component;
import smarthomesim.demo.thermostat.Thermostat;
import smarthomesim.demo.thermostat.ThermostatMode;
import smarthomesim.demo.thermostat.thermostatstates.CoolingThermostatState;

@Component
public class CoolingMode implements ThermostatModeStrategy {

  @Override
  public ThermostatMode mode() {
    return ThermostatMode.Cooling;
  }

  @Override
  public void evaluate(Thermostat t) {
    if (t.getAmbientTemperature() > t.getDesiredTemperature()) {
      t.changeState(new CoolingThermostatState());
    }
  }
}