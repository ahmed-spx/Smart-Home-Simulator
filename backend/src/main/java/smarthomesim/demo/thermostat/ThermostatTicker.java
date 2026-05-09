package smarthomesim.demo.thermostat;

import java.util.Map;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import smarthomesim.demo.device.Device;
import smarthomesim.demo.events.DeviceStateChangedEvent;

@Component
public class ThermostatTicker {

  private final Map<String, Device> devices;
  private final ApplicationEventPublisher publisher;

  public ThermostatTicker(Map<String, Device> devices, ApplicationEventPublisher publisher) {
    this.devices = devices;
    this.publisher = publisher;
  }

  @Scheduled(fixedRate = 500)
  public void tickAll() {
    for (Device device : devices.values()) {
      if (device instanceof Thermostat t) {
        ThermostatState before = t.getThermostatState();
        int beforeAmbient = t.getAmbientTemperature();
        t.tick();
        if (t.getThermostatState() != before
            || t.getAmbientTemperature() != beforeAmbient) {
          publisher.publishEvent(new DeviceStateChangedEvent(t.getId(), "tick"));
        }
      }
    }
  }
}