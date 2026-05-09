package smarthomesim.demo.persistence;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.stereotype.Component;
import org.springframework.context.event.EventListener;

import smarthomesim.demo.device.Device;
import smarthomesim.demo.factory.DeviceFactory;
import smarthomesim.demo.events.DeviceMutationEvent;

@Component
public class DeviceStore {

  private static final File STATE_FILE = new File("data/state.json");
  private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
  private final DeviceFactory factory;
  private Map<String, Device> hub;

  public DeviceStore(DeviceFactory factory) {
    this.factory = factory;
  }

  @EventListener
  public void onMutation(DeviceMutationEvent event) {
    save();
  }
  
  public Map<String, Device> load() {
    if (!STATE_FILE.exists()) {
      hub = seed();
      save();
      return hub;
    }
    try {
      List<DeviceSnapshot> snapshots = mapper.readValue(STATE_FILE,
          mapper.getTypeFactory().constructCollectionType(List.class, DeviceSnapshot.class));
      hub = new LinkedHashMap<>();
      for (DeviceSnapshot s : snapshots) hub.put(s.id(), factory.rehydrate(s));
      return hub;
    } catch (IOException e) {
      throw new RuntimeException("Failed to load " + STATE_FILE, e);
    }
  }

  public synchronized void addDevice(Device d) {
    hub.put(d.getId(), d);
    save();
  }

  public synchronized void removeDevice(String id) {
    hub.remove(id);
    save();
  }

  public synchronized void save() {
    try {
      STATE_FILE.getParentFile().mkdirs();
      List<DeviceSnapshot> snapshots = hub.values().stream().map(factory::dehydrate).toList();
      mapper.writeValue(STATE_FILE, snapshots);
    } catch (IOException e) {
      throw new RuntimeException("Failed to save " + STATE_FILE, e);
    }
  }

  private Map<String, Device> seed() {
    Map<String, Device> m = new LinkedHashMap<>();
    // Living Room
    m.put("light-1",  factory.create("Light",      "light-1",  "Overhead Light",        "Living Room"));
    m.put("light-2",  factory.create("Light",      "light-2",  "Hallway Light",            "Living Room"));
    m.put("fan-1",    factory.create("Fan",        "fan-1",    "Ceiling Fan",           "Living Room"));
    m.put("thermo-1", factory.create("Thermostat", "thermo-1", "Thermostat",            "Living Room"));
    m.put("lock-1",   factory.create("DoorLock",   "lock-1",   "Front Door",            "Living Room"));
    // Kitchen
    m.put("light-3",  factory.create("Light",      "light-3",  "Kitchen Lights",  "Kitchen"));
    m.put("fan-2",    factory.create("Fan",        "fan-2",    "Kitchen Fan",           "Kitchen"));
    m.put("lock-2",   factory.create("DoorLock",   "lock-2",   "Back Door",             "Kitchen"));
    // Bedroom
    m.put("light-4",  factory.create("Light",      "light-4",  "Bedroom Lights",          "Bedroom"));
    m.put("lock-3",   factory.create("DoorLock",   "lock-3",   "Room Door",             "Bedroom"));
    return m;
  }
}