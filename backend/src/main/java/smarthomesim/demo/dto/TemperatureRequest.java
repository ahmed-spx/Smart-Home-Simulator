package smarthomesim.demo.dto;
import jakarta.validation.constraints.NotNull;

public record TemperatureRequest(@NotNull Integer temperature) {}