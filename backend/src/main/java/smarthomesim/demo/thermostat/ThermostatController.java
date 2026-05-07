package smarthomesim.demo.Thermostat;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import smarthomesim.demo.Device;

@RestController
@RequestMapping("/api/thermostats")

public class ThermostatController {

  private final Map<String, Device> devices;

  // constructor: Spring will inject the device hub
  public ThermostatController(Map<String, Device> devices) {
    this.devices = devices;
  }

  // GET info about a thermostat
  @GetMapping("/{id}")
  public Map<String, Object> getThermostat(@PathVariable String id) {
    return thermostatInfo((Thermostat) devices.get(id));
  }

  // POST for toggle
  @PostMapping("/{id}/toggle")
  public Map<String, Object> toggleThermostat(@PathVariable String id) {
    Thermostat t = (Thermostat) devices.get(id);
    t.togglePower();
    return thermostatInfo(t);
  }

  // POST for mode
  @PostMapping("/{id}/mode")
  public Map<String, Object> setMode(@PathVariable String id,
                                     @RequestBody Map<String, String> body) {
    Thermostat t = (Thermostat) devices.get(id);
    t.setStateMode(ThermostatMode.valueOf(body.get("mode")));
    return thermostatInfo(t);
  }

  // POST for set temperature
  @PostMapping("/{id}/temperature")
  public Map<String, Object> setTemperature(@PathVariable String id,
                                            @RequestBody Map<String, Integer> body) {
    Thermostat t = (Thermostat) devices.get(id);
    t.setStateDesiredTemperature(body.get("temperature"));
    return thermostatInfo(t);
  }

  private Map<String, Object> thermostatInfo(Thermostat t) {
    Map<String, Object> m = new HashMap<>();
    m.put("id", t.getId());
    m.put("name", t.getName());
    m.put("location", t.getLocation());
    m.put("type", t.getType());
    m.put("state", t.getThermostatState());
    m.put("mode", t.getMode());
    m.put("desiredTemperature", t.getDesiredTemperature());
    m.put("ambientTemperature", t.getAmbientTemperature());
    return m;
  }
}
