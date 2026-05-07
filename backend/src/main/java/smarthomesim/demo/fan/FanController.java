package smarthomesim.demo.Fan;
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
@RequestMapping("/api/fans")

public class FanController {

  private final Map<String, Device> devices;

    // constructor: Spring will inject the device hub
  public FanController(Map<String, Device> devices) {
    this.devices = devices;
  }

  // GET info about a fan
  @GetMapping("/{id}")
  public Map<String, Object> getFan(@PathVariable String id) {
    return fanInfo((Fan) devices.get(id));
  }

  // POST for toggle
  @PostMapping("/{id}/toggle")
  public Map<String, Object> toggleFan(@PathVariable String id) {
    Fan fan = (Fan) devices.get(id);
    fan.togglePower();
    return fanInfo(fan);
  }

  // POST for speed
  @PostMapping("/{id}/speed")
  public Map<String, Object> setSpeed(@PathVariable String id,
                                      @RequestBody Map<String, String> body) {
    Fan fan = (Fan) devices.get(id);
    fan.setSpeed(Speed.valueOf(body.get("speed")));
    return fanInfo(fan);
  }

  private Map<String, Object> fanInfo(Fan f) {
    Map<String, Object> m = new HashMap<>();
    m.put("id", f.getId());
    m.put("name", f.getName());
    m.put("location", f.getLocation());
    m.put("type", f.getType());
    m.put("state", f.getFanState());
    m.put("speed", f.getSpeed());
    return m;
  }
}