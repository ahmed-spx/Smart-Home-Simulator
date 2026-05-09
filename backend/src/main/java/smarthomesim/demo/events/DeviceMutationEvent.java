package smarthomesim.demo.events;

// Marker for any event that mutates persisted device state.
// Subscribed to by {@link smarthomesim.demo.persistence.DeviceStore} so that
// a single listener handles persistence regardless of mutation type
// (state-changed / registered / removed).

public interface DeviceMutationEvent {
    String deviceId();
}