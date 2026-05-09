package smarthomesim.demo.domain.dto;
import jakarta.validation.constraints.NotNull;

public record BrightnessRequest(@NotNull Integer brightness) {}
