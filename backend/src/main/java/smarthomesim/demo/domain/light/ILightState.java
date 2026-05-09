package smarthomesim.demo.domain.light;
import java.awt.Color;

// State strategy for Light. Each implementation represents one power
// state and defines how the light reacts to commands while in that state.

public interface ILightState {
  public LightState getState();
  public void togglePower(Light light);
  public void setBrightness(Light light, int brightness);
  public void setColor(Light light, Color color);
}
