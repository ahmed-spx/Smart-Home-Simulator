package smarthomesim.demo.device;

import java.util.List;
import java.util.Map;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import smarthomesim.demo.audit.AuditEntry;
import smarthomesim.demo.audit.AuditLog;
import smarthomesim.demo.dto.RegisterDeviceRequest;
import smarthomesim.demo.events.DeviceRegisteredEvent;
import smarthomesim.demo.events.DeviceRemovedEvent;
import smarthomesim.demo.exception.DeviceNotFoundException;
import smarthomesim.demo.exception.InvalidDeviceTypeException;
import smarthomesim.demo.factory.DeviceFactory;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {

  private final Map<String, Device> devices;
  private final DeviceFactory factory;
  private final AuditLog auditLog;
  private final ApplicationEventPublisher publisher;

  public DeviceController(Map<String, Device> devices, DeviceFactory factory,
                          AuditLog auditLog, ApplicationEventPublisher publisher) {
    this.devices = devices;
    this.factory = factory;
    this.auditLog = auditLog;
    this.publisher = publisher;
  }

  @GetMapping
  public List<Map<String, Object>> listDevices() {
    return devices.values().stream().map(factory::view).toList();
  }

  @GetMapping("/{id}/history")
  public List<AuditEntry> getHistory(@PathVariable String id) {
    return auditLog.historyFor(id);
  }

  @PostMapping
  public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterDeviceRequest req) {
    String id;
    Device device;
    try {
      id = factory.nextId(req.type(), devices.keySet());
      device = factory.create(req.type(), id, req.name(), req.location());
    } catch (IllegalArgumentException e) {
      throw new InvalidDeviceTypeException(e.getMessage());
    }
    devices.put(id, device);
    publisher.publishEvent(new DeviceRegisteredEvent(id, req.type(), req.name(), req.location()));
    return ResponseEntity.status(HttpStatus.CREATED).body(factory.view(device));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> remove(@PathVariable String id) {
    Device removed = devices.remove(id);
    if (removed == null) throw new DeviceNotFoundException(id);
    publisher.publishEvent(new DeviceRemovedEvent(id));
    return ResponseEntity.noContent().build();
  }
}