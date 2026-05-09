package smarthomesim.demo.domain.thermostat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import smarthomesim.demo.domain.exception.DomainException;
import smarthomesim.demo.service.modes.AutoMode;
import smarthomesim.demo.service.modes.CoolingMode;
import smarthomesim.demo.service.modes.HeatingMode;
import smarthomesim.demo.service.modes.ThermostatModes;

@DisplayName("Thermostat temperature boundaries")
class ThermostatBoundaryTest {

  private ThermostatModes modes;

  @BeforeEach
  void wireStrategies() {
    modes = new ThermostatModes(List.of(new AutoMode(), new HeatingMode(), new CoolingMode()));
  }

  private Thermostat newThermostat() {
    Thermostat t = new Thermostat("thermo-test", "Test", "Living Room");
    t.setModes(modes);
    return t;
  }

  @Test
  void setThermostatDesiredTemperature_AtMinimum60_Succeeds() {
    Thermostat t = newThermostat();
    assertThatNoException().isThrownBy(() -> t.setThermostatDesiredTemperature(60));
    assertThat(t.getDesiredTemperature()).isEqualTo(60);
  }

  @Test
  void setThermostatDesiredTemperature_AtMaximum80_Succeeds() {
    Thermostat t = newThermostat();
    assertThatNoException().isThrownBy(() -> t.setThermostatDesiredTemperature(80));
    assertThat(t.getDesiredTemperature()).isEqualTo(80);
  }

  @Test
  void setThermostatDesiredTemperature_MidRange70_Succeeds() {
    Thermostat t = newThermostat();
    t.setThermostatDesiredTemperature(70);
    assertThat(t.getDesiredTemperature()).isEqualTo(70);
  }

  @ParameterizedTest(name = "setThermostatDesiredTemperature({0}) → DomainException")
  @ValueSource(ints = {59, 50, 0, -10, 81, 100, 200, Integer.MAX_VALUE, Integer.MIN_VALUE})
  void setThermostatDesiredTemperature_OutOfRange_ThrowsDomainException(int invalid) {
    Thermostat t = newThermostat();
    assertThatThrownBy(() -> t.setThermostatDesiredTemperature(invalid))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("desiredTemperature");
  }

  @Test
  void setStateDesiredTemperature_FromIdleWithInvalidValue_ThrowsDomainException() {
    // State-aware path: Idle.setTemperature → setThermostatDesiredTemperature → validates.
    Thermostat t = newThermostat();
    t.setAmbientTemperature(70);
    t.togglePower(); // Idle
    assertThatThrownBy(() -> t.setStateDesiredTemperature(99))
        .isInstanceOf(DomainException.class);
  }

  @Test
  void setStateDesiredTemperature_FromOffWithInvalidValue_DoesNotThrow() {
    // Off state silently swallows the command — validation never runs.
    // Documents the same Off-state asymmetry as Light.
    Thermostat t = newThermostat();
    assertThatNoException().isThrownBy(() -> t.setStateDesiredTemperature(999));
    assertThat(t.getDesiredTemperature()).isEqualTo(70);
  }
}
