package smarthomesim.demo.service.modes;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

import smarthomesim.demo.domain.thermostat.ThermostatMode;

@Component
public class ThermostatModes {

  private final Map<ThermostatMode, ThermostatModeStrategy> strategies;

  public ThermostatModes(List<ThermostatModeStrategy> beans) {
    this.strategies = beans.stream()
        .collect(Collectors.toMap(ThermostatModeStrategy::mode, s -> s));
  }

  public ThermostatModeStrategy forMode(ThermostatMode mode) {
    ThermostatModeStrategy s = strategies.get(mode);
    if (s == null) throw new IllegalStateException("No strategy registered for mode: " + mode);
    return s;
  }
}