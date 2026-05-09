package smarthomesim.demo.factory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import smarthomesim.demo.device.Device;
import smarthomesim.demo.persistence.DeviceSnapshot;

@Component
public class DeviceFactory {

  private final Map<String, DeviceCreator> byKey;
  private final Map<Class<? extends Device>, DeviceCreator> byClass;

  public DeviceFactory(List<DeviceCreator> creatorBeans) {
    this.byKey = creatorBeans.stream()
        .collect(Collectors.toMap(DeviceCreator::typeKey, c -> c));
    this.byClass = creatorBeans.stream()
        .collect(Collectors.toMap(DeviceCreator::deviceClass, c -> c));
  }

  public Device create(String type, String id, String name, String location) {
    return resolveByKey(type).create(id, name, location);
  }

  public Device rehydrate(DeviceSnapshot snapshot) {
    return resolveByKey(snapshot.type()).rehydrate(snapshot);
  }

  public DeviceSnapshot dehydrate(Device device) {
    DeviceCreator c = byClass.get(device.getClass());
    if (c == null) {
      throw new IllegalArgumentException(
          "No creator registered for class: " + device.getClass().getName());
    }
    return c.dehydrate(device);
  }

  private DeviceCreator resolveByKey(String key) {
    DeviceCreator c = byKey.get(key);
    if (c == null) throw new IllegalArgumentException("Unknown device type: " + key);
    return c;
  }

  public Map<String, Object> view(Device device) {
    DeviceCreator c = byClass.get(device.getClass());
    if (c == null) {
      throw new IllegalArgumentException("No creator registered for class: " + device.getClass().getName());
    }
    return c.view(device);
  }

  public String nextId(String type, java.util.Set<String> existingIds) {
    String prefix = resolveByKey(type).idPrefix();
    int max = existingIds.stream()
        .filter(id -> id.startsWith(prefix + "-"))
        .map(id -> id.substring(prefix.length() + 1))
        .filter(s -> s.matches("\\d+"))
        .mapToInt(Integer::parseInt)
        .max()
        .orElse(0);
    return prefix + "-" + (max + 1);
  }
}