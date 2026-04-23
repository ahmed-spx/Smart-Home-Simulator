package main.java.smarthome.Thermostat;

import main.java.smarthome.Device;
import main.java.smarthome.DeviceType;

public class Thermostat extends Device {
  //attributes
  private IThermostatState mode;
  private int desiredTemperature;

  //constructor
  public Thermostat(String id, String name, String location) {
    super(id, name, location, DeviceType.Thermostat);
    //this.mode = new OffThermostatState(this);
    this.desiredTemperature = 70;
  }

  //setters
  public void togglePower() {
    mode.togglePower(this);
  }
}
