package smarthomesim.demo.domain.fan;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@DisplayName("Fan state machine")
class FanStateMachineTest {

  private Fan newFan() {
    return new Fan("fan-test", "Test Fan", "Kitchen");
  }

  @Nested
  @DisplayName("Initial state")
  class Initial {

    @Test
    void newFan_DefaultState_IsOff() {
      assertThat(newFan().getFanState()).isEqualTo(FanState.Off);
    }

    @Test
    void newFan_DefaultSpeed_IsOff() {
      assertThat(newFan().getSpeed()).isEqualTo(Speed.Off);
    }
  }

  @Nested
  @DisplayName("togglePower transitions")
  class Toggle {

    @Test
    void togglePower_FromOff_TransitionsToOn() {
      Fan fan = newFan();
      fan.togglePower();
      assertThat(fan.getFanState()).isEqualTo(FanState.On);
    }

    @Test
    void togglePower_FromOn_TransitionsToOff() {
      Fan fan = newFan();
      fan.togglePower();
      fan.togglePower();
      assertThat(fan.getFanState()).isEqualTo(FanState.Off);
    }

    @Test
    void togglePower_DoesNotMutateSpeedField() {
      // Toggling power must not silently zero out the chosen speed.
      Fan fan = newFan();
      fan.togglePower();
      fan.setStateSpeed(Speed.Medium);
      fan.togglePower();
      assertThat(fan.getSpeed()).isEqualTo(Speed.Medium);
    }
  }

  @Nested
  @DisplayName("Speed state-aware behavior")
  class StateSpeed {

    @ParameterizedTest(name = "setStateSpeed({0}) while On stores {0}")
    @EnumSource(value = Speed.class, names = {"Low", "Medium", "High"})
    void setStateSpeed_WhileOn_UpdatesSpeed(Speed s) {
      Fan fan = newFan();
      fan.togglePower();
      fan.setStateSpeed(s);
      assertThat(fan.getSpeed()).isEqualTo(s);
    }

    @Test
    void setStateSpeed_WhileOff_LeavesSpeedUnchanged() {
      // OffFanState.setSpeed() is a no-op — the spec calls this "rejected".
      Fan fan = newFan(); // Off
      fan.setStateSpeed(Speed.High);
      assertThat(fan.getSpeed())
          .as("Off fan must ignore speed-change commands")
          .isEqualTo(Speed.Off);
    }
  }

  @Nested
  @DisplayName("Settings retention")
  class Retention {

    @Test
    void speed_SurvivesOffOnCycle() {
      Fan fan = newFan();
      fan.togglePower();              // On
      fan.setStateSpeed(Speed.High);
      fan.togglePower();              // Off (speed retained as field)
      fan.togglePower();              // On
      assertThat(fan.getSpeed()).isEqualTo(Speed.High);
    }
  }
}
