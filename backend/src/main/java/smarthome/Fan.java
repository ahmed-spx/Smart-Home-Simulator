package main.java.smarthome;

public class Fan extends Device implements IPoweredDevice {
  private boolean isOn;
  //take enum out and make it state like lights
  protected enum speed {
    Off,
    Low,
    Medium,
    High
  }

  public Fan(String id, String name, String location, DeviceType type) {
    super(id, name, location, type);
    this.isOn = false;
  }

  public void setSpeed(speed speed) {
    // Implementation to set the fan speed
  }

  @Override
  public void togglePower() {
    this.isOn = !this.isOn;
  }

  @Override
  public boolean isOn() {
    return this.isOn;
  }
}
