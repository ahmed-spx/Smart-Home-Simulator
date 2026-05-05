package smarthomesim.demo;

public abstract class Device {
  //fields
  private final String id;
  private final String name;
  private final String location;
  private final DeviceType type;

  //constructor
  public Device(String id, String name, String location, DeviceType type) {
    this.id = id;
    this.name = name;
    this.location = location;
    this.type = type;
  }

  //getters
  public String getId() {
    return id;
  }
  public String getName() {
    return name;
  }
  public String getLocation() {
    return location;
  }
  public DeviceType getType() {
    return type;
  }
}
