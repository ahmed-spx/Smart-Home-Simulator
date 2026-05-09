package smarthomesim.demo.service.factory;

import java.awt.Color;
import java.util.Map;

import org.springframework.stereotype.Component;

import smarthomesim.demo.domain.Device;
import smarthomesim.demo.domain.light.Light;
import smarthomesim.demo.domain.light.OffLight;
import smarthomesim.demo.domain.light.OnLight;
import smarthomesim.demo.repository.persistence.DeviceSnapshot;

@Component
public class LightCreator implements DeviceCreator {

  @Override public String typeKey() { return "Light"; }
  @Override public Class<? extends Device> deviceClass() { return Light.class; }

  @Override
  public Device create(String id, String name, String location) {
    return new Light(id, name, location);
  }

  @Override
  public Device rehydrate(DeviceSnapshot s) {
    Light l = new Light(s.id(), s.name(), s.location());
    l.changeState("On".equals(s.state()) ? new OnLight() : new OffLight());
    l.setBrightness(s.brightness());
    l.setColor(Color.decode(s.color()));
    return l;
  }

  @Override
  public DeviceSnapshot dehydrate(Device d) {
    Light l = (Light) d;
    return new DeviceSnapshot(l.getId(), "Light", l.getName(), l.getLocation(),
        l.getLightState().name(), l.getBrightness(), l.getColorHex(),
        null, null, null, null, null);
  }

  @Override public String idPrefix() { return "light"; }

  @Override
  public void addCustomFields(Device device, Map<String, Object> info) {
    Light l = (Light) device;
    info.put("state", l.getLightState());
    info.put("brightness", l.getBrightness());
    info.put("color", l.getColorHex());
  }
  
}