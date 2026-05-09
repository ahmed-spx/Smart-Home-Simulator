package smarthomesim.demo.service.factory;

import java.util.Map;

import org.springframework.stereotype.Component;

import smarthomesim.demo.domain.Device;
import smarthomesim.demo.domain.thermostat.IThermostatState;
import smarthomesim.demo.domain.thermostat.Multiplier;
import smarthomesim.demo.domain.thermostat.Thermostat;
import smarthomesim.demo.domain.thermostat.ThermostatMode;
import smarthomesim.demo.domain.thermostat.thermostatstates.CoolingThermostatState;
import smarthomesim.demo.domain.thermostat.thermostatstates.HeatingThermostatState;
import smarthomesim.demo.domain.thermostat.thermostatstates.IdleThermostatState;
import smarthomesim.demo.domain.thermostat.thermostatstates.OffThermostatState;
import smarthomesim.demo.repository.persistence.DeviceSnapshot;
import smarthomesim.demo.service.modes.ThermostatModes;

@Component
public class ThermostatCreator implements DeviceCreator {

  private final ThermostatModes modes;

  public ThermostatCreator(ThermostatModes modes) {
    this.modes = modes;
  }

  @Override public String typeKey() { return "Thermostat"; }
  @Override public Class<? extends Device> deviceClass() { return Thermostat.class; }

  @Override
  public Device create(String id, String name, String location) {
    Thermostat t = new Thermostat(id, name, location);
    t.setModes(modes);
    return t;
  }

  @Override
  public Device rehydrate(DeviceSnapshot s) {
    Thermostat t = new Thermostat(s.id(), s.name(), s.location());
    t.setModes(modes);
    t.changeState(thermostatStateFor(s.state()));
    t.setThermostatMode(ThermostatMode.valueOf(s.mode()));
    t.setMultiplier(Multiplier.valueOf(s.multiplier()));
    t.setThermostatDesiredTemperature(s.desiredTemperature());
    t.setAmbientTemperature(s.ambientTemperature());
    return t;
  }

  @Override
  public DeviceSnapshot dehydrate(Device d) {
    Thermostat t = (Thermostat) d;
    return new DeviceSnapshot(t.getId(), "Thermostat", t.getName(), t.getLocation(),
        t.getThermostatState().name(), null, null, null,
        t.getMode().name(), t.getMultiplier().name(),
        t.getDesiredTemperature(), t.getAmbientTemperature());
  }

  private IThermostatState thermostatStateFor(String state) {
    return switch (state) {
      case "Idle" -> new IdleThermostatState();
      case "Heating" -> new HeatingThermostatState();
      case "Cooling" -> new CoolingThermostatState();
      default -> new OffThermostatState();
    };
  }

  @Override public String idPrefix() { return "thermo"; }

  @Override
  public void addCustomFields(Device device, Map<String, Object> info) {
    Thermostat t = (Thermostat) device;
    info.put("state", t.getThermostatState());
    info.put("mode", t.getMode());
    info.put("multiplier", t.getMultiplier());
    info.put("desiredTemperature", t.getDesiredTemperature());
    info.put("ambientTemperature", t.getAmbientTemperature());
  } 
  
}