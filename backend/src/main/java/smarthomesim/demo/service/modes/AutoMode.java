package smarthomesim.demo.service.modes;

import org.springframework.stereotype.Component;

import smarthomesim.demo.domain.thermostat.Thermostat;
import smarthomesim.demo.domain.thermostat.ThermostatMode;
import smarthomesim.demo.domain.thermostat.thermostatstates.CoolingThermostatState;
import smarthomesim.demo.domain.thermostat.thermostatstates.HeatingThermostatState;

@Component
public class AutoMode implements ThermostatModeStrategy {

  @Override
  public ThermostatMode mode() {
    return ThermostatMode.Auto;
  }

  @Override
  public void evaluate(Thermostat t) {
    int amb = t.getAmbientTemperature();
    int des = t.getDesiredTemperature();
    if (amb < des) t.changeState(new HeatingThermostatState());
    else if (amb > des) t.changeState(new CoolingThermostatState());
  }
}