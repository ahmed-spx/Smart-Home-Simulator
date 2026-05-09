package smarthomesim.demo.service.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import smarthomesim.demo.domain.Device;
import smarthomesim.demo.domain.doorlock.DoorLock;
import smarthomesim.demo.domain.doorlock.DoorLockStates;
import smarthomesim.demo.domain.fan.Fan;
import smarthomesim.demo.domain.fan.FanState;
import smarthomesim.demo.domain.fan.Speed;
import smarthomesim.demo.domain.light.Light;
import smarthomesim.demo.domain.light.LightState;
import smarthomesim.demo.domain.thermostat.Thermostat;
import smarthomesim.demo.domain.thermostat.ThermostatMode;
import smarthomesim.demo.domain.thermostat.ThermostatState;
import smarthomesim.demo.service.modes.AutoMode;
import smarthomesim.demo.service.modes.CoolingMode;
import smarthomesim.demo.service.modes.HeatingMode;
import smarthomesim.demo.service.modes.ThermostatModes;

@DisplayName("DeviceFactory")
class DeviceFactoryTest {

  private DeviceFactory factory;

  @BeforeEach
  void buildFactory() {
    ThermostatModes modes = new ThermostatModes(
        List.of(new AutoMode(), new HeatingMode(), new CoolingMode()));
    factory = new DeviceFactory(List.of(
        new LightCreator(),
        new FanCreator(),
        new ThermostatCreator(modes),
        new DoorLockCreator()));
  }

  // ────────────────────────────────────────────────────────────
  @Nested
  @DisplayName("create() returns the right concrete type")
  class CreateType {

    @Test
    void create_LightType_ReturnsLightInstance() {
      Device d = factory.create("Light", "light-1", "Lamp", "Bedroom");
      assertThat(d).isInstanceOf(Light.class);
      assertThat(d.getId()).isEqualTo("light-1");
      assertThat(d.getName()).isEqualTo("Lamp");
      assertThat(d.getLocation()).isEqualTo("Bedroom");
    }

    @Test
    void create_FanType_ReturnsFanInstance() {
      Device d = factory.create("Fan", "fan-1", "Ceiling", "Bedroom");
      assertThat(d).isInstanceOf(Fan.class);
    }

    @Test
    void create_ThermostatType_ReturnsThermostatInstance() {
      Device d = factory.create("Thermostat", "thermo-1", "T", "Hall");
      assertThat(d).isInstanceOf(Thermostat.class);
    }

    @Test
    void create_DoorLockType_ReturnsDoorLockInstance() {
      Device d = factory.create("DoorLock", "lock-1", "Front", "Foyer");
      assertThat(d).isInstanceOf(DoorLock.class);
    }

    @Test
    void create_UnknownType_ThrowsIllegalArgumentException() {
      assertThatThrownBy(() -> factory.create("Microwave", "mw-1", "X", "Kitchen"))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Unknown device type");
    }
  }

  // ────────────────────────────────────────────────────────────
  @Nested
  @DisplayName("create() initializes new devices in default state")
  class Defaults {

    @Test
    void createLight_HasOffStateBrightness100ColorWhite() {
      Light l = (Light) factory.create("Light", "light-1", "Lamp", "Bedroom");
      assertThat(l.getLightState()).isEqualTo(LightState.Off);
      assertThat(l.getBrightness()).isEqualTo(100);
      assertThat(l.getColorHex()).isEqualToIgnoringCase("#FFFFFF");
    }

    @Test
    void createFan_HasOffStateAndOffSpeed() {
      Fan f = (Fan) factory.create("Fan", "fan-1", "Ceiling", "Bedroom");
      assertThat(f.getFanState()).isEqualTo(FanState.Off);
      assertThat(f.getSpeed()).isEqualTo(Speed.Off);
    }

    @Test
    void createThermostat_HasOffStateAutoModeDefaults() {
      Thermostat t = (Thermostat) factory.create("Thermostat", "thermo-1", "T", "Hall");
      assertThat(t.getThermostatState()).isEqualTo(ThermostatState.Off);
      assertThat(t.getMode()).isEqualTo(ThermostatMode.Auto);
      assertThat(t.getDesiredTemperature()).isEqualTo(70);
      assertThat(t.getAmbientTemperature()).isEqualTo(65);
    }

    @Test
    void createDoorLock_HasUnlockedState() {
      DoorLock dl = (DoorLock) factory.create("DoorLock", "lock-1", "Front", "Foyer");
      assertThat(dl.getDoorLockState()).isEqualTo(DoorLockStates.Unlocked);
    }
  }

  // ────────────────────────────────────────────────────────────
  @Nested
  @DisplayName("nextId() generation")
  class NextId {

    @Test
    void nextId_NoExistingIds_ReturnsPrefixDashOne() {
      assertThat(factory.nextId("Light", Set.of())).isEqualTo("light-1");
      assertThat(factory.nextId("Fan", Set.of())).isEqualTo("fan-1");
      assertThat(factory.nextId("Thermostat", Set.of())).isEqualTo("thermo-1");
      assertThat(factory.nextId("DoorLock", Set.of())).isEqualTo("lock-1");
    }

    @Test
    void nextId_WithExistingIds_IncrementsMaxNumberForType() {
      Set<String> existing = Set.of(
          "light-1", "light-2", "light-7",
          "fan-3",
          "lock-1", "lock-2", "lock-12");
      assertThat(factory.nextId("Light", existing)).isEqualTo("light-8");
      assertThat(factory.nextId("Fan", existing)).isEqualTo("fan-4");
      assertThat(factory.nextId("DoorLock", existing)).isEqualTo("lock-13");
    }

    @Test
    void nextId_IgnoresIdsThatDoNotMatchPrefix() {
      Set<String> existing = Set.of("light-1", "fan-99");
      assertThat(factory.nextId("Light", existing))
          .as("fan-99 must not affect light's next id")
          .isEqualTo("light-2");
    }

    @Test
    void nextId_UnknownType_ThrowsIllegalArgumentException() {
      assertThatThrownBy(() -> factory.nextId("Microwave", Set.of()))
          .isInstanceOf(IllegalArgumentException.class);
    }
  }

  // ────────────────────────────────────────────────────────────
  @Nested
  @DisplayName("dehydrate / view")
  class Snapshots {

    @Test
    void dehydrate_Light_RoundTripsFieldsIntoSnapshot() {
      Light l = (Light) factory.create("Light", "light-1", "Lamp", "Bedroom");
      var snap = factory.dehydrate(l);
      assertThat(snap.id()).isEqualTo("light-1");
      assertThat(snap.type()).isEqualTo("Light");
      assertThat(snap.brightness()).isEqualTo(100);
      assertThat(snap.color()).isEqualToIgnoringCase("#FFFFFF");
    }

    @Test
    void view_Light_IncludesTypeStateBrightnessColor() {
      Light l = (Light) factory.create("Light", "light-1", "Lamp", "Bedroom");
      var view = factory.view(l);
      assertThat(view).containsKeys("id", "name", "location", "type",
                                     "state", "brightness", "color");
    }

    @Test
    void view_Thermostat_IncludesAmbientAndDesiredTemperature() {
      Thermostat t = (Thermostat) factory.create("Thermostat", "thermo-1", "T", "Hall");
      var view = factory.view(t);
      assertThat(view).containsKeys("ambientTemperature", "desiredTemperature",
                                     "mode", "multiplier");
    }
  }
}
