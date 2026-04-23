package main.java.smarthome.Light;
import java.awt.Color;
import main.java.smarthome.Device;
import main.java.smarthome.DeviceType;

public class Light extends Device {
  //fields
  private ILightState lightState;
  private int brightness;
  private Color color;

  //constructor
  public Light(String id, String name, String location) {
    super(id, name, location, DeviceType.Light);
    this.lightState = new OffLight();
    this.brightness = 100;
    this.color = Color.WHITE;
  }

  //setters
  public void togglePower() {
    lightState.togglePower(this);
  }

  public void setBrightness(int brightness) {
    if (brightness < 10 || brightness > 100) {
      throw new IllegalArgumentException("Brightness must be between 10 and 100");
    } else lightState.setBrightness(this, brightness);
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

//TODO: cant change brightness or color while off