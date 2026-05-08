package smarthomesim.demo.thermostat;

import java.util.Map;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import smarthomesim.demo.device.Device;
import smarthomesim.demo.persistence.DeviceStore;

@Component
public class ThermostatTicker {

  private final Map<String, Device> devices;
  private final DeviceStore store;

  public ThermostatTicker(Map<String, Device> devices, DeviceStore store) {
    this.devices = devices;
    this.store = store;
  }

  @Scheduled(fixedRate = 500)
  public void tickAll() {
    boolean changed = false;
    for (Device device : devices.values()) {
      if (device instanceof Thermostat t) {
        ThermostatState beforeState = t.getThermostatState();
        int beforeAmbient = t.getAmbientTemperature();
        t.tick();
        if (t.getThermostatState() != beforeState || t.getAmbientTemperature() != beforeAmbient) {
          changed = true;
        }
      }
    }
    if (changed) store.save();
  }
}