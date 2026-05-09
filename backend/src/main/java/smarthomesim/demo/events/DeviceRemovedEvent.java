package smarthomesim.demo.events;

import java.time.Instant;

public record DeviceRemovedEvent(String deviceId, Instant timestamp) implements DeviceMutationEvent {
    public DeviceRemovedEvent(String deviceId) {
        this(deviceId, Instant.now());
    }
}