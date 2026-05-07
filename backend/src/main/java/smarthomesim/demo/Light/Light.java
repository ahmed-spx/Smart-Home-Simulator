package smarthomesim.demo.light;
import java.awt.Color;
import smarthomesim.demo.device.Device;
import smarthomesim.demo.device.DeviceType;

public class Light extends Device {
  //fields
  private ILightState lightState;
  private int brightness;
  private Color color;

  //constructor
  public Light(String id, String name, String location) {
    super(id, name, location, DeviceType.Light);
    this.lightState = new OnLight();
    this.brightness = 100;
    this.color = Color.WHITE;
  }

  //state machine setters
  public void togglePower() {
    lightState.togglePower(this);
  }
  public void setStateBrightness(int brightness) {
    lightState.setBrightness(this, brightness);
  }
  public void setStateColor(Color color) {
    lightState.setColor(this, color);
  }

  //setters
  public void setBrightness(int brightness) {
    this.brightness = brightness;
  }
  public void setColor(Color color) {
    this.color = color;
  }

  //getters
  public LightState getLightState() {
    return lightState.getState();
  }
  public int getBrightness() {
    return brightness;
  }
  public Color getColor() {
    return color;
  }

  //transition
  public void changeState(ILightState newState) {
    this.lightState = newState;
  }
}