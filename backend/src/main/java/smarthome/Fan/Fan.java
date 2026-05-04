package main.java.smarthome.Fan;
import main.java.smarthome.Device;
import main.java.smarthome.DeviceType;

public class Fan extends Device {
  //attributes
  private IFanState fanState;
  private Speed speed;

  //constructor
  public Fan(String id, String name, String location) {
    super(id, name, location, DeviceType.Fan);
    this.fanState = new OffFanState(this);
    this.speed = Speed.Off;
  }

  //setters
  public void togglePower() {
    this.fanState.togglePower(this);
  }
  public void setSpeed(Speed speed) {
    this.fanState.setSpeed(speed);
  }

  //getters
  public FanState getFanState() {
    return fanState.getState();
  }
  public Speed getSpeed() {
    return speed;
  }
  
  //transition
  public void changeState(IFanState newState) {
    this.fanState = newState;
  }
}
