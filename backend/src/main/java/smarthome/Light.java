package main.java.smarthome;
import java.awt.Color;

public class Light extends Device implements IPoweredDevice{
  private boolean isOn;

  private int brightness = 100;
  private Color color = new Color(255, 255, 255);

  public Light(int id, String name, String location, DeviceType type) {
    super(id, name, location, type);
    this.isOn = false;
  }

  public void setBrightness(int brightness) {
    if (brightness < 10 || brightness > 100) {
      throw new IllegalArgumentException("Brightness must be between 10 and 100");
    } else {
      this.brightness = brightness;
    }
  }

  public void setColor(Color color) {
    this.color = color;
  }

  @Override
  public void togglePower() {
    this.isOn = !this.isOn;
  }

  @Override
  public boolean isOn() {
    return this.isOn;
  }
}
