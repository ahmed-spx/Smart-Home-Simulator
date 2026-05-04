package main.java.smarthome.Light;
import java.awt.Color;

public class OffLight implements ILightState {

  @Override
  public LightState getState() {
    return LightState.Off;
  }

  @Override
  public void togglePower(Light light) {
    light.changeState(new OnLight());
  }

  @Override
  public void setBrightness(Light light, int brightness) {
  }

  @Override
  public void setColor(Light light, Color color) {
  }
  
}
