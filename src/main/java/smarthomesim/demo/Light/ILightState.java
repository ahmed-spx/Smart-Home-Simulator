package smarthomesim.demo.Light;
import java.awt.Color;

public interface ILightState {
  public LightState getState();
  public void togglePower(Light light);
  public void setBrightness(Light light, int brightness);
  public void setColor(Light light, Color color);
}
