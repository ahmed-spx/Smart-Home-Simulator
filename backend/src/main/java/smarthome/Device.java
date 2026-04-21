package main.java.smarthome;

public abstract class Device {
  private String id;
  private String name;
  private String location;
  private DeviceType type;

  private enum DeviceType {
    Light,
    Fan,
    Thermostat,
    DoorLock
  }
  public Device(String id, String name, String location, DeviceType type) {
    this.id = id;
    this.name = name;
    this.location = location;
    this.type = type;
  }
}
