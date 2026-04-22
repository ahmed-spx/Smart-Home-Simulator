package main.java.smarthome;

public class Thermostat extends Device implements IPoweredDevice {
  private boolean isOn;
  private int mode;
  private double desiredTemperature;


  public Thermostat(String id, String name, String location, DeviceType type) {
    super(id, name, location, type);
    this.isOn = false;
  }

  public void setMode(int mode) {
    this.mode = mode;
  }

  public void setTemperature(double temperature) {
    desiredTemperature = temperature;
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
