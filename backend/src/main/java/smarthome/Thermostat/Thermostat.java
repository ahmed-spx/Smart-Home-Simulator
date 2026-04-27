package main.java.smarthome.Thermostat;
import main.java.smarthome.Device;
import main.java.smarthome.DeviceType;

public class Thermostat extends Device {
  //attributes
  private IThermostatState thermostat;
  private ThermostatState mode;
  private int desiredTemperature;
  private int ambientTemperature;

  //constructor
  public Thermostat(String id, String name, String location) {
    super(id, name, location, DeviceType.Thermostat);
    //this.mode = new OffThermostatState(this);
    this.desiredTemperature = 70;
  }

  //setters
  public void togglePower() {
    thermostat.togglePower(this);
  }
  public void setDesiredTemp(int temperature){
    if (temperature < 60 || temperature > 80) {
      throw new IllegalArgumentException("Temperature must be between 60 and 80");
    } else thermostat.setTemperature(this, temperature);
  }
  public void setMode(ThermostatState mode){
    thermostat.setMode(this, mode);
  }
}
