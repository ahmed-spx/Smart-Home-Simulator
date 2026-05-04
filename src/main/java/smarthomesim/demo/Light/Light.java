package smarthomesim.demo.Light;
import java.awt.Color;
import smarthomesim.demo.Device;
import smarthomesim.demo.DeviceType;

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

  //setters
  public void togglePower() {
    lightState.togglePower(this);
  }
  public void setBrightness(int brightness) {
    lightState.setBrightness(this, brightness);
  }
  public void setColor(Color color) {
    lightState.setColor(this, color);
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