package main.java.smarthome;

public class Fan extends Device implements IPoweredDevice {
  private boolean isOn;
  private int speed; // 0 for off, 1 for low, 2 for medium, 3 for high

  public Fan(int id, String name, String location, DeviceType type) {
    super(id, name, location, type);
    this.isOn = false;
  }

  public void setSpeed(int speed) {
    this.speed = speed;
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
