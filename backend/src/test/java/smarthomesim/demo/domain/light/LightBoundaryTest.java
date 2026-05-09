package smarthomesim.demo.domain.light;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import smarthomesim.demo.domain.exception.DomainException;

/**
 * Boundary tests for Light.setBrightness().
 *
 * Validation lives in {@link Light#setBrightness(int)} — both raw setter calls
 * and state-aware calls (via {@code OnLight}) ultimately funnel through it.
 * Range: [10, 100] inclusive. Anything outside throws {@link DomainException}.
 */
@DisplayName("Light brightness boundaries")
class LightBoundaryTest {

  private Light onLight() {
    Light l = new Light("light-test", "Test", "Living Room");
    l.togglePower(); // On
    return l;
  }

  @Test
  void setBrightness_AtMinimum10_Succeeds() {
    Light light = onLight();
    assertThatNoException().isThrownBy(() -> light.setBrightness(10));
    assertThat(light.getBrightness()).isEqualTo(10);
  }

  @Test
  void setBrightness_AtMaximum100_Succeeds() {
    Light light = onLight();
    assertThatNoException().isThrownBy(() -> light.setBrightness(100));
    assertThat(light.getBrightness()).isEqualTo(100);
  }

  @Test
  void setBrightness_MidRange50_Succeeds() {
    Light light = onLight();
    light.setBrightness(50);
    assertThat(light.getBrightness()).isEqualTo(50);
  }

  @ParameterizedTest(name = "setBrightness({0}) → DomainException")
  @ValueSource(ints = {9, 0, -1, -100, 101, 150, Integer.MAX_VALUE, Integer.MIN_VALUE})
  void setBrightness_OutOfRange_ThrowsDomainException(int invalid) {
    Light light = onLight();
    assertThatThrownBy(() -> light.setBrightness(invalid))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("brightness");
  }

  @Test
  void setStateBrightness_WhileOnWithInvalidValue_ThrowsDomainException() {
    // State-aware path: OnLight.setBrightness delegates to Light.setBrightness
    // which performs the validation.
    Light light = onLight();
    assertThatThrownBy(() -> light.setStateBrightness(5))
        .isInstanceOf(DomainException.class);
  }

  @Test
  void setStateBrightness_WhileOffWithInvalidValue_DoesNotThrow() {
    // OffLight.setBrightness is a no-op — invalid values are silently dropped
    // because the command never reaches the validating setter. This documents
    // an asymmetry in the state pattern: validation only fires while On.
    Light light = new Light("light-test", "Test", "Living Room"); // Off
    assertThatNoException().isThrownBy(() -> light.setStateBrightness(9999));
    assertThat(light.getBrightness()).isEqualTo(100); // unchanged default
  }
}
