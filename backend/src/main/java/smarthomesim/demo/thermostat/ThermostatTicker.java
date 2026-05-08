package smarthomesim.demo.thermostat;
import java.util.Map;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import smarthomesim.demo.device.Device;

@Component
public class ThermostatTicker {

    private final Map<String, Device> devices;

    public ThermostatTicker(Map<String, Device> devices) {
        this.devices = devices;
    }

    @Scheduled(fixedRate = 500)
    public void tickAll() {
        for (Device device : devices.values()) {
            if (device instanceof Thermostat thermostat) {
                thermostat.tick();
            }
        }
    }
}
