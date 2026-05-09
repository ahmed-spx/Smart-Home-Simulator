package smarthomesim.demo.factory;

import java.util.HashMap;
import java.util.Map;
import smarthomesim.demo.device.Device;
import smarthomesim.demo.persistence.DeviceSnapshot;

// Per-type creation, persistence, and presentation rules for one device subtype.
// Implementations are auto-discovered by {@link DeviceFactory} via Spring DI.

// Adding a new device type means: implement this interface as a Component,
// supply the type's class, id-prefix, construction logic, and snapshot mapping.
// No central registry edits required.

public interface DeviceCreator {
  String typeKey();
  String idPrefix();
  Class<? extends Device> deviceClass();
  Device create(String id, String name, String location);
  Device rehydrate(DeviceSnapshot snapshot);
  DeviceSnapshot dehydrate(Device device);
  void addCustomFields(Device device, Map<String, Object> info);

  default Map<String, Object> view(Device device) {
    Map<String, Object> info = new HashMap<>();
    info.put("id", device.getId());
    info.put("name", device.getName());
    info.put("location", device.getLocation());
    info.put("type", device.getType());
    addCustomFields(device, info);
    return info;
  }
}