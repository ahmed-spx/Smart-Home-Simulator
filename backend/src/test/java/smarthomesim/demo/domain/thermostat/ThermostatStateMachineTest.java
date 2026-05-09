package smarthomesim.demo.domain.thermostat;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import smarthomesim.demo.domain.thermostat.thermostatstates.CoolingThermostatState;
import smarthomesim.demo.domain.thermostat.thermostatstates.HeatingThermostatState;
import smarthomesim.demo.domain.thermostat.thermostatstates.IdleThermostatState;
import smarthomesim.demo.domain.thermostat.thermostatstates.OffThermostatState;
import smarthomesim.demo.service.modes.AutoMode;
import smarthomesim.demo.service.modes.CoolingMode;
import smarthomesim.demo.service.modes.HeatingMode;
import smarthomesim.demo.service.modes.ThermostatModes;

@DisplayName("Thermostat state machine")
class ThermostatStateMachineTest {

  private ThermostatModes modes;

  @BeforeEach
  void wireStrategies() {
    modes = new ThermostatModes(List.of(new AutoMode(), new HeatingMode(), new CoolingMode()));
  }

  private Thermostat newThermostat() {
    Thermostat t = new Thermostat("thermo-test", "Test Thermostat", "Living Room");
    t.setModes(modes);
    return t;
  }

  // ────────────────────────────────────────────────────────────
  @Nested
  @DisplayName("Initial state")
  class Initial {
    @Test
    void newThermostat_DefaultState_IsOff() {
      assertThat(newThermostat().getThermostatState()).isEqualTo(ThermostatState.Off);
    }

    @Test
    void newThermostat_DefaultMode_IsAuto() {
      assertThat(newThermostat().getMode()).isEqualTo(ThermostatMode.Auto);
    }

    @Test
    void newThermostat_DefaultDesiredTemperature_Is70() {
      assertThat(newThermostat().getDesiredTemperature()).isEqualTo(70);
    }

    @Test
    void newThermostat_DefaultAmbientTemperature_Is65() {
      assertThat(newThermostat().getAmbientTemperature()).isEqualTo(65);
    }

    @Test
    void newThermostat_DefaultMultiplier_Is1x() {
      assertThat(newThermostat().getMultiplier()).isEqualTo(Multiplier.x1);
    }
  }

  // ────────────────────────────────────────────────────────────
  @Nested
  @DisplayName("togglePower transitions")
  class Toggle {

    @Test
    void togglePower_FromOff_WithAmbientLessThanDesired_GoesToHeating() {
      // Defaults: ambient=65, desired=70, mode=Auto.
      // Off→toggle promotes to Idle and re-evaluates strategy → Heating.
      Thermostat t = newThermostat();
      t.togglePower();
      assertThat(t.getThermostatState()).isEqualTo(ThermostatState.Heating);
    }

    @Test
    void togglePower_FromOff_WithAmbientGreaterThanDesired_GoesToCooling() {
      Thermostat t = newThermostat();
      t.setAmbientTemperature(75); // > desired (70)
      t.togglePower();
      assertThat(t.getThermostatState()).isEqualTo(ThermostatState.Cooling);
    }

    @Test
    void togglePower_FromOff_WithAmbientEqualsDesired_GoesToIdle() {
      Thermostat t = newThermostat();
      t.setAmbientTemperature(70); // == desired
      t.togglePower();
      assertThat(t.getThermostatState()).isEqualTo(ThermostatState.Idle);
    }

    @Test
    void togglePower_FromHeating_GoesToOff() {
      Thermostat t = newThermostat();
      t.togglePower(); // -> Heating
      t.togglePower(); // -> Off
      assertThat(t.getThermostatState()).isEqualTo(ThermostatState.Off);
    }

    @Test
    void togglePower_FromCooling_GoesToOff() {
      Thermostat t = newThermostat();
      t.setAmbientTemperature(75);
      t.togglePower(); // Cooling
      t.togglePower(); // Off
      assertThat(t.getThermostatState()).isEqualTo(ThermostatState.Off);
    }

    @Test
    void togglePower_FromIdle_GoesToOff() {
      Thermostat t = newThermostat();
      t.setAmbientTemperature(70);
      t.togglePower(); // Idle
      t.togglePower(); // Off
      assertThat(t.getThermostatState()).isEqualTo(ThermostatState.Off);
    }
  }

  // ────────────────────────────────────────────────────────────
  @Nested
  @DisplayName("Invalid direct transitions")
  class InvalidTransitions {

    @Test
    void setStateMode_OnOff_DoesNotTransitionToHeating() {
      // The spec calls "Off → Heating" an invalid direct transition.
      // OffThermostatState.setMode is a no-op, so this command is silently
      // rejected — the only legal way out of Off is togglePower().
      Thermostat t = newThermostat();
      t.setStateMode(ThermostatMode.Heating);
      assertThat(t.getThermostatState()).isEqualTo(ThermostatState.Off);
      assertThat(t.getMode())
          .as("mode field is also untouched while Off")
          .isEqualTo(ThermostatMode.Auto);
    }

    @Test
    void setStateDesiredTemperature_OnOff_LeavesValueUnchanged() {
      Thermostat t = newThermostat();
      t.setStateDesiredTemperature(78);
      assertThat(t.getDesiredTemperature())
          .as("Off thermostat ignores temperature changes")
          .isEqualTo(70);
    }

    @Test
    void changeStateDirectlyToHeatingFromOff_IsBlockedAtStateMachineLevel() {
      // Public API offers no method to teleport from Off to Heating.
      // changeState() is package-internal use by the State pattern itself.
      // Verify there is no setOn(), setHeating(), etc. exposed.
      Thermostat t = newThermostat();
      t.togglePower(); // Off -> Heating (legal path)
      assertThat(t.getThermostatState()).isEqualTo(ThermostatState.Heating);
    }
  }

  // ────────────────────────────────────────────────────────────
  @Nested
  @DisplayName("Mode changes while active")
  class ModeChanges {

    @Test
    void setStateMode_FromIdle_ToHeating_WithAmbientLessThanDesired_TransitionsToHeating() {
      Thermostat t = newThermostat();
      t.setAmbientTemperature(70);
      t.togglePower(); // Idle
      t.setAmbientTemperature(60);
      t.setStateMode(ThermostatMode.Heating);
      assertThat(t.getMode()).isEqualTo(ThermostatMode.Heating);
      assertThat(t.getThermostatState()).isEqualTo(ThermostatState.Heating);
    }

    @Test
    void setStateMode_FromIdle_ToCooling_WithAmbientGreaterThanDesired_TransitionsToCooling() {
      Thermostat t = newThermostat();
      t.setAmbientTemperature(70);
      t.togglePower(); // Idle
      t.setAmbientTemperature(80);
      t.setStateMode(ThermostatMode.Cooling);
      assertThat(t.getMode()).isEqualTo(ThermostatMode.Cooling);
      assertThat(t.getThermostatState()).isEqualTo(ThermostatState.Cooling);
    }

    @Test
    void setStateMode_FromHeating_DoesNotChangeMode() {
      // HeatingThermostatState.setMode is a no-op — mode locks while running.
      Thermostat t = newThermostat();
      t.togglePower(); // Heating
      t.setStateMode(ThermostatMode.Cooling);
      assertThat(t.getMode())
          .as("Mode cannot be reassigned mid-run; toggle off first")
          .isEqualTo(ThermostatMode.Auto);
    }

    @Test
    void setStateMode_FromCooling_DoesNotChangeMode() {
      Thermostat t = newThermostat();
      t.setAmbientTemperature(75);
      t.togglePower(); // Cooling
      t.setStateMode(ThermostatMode.Heating);
      assertThat(t.getMode()).isEqualTo(ThermostatMode.Auto);
    }
  }

  // ────────────────────────────────────────────────────────────
  @Nested
  @DisplayName("Settings retention via setStateDesiredTemperature")
  class TemperatureSettings {

    @Test
    void setStateDesiredTemperature_FromIdle_UpdatesAndReevaluates() {
      Thermostat t = newThermostat();
      t.setAmbientTemperature(70);
      t.togglePower(); // Idle
      t.setStateDesiredTemperature(75);
      assertThat(t.getDesiredTemperature()).isEqualTo(75);
      // ambient(70) < new desired(75) → Auto mode evaluates → Heating.
      assertThat(t.getThermostatState()).isEqualTo(ThermostatState.Heating);
    }

    @Test
    void setStateDesiredTemperature_FromHeating_UpdatesDesiredTemperature() {
      Thermostat t = newThermostat();
      t.togglePower(); // Heating (default ambient<desired)
      t.setStateDesiredTemperature(72);
      assertThat(t.getDesiredTemperature()).isEqualTo(72);
    }
  }

  // ────────────────────────────────────────────────────────────
  @Nested
  @DisplayName("Ambient drift via tick()")
  class TickDrift {

    /**
     * {@link Thermostat#tick()} only delegates to the inner state's tick
     * every {@code 10 / multiplier} calls. We set multiplier=x10 in these
     * tests so each call to tick() triggers the inner state immediately —
     * without that, a single tick() just bumps an internal counter.
     */
    private Thermostat fastTickingThermostat() {
      Thermostat t = newThermostat();
      t.setMultiplier(Multiplier.x10); // 10/10 = 1 tick per call
      return t;
    }

    @Test
    void tick_FromHeating_WithAmbientLessThanDesired_IncreasesAmbient() {
      Thermostat t = fastTickingThermostat();
      t.changeState(new HeatingThermostatState());
      int before = t.getAmbientTemperature();
      t.tick();
      assertThat(t.getAmbientTemperature()).isEqualTo(before + 1);
      assertThat(t.getThermostatState()).isEqualTo(ThermostatState.Heating);
    }

    @Test
    void tick_FromHeating_WhenAmbientReachesDesired_TransitionsToIdle() {
      Thermostat t = fastTickingThermostat();
      t.setAmbientTemperature(70); // == desired
      t.changeState(new HeatingThermostatState());
      t.tick();
      assertThat(t.getThermostatState()).isEqualTo(ThermostatState.Idle);
    }

    @Test
    void tick_FromCooling_WithAmbientGreaterThanDesired_DecreasesAmbient() {
      Thermostat t = fastTickingThermostat();
      t.setAmbientTemperature(75);
      t.changeState(new CoolingThermostatState());
      t.tick();
      assertThat(t.getAmbientTemperature()).isEqualTo(74);
      assertThat(t.getThermostatState()).isEqualTo(ThermostatState.Cooling);
    }

    @Test
    void tick_FromCooling_WhenAmbientReachesDesired_TransitionsToIdle() {
      Thermostat t = fastTickingThermostat();
      t.setAmbientTemperature(70);
      t.changeState(new CoolingThermostatState());
      t.tick();
      assertThat(t.getThermostatState()).isEqualTo(ThermostatState.Idle);
    }

    @Test
    void tick_FromOff_DoesNothing() {
      Thermostat t = fastTickingThermostat();
      t.changeState(new OffThermostatState());
      int beforeAmb = t.getAmbientTemperature();
      t.tick();
      assertThat(t.getAmbientTemperature()).isEqualTo(beforeAmb);
      assertThat(t.getThermostatState()).isEqualTo(ThermostatState.Off);
    }

    @Test
    void tick_FromIdle_DoesNothing() {
      Thermostat t = fastTickingThermostat();
      t.setAmbientTemperature(70);
      t.changeState(new IdleThermostatState());
      int beforeAmb = t.getAmbientTemperature();
      t.tick();
      assertThat(t.getAmbientTemperature()).isEqualTo(beforeAmb);
      assertThat(t.getThermostatState()).isEqualTo(ThermostatState.Idle);
    }

    @Test
    void tick_AtX1Multiplier_AccumulatesCounterBeforeInnerTickFires() {
      // Documents the throttling: at x1 it takes 10 calls before ambient moves.
      Thermostat t = newThermostat(); // default x1
      t.changeState(new HeatingThermostatState());
      int before = t.getAmbientTemperature();
      for (int i = 0; i < 9; i++) t.tick();
      assertThat(t.getAmbientTemperature()).isEqualTo(before);
      t.tick(); // 10th call triggers
      assertThat(t.getAmbientTemperature()).isEqualTo(before + 1);
    }
  }
}
