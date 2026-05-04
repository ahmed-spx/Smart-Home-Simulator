package main.java.smarthome.Light;
import java.awt.Color;

public class OnLight implements ILightState {
  @Override
  public LightState getState() {
    return LightState.On;
  }

  @Override
  public void togglePower(Light light) {
    light.changeState(new OffLight());
  }

  @Override
  public void setBrightness(Light light, int brightness) {
    light.setBrightness(brightness);
  }

  @Override
  public void setColor(Light light, Color color) {
    light.setColor(color);
  }
}

