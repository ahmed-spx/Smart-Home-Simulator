package main.java.smarthome;

public class Thermostat extends Device implements IPoweredDevice {
  private boolean isOn;


  public Thermostat(int id, String name, String location, DeviceType type) {
    super(id, name, location, type);
    this.isOn = false;
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
