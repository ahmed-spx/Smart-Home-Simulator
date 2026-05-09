package smarthomesim.demo.controller;

import java.util.List;
import java.util.Map;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import smarthomesim.demo.domain.Device;
import smarthomesim.demo.domain.dto.RegisterDeviceRequest;
import smarthomesim.demo.domain.events.DeviceRegisteredEvent;
import smarthomesim.demo.domain.events.DeviceRemovedEvent;
import smarthomesim.demo.domain.exception.DeviceNotFoundException;
import smarthomesim.demo.domain.exception.InvalidDeviceTypeException;
import smarthomesim.demo.repository.AuditEntry;
import smarthomesim.demo.service.AuditLog;
import smarthomesim.demo.service.factory.DeviceFactory;
import smarthomesim.demo.repository.persistence.DeviceStore;
import smarthomesim.demo.domain.events.DeviceStateChangedEvent;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {

  private final Map<String, Device> devices;
  private final DeviceFactory factory;
  private final AuditLog auditLog;
  private final ApplicationEventPublisher publisher;
  private final DeviceStore store;

  public DeviceController(Map<String, Device> devices, DeviceFactory factory, AuditLog auditLog, ApplicationEventPublisher publisher, DeviceStore store) {
    this.devices = devices;
    this.factory = factory;
    this.auditLog = auditLog;
    this.publisher = publisher;
    this.store = store;
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

  @PostMapping("/reset")
  public ResponseEntity<Void> reset() {
    store.reset();
    publisher.publishEvent(new DeviceStateChangedEvent("system", "reset"));
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> remove(@PathVariable String id) {
    Device removed = devices.remove(id);
    if (removed == null) throw new DeviceNotFoundException(id);
    publisher.publishEvent(new DeviceRemovedEvent(id));
    return ResponseEntity.noContent().build();
  }
}