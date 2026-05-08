package smarthomesim.demo.factory;

import smarthomesim.demo.device.Device;
import smarthomesim.demo.persistence.DeviceSnapshot;

public interface DeviceCreator {
  String typeKey();
  Class<? extends Device> deviceClass();
  Device create(String id, String name, String location);
  Device rehydrate(DeviceSnapshot snapshot);
  DeviceSnapshot dehydrate(Device device);
}