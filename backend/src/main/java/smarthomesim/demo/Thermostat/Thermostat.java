package smarthomesim.demo.Thermostat;
import smarthomesim.demo.Device;
import smarthomesim.demo.DeviceType;
import smarthomesim.demo.Thermostat.ThermostatStates.OffThermostatState;

public class Thermostat extends Device {
  //attributes
  private IThermostatState thermostatState;
  private ThermostatMode mode;
  private int desiredTemperature;
  private int ambientTemperature = 65;

  //constructor
  public Thermostat(String id, String name, String location) {
    super(id, name, location, DeviceType.Thermostat);
    this.thermostatState = new OffThermostatState();
    this.desiredTemperature = 70;
    this.mode = ThermostatMode.Auto;
  }

  //setters
  public void togglePower() {
    thermostatState.togglePower(this);
  }
  public void setMode(ThermostatMode mode) {
    thermostatState.setMode(this, mode);
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
