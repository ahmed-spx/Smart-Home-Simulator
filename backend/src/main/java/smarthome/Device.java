package main.java.smarthome;

public abstract class Device {
  private int id;
  private String name;
  private String location;
  private DeviceType type;

  protected enum DeviceType {
    Light,
    Fan,
    Thermostat,
    DoorLock
  }
  public Device(int id, String name, String location, DeviceType type) {
    this.id = id;
    this.name = name;
    this.location = location;
    this.type = type;
  }
}
