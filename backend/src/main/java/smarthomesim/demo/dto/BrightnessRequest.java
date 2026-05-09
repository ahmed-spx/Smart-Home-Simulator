package smarthomesim.demo.dto;
import jakarta.validation.constraints.NotNull;

public record BrightnessRequest(@NotNull Integer brightness) {}
