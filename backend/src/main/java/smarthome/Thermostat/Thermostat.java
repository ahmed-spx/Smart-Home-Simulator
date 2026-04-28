package main.java.smarthome.Thermostat;
import main.java.smarthome.Device;
import main.java.smarthome.DeviceType;

public class Thermostat extends Device {
  //attributes
  private IThermostatState thermostatState;
  private ThermostatState mode;
  private int desiredTemperature;
  //private int ambientTemperature;

  //constructor
  public Thermostat(String id, String name, String location) {
    super(id, name, location, DeviceType.Thermostat);
    this.thermostatState = new OffThermostatState();
    this.desiredTemperature = 70;
  }

  //setters
  public void togglePower() {
    thermostatState.togglePower(this);
  }
  public void setDesiredTemp(int temperature){
    if (temperature < 60 || temperature > 80) {
      throw new IllegalArgumentException("Temperature must be between 60 and 80");
    } else thermostatState.setTemperature(this, temperature);
  }
  public void setMode(ThermostatState mode){
    thermostatState.setMode(this, mode);
  }

  //getters
  public ThermostatState getThermostatState() {
    return thermostatState.getState();
  }
  public int getDesiredTemperature() {
    return desiredTemperature;
  }

  //transition
  public void changeState(IThermostatState newState) {
    this.thermostatState = newState;
  }
}
