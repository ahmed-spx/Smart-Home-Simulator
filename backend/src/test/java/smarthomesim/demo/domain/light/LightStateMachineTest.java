package smarthomesim.demo.domain.light;

import static org.assertj.core.api.Assertions.assertThat;

import java.awt.Color;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the Light state machine.
 *
 * Light implements the State pattern via {@link ILightState}.
 * Concrete states: {@link OffLight}, {@link OnLight}.
 *
 * The only state-transition operation exposed on Light is {@code togglePower()}.
 * The "invalid On→On / Off→Off" cases the spec mentions are structurally
 * impossible because the API itself only exposes a toggle: there is no
 * setOn() / setOff(). The test below documents that toggling always changes
 * the state — the State pattern guarantees the invariant.
 */
@DisplayName("Light state machine")
class LightStateMachineTest {

  private Light newLight() {
    return new Light("light-test", "Test Light", "Living Room");
  }

  @Nested
  @DisplayName("Initial state")
  class Initial {

    @Test
    void newLight_DefaultState_IsOff() {
      assertThat(newLight().getLightState()).isEqualTo(LightState.Off);
    }

    @Test
    void newLight_DefaultBrightness_Is100() {
      assertThat(newLight().getBrightness()).isEqualTo(100);
    }

    @Test
    void newLight_DefaultColor_IsWhite() {
      assertThat(newLight().getColorHex()).isEqualToIgnoringCase("#FFFFFF");
    }
  }

  @Nested
  @DisplayName("togglePower transitions")
  class Toggle {

    @Test
    void togglePower_FromOff_TransitionsToOn() {
      Light light = newLight();
      light.togglePower();
      assertThat(light.getLightState()).isEqualTo(LightState.On);
    }

    @Test
    void togglePower_FromOn_TransitionsToOff() {
      Light light = newLight();
      light.togglePower(); // Off -> On
      light.togglePower(); // On -> Off
      assertThat(light.getLightState()).isEqualTo(LightState.Off);
    }

    @Test
    void togglePower_FromOnTwice_AlwaysChangesState() {
      // Off→On→Off proves the State pattern never lets us "stay On" via toggle.
      Light light = newLight();
      light.togglePower();
      assertThat(light.getLightState()).isEqualTo(LightState.On);
      light.togglePower();
      assertThat(light.getLightState()).isEqualTo(LightState.Off);
    }
  }

  @Nested
  @DisplayName("Brightness state-aware behavior")
  class StateBrightness {

    @Test
    void setStateBrightness_WhileOn_UpdatesBrightness() {
      Light light = newLight();
      light.togglePower(); // On
      light.setStateBrightness(55);
      assertThat(light.getBrightness()).isEqualTo(55);
    }

    @Test
    void setStateBrightness_WhileOff_LeavesBrightnessUnchanged() {
      Light light = newLight();
      light.setStateBrightness(55);
      assertThat(light.getBrightness())
          .as("OffLight must not mutate brightness — the command is silently ignored")
          .isEqualTo(100);
    }
  }

  @Nested
  @DisplayName("Color state-aware behavior")
  class StateColor {

    @Test
    void setStateColor_WhileOn_UpdatesColor() {
      Light light = newLight();
      light.togglePower();
      light.setStateColor(Color.RED);
      assertThat(light.getColorHex()).isEqualToIgnoringCase("#FF0000");
    }

    @Test
    void setStateColor_WhileOff_LeavesColorUnchanged() {
      Light light = newLight();
      light.setStateColor(Color.RED);
      assertThat(light.getColorHex())
          .as("OffLight must not mutate color")
          .isEqualToIgnoringCase("#FFFFFF");
    }
  }

  @Nested
  @DisplayName("Settings retention across power cycle")
  class Retention {

    @Test
    void brightness_SurvivesOffOnCycle() {
      Light light = newLight();
      light.togglePower();
      light.setStateBrightness(42);
      light.togglePower();        // -> Off
      light.togglePower();        // -> On
      assertThat(light.getBrightness()).isEqualTo(42);
    }

    @Test
    void color_SurvivesOffOnCycle() {
      Light light = newLight();
      light.togglePower();
      light.setStateColor(Color.GREEN);
      light.togglePower();
      light.togglePower();
      assertThat(light.getColorHex()).isEqualToIgnoringCase("#00FF00");
    }
  }
}
