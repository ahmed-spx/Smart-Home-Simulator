package smarthomesim.demo.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import smarthomesim.demo.domain.Device;
import smarthomesim.demo.domain.thermostat.Thermostat;
import smarthomesim.demo.repository.AuditEntry;
import smarthomesim.demo.repository.persistence.DeviceStore;
import smarthomesim.demo.service.AuditLog;
import smarthomesim.demo.service.factory.DeviceFactory;
import smarthomesim.demo.service.modes.AutoMode;
import smarthomesim.demo.service.modes.CoolingMode;
import smarthomesim.demo.service.modes.HeatingMode;
import smarthomesim.demo.service.modes.ThermostatModes;

/**
 * HTTP-layer tests for {@link DeviceController}.
 *
 * Uses Spring's vanilla {@code MockMvcBuilders.standaloneSetup} instead of
 * {@code @WebMvcTest} because Spring Boot 4.0 removed the slice test
 * annotations from spring-boot-test-autoconfigure. This gives us the same
 * coverage (request validation, JSON conversion, @ControllerAdvice exception
 * handling) without depending on auto-configured beans.
 */
@DisplayName("DeviceController (HTTP layer)")
class DeviceControllerWebMvcTest {

  private MockMvc mockMvc;
  private Map<String, Device> devices;
  private DeviceFactory factory;
  private AuditLog auditLog;
  private DeviceStore store;
  private ApplicationEventPublisher publisher;

  @BeforeEach
  void setUp() {
    devices   = new LinkedHashMap<>();
    factory   = mock(DeviceFactory.class);
    auditLog  = mock(AuditLog.class);
    store     = mock(DeviceStore.class);
    publisher = mock(ApplicationEventPublisher.class);

    DeviceController controller =
        new DeviceController(devices, factory, auditLog, publisher, store);

    LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
    validator.afterPropertiesSet();

    mockMvc = MockMvcBuilders.standaloneSetup(controller)
        .setControllerAdvice(new ApiExceptionHandler())
        .setValidator(validator)
        .build();
  }

  // ────────────────────────────────────────────────────────────
  @Nested
  @DisplayName("GET /api/devices")
  class ListDevices {
    @Test
    void listDevices_NoDevices_ReturnsEmptyArray() throws Exception {
      mockMvc.perform(get("/api/devices"))
          .andExpect(status().isOk())
          .andExpect(content().json("[]"));
    }
  }

  // ────────────────────────────────────────────────────────────
  @Nested
  @DisplayName("POST /api/devices — successful registration")
  class RegisterSuccess {

    @Test
    void register_ValidLightPayload_Returns201WithDeviceView() throws Exception {
      Device fakeDevice = stubFactoryToReturn("Light", "light-1", "Lamp", "Bedroom");

      String body = """
          { "type": "Light", "name": "Lamp", "location": "Bedroom" }
          """;

      mockMvc.perform(post("/api/devices")
              .contentType(MediaType.APPLICATION_JSON).content(body))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.id").value("light-1"));

      assertThat(devices).containsEntry("light-1", fakeDevice);
    }
  }

  // ────────────────────────────────────────────────────────────
  @Nested
  @DisplayName("POST /api/devices — request validation")
  class RegisterValidation {

    @Test
    void register_MissingType_Returns400() throws Exception {
      String body = """
          { "name": "Lamp", "location": "Bedroom" }
          """;
      mockMvc.perform(post("/api/devices")
              .contentType(MediaType.APPLICATION_JSON).content(body))
          .andExpect(status().isBadRequest());
    }

    @Test
    void register_MissingName_Returns400() throws Exception {
      String body = """
          { "type": "Light", "location": "Bedroom" }
          """;
      mockMvc.perform(post("/api/devices")
              .contentType(MediaType.APPLICATION_JSON).content(body))
          .andExpect(status().isBadRequest());
    }

    @Test
    void register_MissingLocation_Returns400() throws Exception {
      String body = """
          { "type": "Light", "name": "Lamp" }
          """;
      mockMvc.perform(post("/api/devices")
              .contentType(MediaType.APPLICATION_JSON).content(body))
          .andExpect(status().isBadRequest());
    }

    @Test
    void register_BlankName_Returns400() throws Exception {
      String body = """
          { "type": "Light", "name": "  ", "location": "Bedroom" }
          """;
      mockMvc.perform(post("/api/devices")
              .contentType(MediaType.APPLICATION_JSON).content(body))
          .andExpect(status().isBadRequest());
    }

    @Test
    void register_BlankLocation_Returns400() throws Exception {
      String body = """
          { "type": "Light", "name": "Lamp", "location": "" }
          """;
      mockMvc.perform(post("/api/devices")
              .contentType(MediaType.APPLICATION_JSON).content(body))
          .andExpect(status().isBadRequest());
    }

    @Test
    void register_MalformedJson_Returns400() throws Exception {
      mockMvc.perform(post("/api/devices")
              .contentType(MediaType.APPLICATION_JSON).content("{ not json"))
          .andExpect(status().isBadRequest());
    }

    @Test
    void register_UnknownDeviceType_Returns400() throws Exception {
      // Factory rejects the unknown type with IllegalArgumentException, which
      // the controller wraps as InvalidDeviceTypeException → 400.
      when(factory.nextId(eq("Microwave"), anySet()))
          .thenThrow(new IllegalArgumentException("Unknown device type: Microwave"));

      String body = """
          { "type": "Microwave", "name": "X", "location": "Kitchen" }
          """;
      mockMvc.perform(post("/api/devices")
              .contentType(MediaType.APPLICATION_JSON).content(body))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.title").value(containsString("Invalid device type")));
    }
  }

  // ────────────────────────────────────────────────────────────
  @Nested
  @DisplayName("POST /api/devices — single-thermostat-per-location invariant")
  class ThermostatInvariant {

    /**
     * Spec 5.1.1: "Registering a second thermostat in a location that already
     * has one is rejected." If this test fails, the production code's
     * {@link DeviceController#register} is missing the invariant check —
     * add a guard that returns 409 Conflict (or 400 Bad Request) when the
     * incoming device is a Thermostat and another Thermostat already lives
     * in the requested location.
     */
    @Test
    void register_SecondThermostatInSameLocation_IsRejected() throws Exception {
      Thermostat existing = newThermostat("thermo-1", "Living Room");
      devices.put("thermo-1", existing);

      // Stub in case the invariant guard isn't there — the response shape is
      // less important than the 4xx status assertion below.
      stubFactoryToReturn("Thermostat", "thermo-2", "Second", "Living Room");

      String body = """
          { "type": "Thermostat", "name": "Second", "location": "Living Room" }
          """;

      mockMvc.perform(post("/api/devices")
              .contentType(MediaType.APPLICATION_JSON).content(body))
          .andExpect(status().is4xxClientError());
    }

    @Test
    void register_ThermostatInDifferentLocation_IsAllowed() throws Exception {
      Thermostat existing = newThermostat("thermo-1", "Living Room");
      devices.put("thermo-1", existing);

      stubFactoryToReturn("Thermostat", "thermo-2", "Bedroom T", "Bedroom");

      String body = """
          { "type": "Thermostat", "name": "Bedroom T", "location": "Bedroom" }
          """;
      mockMvc.perform(post("/api/devices")
              .contentType(MediaType.APPLICATION_JSON).content(body))
          .andExpect(status().isCreated());
    }
  }

  // ────────────────────────────────────────────────────────────
  @Nested
  @DisplayName("DELETE /api/devices/{id}")
  class Remove {

    @Test
    void remove_ExistingDevice_Returns204AndRemovesFromRegistry() throws Exception {
      Device d = newThermostat("thermo-1", "Living Room");
      devices.put("thermo-1", d);

      mockMvc.perform(delete("/api/devices/thermo-1"))
          .andExpect(status().isNoContent());

      assertThat(devices).doesNotContainKey("thermo-1");
    }

    @Test
    void remove_UnknownDevice_Returns404() throws Exception {
      mockMvc.perform(delete("/api/devices/does-not-exist"))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.title").value("Device not found"));
    }
  }

  // ────────────────────────────────────────────────────────────
  @Nested
  @DisplayName("GET /api/devices/{id}/history")
  class History {

    @Test
    void getHistory_DelegatesToAuditLog_AndReturnsEntries() throws Exception {
      AuditEntry e1 = new AuditEntry("2026-05-08T10:00:00Z", "light-1", "toggle");
      AuditEntry e2 = new AuditEntry("2026-05-08T10:00:01Z", "light-1", "setBrightness:50");
      when(auditLog.historyFor("light-1")).thenReturn(List.of(e1, e2));

      mockMvc.perform(get("/api/devices/light-1/history"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.length()").value(2))
          .andExpect(jsonPath("$[0].deviceId").value("light-1"))
          .andExpect(jsonPath("$[0].operation").value("toggle"))
          .andExpect(jsonPath("$[1].operation").value("setBrightness:50"));
    }

    @Test
    void getHistory_DeviceWithNoHistory_ReturnsEmptyArray() throws Exception {
      when(auditLog.historyFor("nobody")).thenReturn(List.of());

      mockMvc.perform(get("/api/devices/nobody/history"))
          .andExpect(status().isOk())
          .andExpect(content().json("[]"));
    }
  }

  // ────────────────────────────────────────────────────────────
  // Helpers
  // ────────────────────────────────────────────────────────────

  private Device stubFactoryToReturn(String type, String id, String name, String location) {
    Device d;
    if ("Thermostat".equals(type)) {
      d = newThermostat(id, location);
    } else {
      d = new smarthomesim.demo.domain.light.Light(id, name, location);
    }
    when(factory.nextId(eq(type), anySet())).thenReturn(id);
    when(factory.create(eq(type), eq(id), anyString(), anyString())).thenReturn(d);
    when(factory.view(any(Device.class))).thenAnswer(inv -> {
      Device device = inv.getArgument(0);
      return Map.of("id", device.getId(), "name", device.getName(),
                    "location", device.getLocation(), "type", device.getType());
    });
    return d;
  }

  private static Thermostat newThermostat(String id, String location) {
    Thermostat t = new Thermostat(id, "T", location);
    t.setModes(new ThermostatModes(List.of(new AutoMode(), new HeatingMode(), new CoolingMode())));
    return t;
  }
}
