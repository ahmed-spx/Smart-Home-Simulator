package smarthomesim.demo.Thermostat;
import smarthomesim.demo.Device;
import smarthomesim.demo.DeviceType;
import smarthomesim.demo.Thermostat.ThermostatStates.OffThermostatState;

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

  //setters
  public void togglePower() {
    thermostatState.togglePower(this);
  }
  public void setStateMode(ThermostatMode mode) {
    thermostatState.setMode(this, mode);
  }
  public void setThermostatMode(ThermostatMode mode) {
    this.mode = mode;
  }
  public void setDesiredTemperature(int desiredTemperature) {
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
