package smarthomesim.demo.thermostat;
import smarthomesim.demo.device.Device;
import smarthomesim.demo.device.DeviceType;
import smarthomesim.demo.thermostat.modes.ThermostatModeStrategy;
import smarthomesim.demo.thermostat.modes.ThermostatModes;
import smarthomesim.demo.thermostat.thermostatstates.OffThermostatState;

public class Thermostat extends Device {
  //attributes
  private IThermostatState thermostatState;
  private ThermostatMode mode;
  private int desiredTemperature;
  private int ambientTemperature;
  private Multiplier multiplier;
  private int counter;
  private ThermostatModes modes;


  //constructor
  public Thermostat(String id, String name, String location) {
    super(id, name, location, DeviceType.Thermostat);
    this.thermostatState = new OffThermostatState();
    this.desiredTemperature = 70;
    this.ambientTemperature = 65;
    this.mode = ThermostatMode.Auto;
    this.multiplier = Multiplier.x1;
    this.counter = 0;
  }
  
// strategy registry wiring
  public void setModes(ThermostatModes modes) {
    this.modes = modes;
  }
  public ThermostatModeStrategy currentModeStrategy() {
    if (modes == null) {
      throw new IllegalStateException("ThermostatModes not set on " + getId());
    }
    return modes.forMode(this.mode);
  }


  // tick counter
  public void tick() {
    counter++;
    int ticksPerTimeInterval = 10 / multiplier.getMultiplierValue();
    if (counter >= ticksPerTimeInterval) {
      counter = 0;
      thermostatState.tick(this);
    }
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
  public void setMultiplier(Multiplier multiplier) {
    this.multiplier = multiplier;
    this.counter = 0;
  }
  public void setAmbientTemperature(int ambientTemperature) {
    this.ambientTemperature = ambientTemperature;
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
  public Multiplier getMultiplier() {
    return multiplier;
  }

  //transition
  public void changeState(IThermostatState newState) {
    this.thermostatState = newState;
  }
}
