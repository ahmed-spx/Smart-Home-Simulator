package main.java.smarthome;
import java.awt.Color;

public class Light extends Device implements IPoweredDevice{
  //fields
  private LightState state;
  private int brightness = 100;
  private Color color = new Color(255, 255, 255);

  //constructor
  public Light(String id, String name, String location) {
    super(id, name, location, DeviceType.Light);
    this.state = LightState.Off;
    this.brightness = brightness;
    this.color = color;
  }

  //setters
  public void setBrightness(int brightness) {
    if (brightness < 10 || brightness > 100) {
      throw new IllegalArgumentException("Brightness must be between 10 and 100");
    }
    this.brightness = brightness;
  }
  public void setColor(Color color) {
    this.color = color;
  }

  //getters
  public LightState getState() {
    return state;
  }
  public int getBrightness() {
    return brightness;
  }
  public Color getColor() {
    return color;
  }

  //overrides
  @Override
  public void togglePower() {
    this.state = this.state == LightState.On ? LightState.Off : LightState.On;
  }
  @Override
  public boolean isOn() {
    return state == LightState.On;
  }
}

//TODO: cant change brightness or color while off