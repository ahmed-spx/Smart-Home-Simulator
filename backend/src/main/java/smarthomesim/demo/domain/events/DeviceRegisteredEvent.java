package smarthomesim.demo.domain.events;

import java.time.Instant;

public record DeviceRegisteredEvent(String deviceId, String type, String name, String location, Instant timestamp) {
    public DeviceRegisteredEvent(String deviceId, String type, String name, String location) {
        this(deviceId, type, name, location, Instant.now());
    }
}
