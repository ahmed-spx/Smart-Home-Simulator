package smarthomesim.demo.domain.dto;
import jakarta.validation.constraints.NotNull;

public record TemperatureRequest(@NotNull Integer temperature) {}