package main.java.smarthome;
import java.awt.Color;

public class Light extends Device implements IPoweredDevice{
  private boolean isOn;

  private int brightness;
  final int limitedValue = Math.clamp(brightness, 10, 100);
  
  private Color color = new Color(255, 255, 255);

  public Light(int id, String name, String location, DeviceType type) {
    super(id, name, location, type);
    this.isOn = false;
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
