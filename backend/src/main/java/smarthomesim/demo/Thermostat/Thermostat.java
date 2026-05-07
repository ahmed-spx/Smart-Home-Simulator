package smarthomesim.demo.thermostat;
import smarthomesim.demo.device.Device;
import smarthomesim.demo.device.DeviceType;
import smarthomesim.demo.thermostat.thermostatstates.OffThermostatState;

public class Thermostat extends Device {
  //attributes
  private IThermostatState thermostatState;
  private ThermostatMode mode;
  private int desiredTemperature;
  private int ambientTemperature;

  //constructor
  public Thermostat(String id, String name, String location) {
    super(id, name, location, DeviceType.Thermostat);
    this.thermostatState = new OffThermostatState();
    this.desiredTemperature = 70;
    this.ambientTemperature = 65;
    this.mode = ThermostatMode.Auto;
  }
  
  //state machine setters
  public void togglePower() {
    thermostatState.togglePower(this);
  }
  public void setStateDesiredTemperature(int desiredTemperature) {
    thermostatState.setTemperature(this, desiredTemperature);
  }
    public void setStateMode(ThermostatMode mode) {
    thermostatState.setMode(this, mode);
  }

  //thermostat setters
  public void setThermostatMode(ThermostatMode mode) {
    this.mode = mode;
  }
  public void setThermostatDesiredTemperature(int desiredTemperature) {
    this.desiredTemperature = desiredTemperature;
  }
  public void increaseAmbientTemperature() {
    this.ambientTemperature++;
  }
  public void decreaseAmbientTemperature() {
    this.ambientTemperature--;
  }

  //getters
  public ThermostatState getThermostatState() {
    return thermostatState.getState();
  }
  public int getDesiredTemperature() {
    return desiredTemperature;
  }
  public int getAmbientTemperature() {
    return ambientTemperature;
  }
  public ThermostatMode getMode() {
    return mode;
  }

  //transition
  public void changeState(IThermostatState newState) {
    this.thermostatState = newState;
  }
}
