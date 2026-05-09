package smarthomesim.demo.domain.events;

import java.time.Instant;

public record DeviceStateChangedEvent(String deviceId, String operation, Instant timestamp) implements DeviceMutationEvent {
    public DeviceStateChangedEvent(String deviceId, String operation) {
        this(deviceId, operation, Instant.now());
    }
}