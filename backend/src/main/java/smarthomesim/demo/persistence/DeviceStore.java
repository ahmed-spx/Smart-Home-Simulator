package smarthomesim.demo.persistence;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.stereotype.Component;

import smarthomesim.demo.device.Device;
import smarthomesim.demo.doorlock.*;
import smarthomesim.demo.fan.*;
import smarthomesim.demo.light.*;
import smarthomesim.demo.thermostat.*;
import smarthomesim.demo.thermostat.thermostatstates.*;
import smarthomesim.demo.thermostat.modes.ThermostatModes;

@Component
public class DeviceStore {

  private static final File STATE_FILE = new File("data/state.json");
  private final ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
  private Map<String, Device> hub;
  private final ThermostatModes modes;

  public DeviceStore(ThermostatModes modes) {
    this.modes = modes;
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
      for (DeviceSnapshot s : snapshots) hub.put(s.id(), rehydrate(s));
      return hub;
    } catch (IOException e) {
      throw new RuntimeException("Failed to load " + STATE_FILE, e);
    }
  }

  public synchronized void save() {
    try {
      STATE_FILE.getParentFile().mkdirs();
      List<DeviceSnapshot> snapshots = hub.values().stream().map(this::dehydrate).toList();
      mapper.writeValue(STATE_FILE, snapshots);
    } catch (IOException e) {
      throw new RuntimeException("Failed to save " + STATE_FILE, e);
    }
  }

  private DeviceSnapshot dehydrate(Device d) {
    if (d instanceof Light l) {
      return new DeviceSnapshot(l.getId(), "Light", l.getName(), l.getLocation(),
          l.getLightState().name(), l.getBrightness(), l.getColorHex(),
          null, null, null, null, null);
    }
    if (d instanceof Fan f) {
      return new DeviceSnapshot(f.getId(), "Fan", f.getName(), f.getLocation(),
          f.getFanState().name(), null, null, f.getSpeed().name(),
          null, null, null, null);
    }
    if (d instanceof Thermostat t) {
      return new DeviceSnapshot(t.getId(), "Thermostat", t.getName(), t.getLocation(),
          t.getThermostatState().name(), null, null, null,
          t.getMode().name(), t.getMultiplier().name(),
          t.getDesiredTemperature(), t.getAmbientTemperature());
    }
    if (d instanceof DoorLock dl) {
      return new DeviceSnapshot(dl.getId(), "DoorLock", dl.getName(), dl.getLocation(),
          dl.getDoorLockState().name(), null, null, null, null, null, null, null);
    }
    throw new IllegalStateException("Unknown device type: " + d.getClass());
  }

  private Device rehydrate(DeviceSnapshot s) {
    return switch (s.type()) {
      case "Light" -> {
        Light l = new Light(s.id(), s.name(), s.location());
        l.changeState("On".equals(s.state()) ? new OnLight() : new OffLight());
        l.setBrightness(s.brightness());
        l.setColor(Color.decode(s.color()));
        yield l;
      }
      case "Fan" -> {
        Fan f = new Fan(s.id(), s.name(), s.location());
        f.changeState("On".equals(s.state()) ? new OnFanState() : new OffFanState());
        f.setSpeed(Speed.valueOf(s.speed()));
        yield f;
      }
      case "Thermostat" -> {
        Thermostat t = new Thermostat(s.id(), s.name(), s.location());
        t.setModes(modes);
        t.changeState(thermostatStateFor(s.state()));
        t.setThermostatMode(ThermostatMode.valueOf(s.mode()));
        t.setMultiplier(Multiplier.valueOf(s.multiplier()));
        t.setThermostatDesiredTemperature(s.desiredTemperature());
        t.setAmbientTemperature(s.ambientTemperature());
        yield t;
      }
      case "DoorLock" -> {
        DoorLock dl = new DoorLock(s.id(), s.name(), s.location());
        dl.changeState("Locked".equals(s.state()) ? new LockedState() : new UnlockedState());
        yield dl;
      }
      default -> throw new IllegalStateException("Unknown device type: " + s.type());
    };
  }

  private IThermostatState thermostatStateFor(String state) {
    return switch (state) {
      case "Idle" -> new IdleThermostatState();
      case "Heating" -> new HeatingThermostatState();
      case "Cooling" -> new CoolingThermostatState();
      default -> new OffThermostatState();
    };
  }

  private Map<String, Device> seed() {
    Map<String, Device> m = new LinkedHashMap<>();
    // create the thermostat separately so we can set its modes before putting it in the hub cuz "strategy pattern"
    Thermostat thermo1 = new Thermostat("thermo-1", "Thermostat", "Living Room");
    thermo1.setModes(modes);

    // Living Room
    m.put("light-1", new Light("light-1", "Overhead Light", "Living Room"));
    m.put("light-2", new Light("light-2", "Floor Lamp", "Living Room"));
    m.put("fan-1", new Fan("fan-1", "Ceiling Fan", "Living Room"));
    m.put("thermo-1", thermo1);
    m.put("lock-1", new DoorLock("lock-1", "Front Door", "Living Room"));

    // Kitchen
    m.put("light-3", new Light("light-3", "Under-Cabinet Lights", "Kitchen"));
    m.put("fan-2", new Fan("fan-2", "Exhaust Fan", "Kitchen"));
    m.put("lock-2", new DoorLock("lock-2", "Back Door", "Kitchen"));

    // Bedroom
    m.put("light-4", new Light("light-4", "Bedside Lamp", "Bedroom"));
    m.put("lock-3", new DoorLock("lock-3", "Room Door", "Bedroom"));
    return m;
  }
}