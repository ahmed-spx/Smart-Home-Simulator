package smarthomesim.demo.domain.fan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Fan speed boundaries")
class FanBoundaryTest {

  @Test
  void speedEnum_ContainsExactlyOffLowMediumHigh() {
    assertThat(Speed.values())
        .containsExactly(Speed.Off, Speed.Low, Speed.Medium, Speed.High);
  }

  @Test
  void speedValueOf_Low_ReturnsLow() {
    assertThat(Speed.valueOf("Low")).isEqualTo(Speed.Low);
  }

  @Test
  void speedValueOf_Medium_ReturnsMedium() {
    assertThat(Speed.valueOf("Medium")).isEqualTo(Speed.Medium);
  }

  @Test
  void speedValueOf_High_ReturnsHigh() {
    assertThat(Speed.valueOf("High")).isEqualTo(Speed.High);
  }

  @Test
  void speedValueOf_InvalidString_ThrowsIllegalArgumentException() {
    // FanController calls Speed.valueOf(body.speed()) — anything not in the
    // enum becomes an IllegalArgumentException, which the global exception
    // handler translates into HTTP 400.
    assertThatThrownBy(() -> Speed.valueOf("Turbo"))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void speedValueOf_WrongCase_ThrowsIllegalArgumentException() {
    assertThatThrownBy(() -> Speed.valueOf("low"))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
