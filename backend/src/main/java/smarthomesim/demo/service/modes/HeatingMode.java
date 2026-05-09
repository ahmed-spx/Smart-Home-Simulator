package smarthomesim.demo.service.modes;

import org.springframework.stereotype.Component;

import smarthomesim.demo.domain.thermostat.Thermostat;
import smarthomesim.demo.domain.thermostat.ThermostatMode;
import smarthomesim.demo.domain.thermostat.thermostatstates.HeatingThermostatState;

@Component
public class HeatingMode implements ThermostatModeStrategy {

  @Override
  public ThermostatMode mode() {
    return ThermostatMode.Heating;
  }

  @Override
  public void evaluate(Thermostat t) {
    if (t.getAmbientTemperature() < t.getDesiredTemperature()) {
      t.changeState(new HeatingThermostatState());
    }
  }
}