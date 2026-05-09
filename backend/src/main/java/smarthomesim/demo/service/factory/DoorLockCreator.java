package smarthomesim.demo.service.factory;

import java.util.Map;

import org.springframework.stereotype.Component;

import smarthomesim.demo.domain.Device;
import smarthomesim.demo.domain.doorlock.DoorLock;
import smarthomesim.demo.domain.doorlock.LockedState;
import smarthomesim.demo.domain.doorlock.UnlockedState;
import smarthomesim.demo.repository.persistence.DeviceSnapshot;

@Component
public class DoorLockCreator implements DeviceCreator {

  @Override public String typeKey() { return "DoorLock"; }
  @Override public Class<? extends Device> deviceClass() { return DoorLock.class; }

  @Override
  public Device create(String id, String name, String location) {
    return new DoorLock(id, name, location);
  }

  @Override
  public Device rehydrate(DeviceSnapshot s) {
    DoorLock dl = new DoorLock(s.id(), s.name(), s.location());
    dl.changeState("Locked".equals(s.state()) ? new LockedState() : new UnlockedState());
    return dl;
  }

  @Override
  public DeviceSnapshot dehydrate(Device d) {
    DoorLock dl = (DoorLock) d;
    return new DeviceSnapshot(dl.getId(), "DoorLock", dl.getName(), dl.getLocation(),
        dl.getDoorLockState().name(), null, null, null, null, null, null, null);
  }

  @Override public String idPrefix() { return "lock"; }

  @Override
  public void addCustomFields(Device device, Map<String, Object> info) {
    DoorLock dl = (DoorLock) device;
    info.put("state", dl.getDoorLockState());
  }
  
}