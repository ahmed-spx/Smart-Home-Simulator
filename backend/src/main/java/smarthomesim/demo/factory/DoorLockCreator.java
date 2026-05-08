package smarthomesim.demo.factory;

import org.springframework.stereotype.Component;
import smarthomesim.demo.device.Device;
import smarthomesim.demo.doorlock.DoorLock;
import smarthomesim.demo.doorlock.LockedState;
import smarthomesim.demo.doorlock.UnlockedState;
import smarthomesim.demo.persistence.DeviceSnapshot;

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
}