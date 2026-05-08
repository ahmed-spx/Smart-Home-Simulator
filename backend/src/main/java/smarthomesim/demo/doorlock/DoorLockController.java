package smarthomesim.demo.doorlock;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import smarthomesim.demo.device.Device;

@RestController
@RequestMapping("/api/doorlocks")

public class DoorLockController {

  private final Map<String, Device> devices;

  // constructor: Spring will inject the device hub
  public DoorLockController(Map<String, Device> devices) {
    this.devices = devices;
  }

  // GET info about a door lock
  @GetMapping("/{id}")
  public Map<String, Object> getDoorLock(@PathVariable String id) {
    return doorLockInfo((DoorLock) devices.get(id));
  }

  // POST for toggle
  @PostMapping("/{id}/toggle")
  public Map<String, Object> toggleDoorLock(@PathVariable String id) {
    DoorLock doorLock = (DoorLock) devices.get(id);
    doorLock.toggleLock();
    return doorLockInfo(doorLock);
  }

  // helper: shape the JSON the frontend receives
  private Map<String, Object> doorLockInfo(DoorLock dl) {
    Map<String, Object> m = new HashMap<>();
    m.put("id", dl.getId());
    m.put("name", dl.getName());
    m.put("location", dl.getLocation());
    m.put("type", dl.getType());
    m.put("state", dl.getDoorLockState());
    return m;
  }
}