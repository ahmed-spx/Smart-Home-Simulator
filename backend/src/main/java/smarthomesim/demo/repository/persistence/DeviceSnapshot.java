package smarthomesim.demo.repository.persistence;

public record DeviceSnapshot(
    String id,
    String type,
    String name,
    String location,
    String state,
    Integer brightness,
    String color,
    String speed,
    String mode,
    String multiplier,
    Integer desiredTemperature,
    Integer ambientTemperature
) {
    
}
