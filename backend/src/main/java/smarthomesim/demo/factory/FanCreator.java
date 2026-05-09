package smarthomesim.demo.factory;

import java.util.Map;

import org.springframework.stereotype.Component;
import smarthomesim.demo.device.Device;
import smarthomesim.demo.fan.Fan;
import smarthomesim.demo.fan.OffFanState;
import smarthomesim.demo.fan.OnFanState;
import smarthomesim.demo.fan.Speed;
import smarthomesim.demo.persistence.DeviceSnapshot;

@Component
public class FanCreator implements DeviceCreator {

  @Override public String typeKey() { return "Fan"; }
  @Override public Class<? extends Device> deviceClass() { return Fan.class; }

  @Override
  public Device create(String id, String name, String location) {
    return new Fan(id, name, location);
  }

  @Override
  public Device rehydrate(DeviceSnapshot s) {
    Fan f = new Fan(s.id(), s.name(), s.location());
    f.changeState("On".equals(s.state()) ? new OnFanState() : new OffFanState());
    f.setSpeed(Speed.valueOf(s.speed()));
    return f;
  }

  @Override
  public DeviceSnapshot dehydrate(Device d) {
    Fan f = (Fan) d;
    return new DeviceSnapshot(f.getId(), "Fan", f.getName(), f.getLocation(),
        f.getFanState().name(), null, null, f.getSpeed().name(),
        null, null, null, null);
  }

  @Override public String idPrefix() { return "fan"; }

  @Override
  public void addCustomFields(Device device, Map<String, Object> info) {
    Fan f = (Fan) device;
    info.put("state", f.getFanState());
    info.put("speed", f.getSpeed());
  }
  
}