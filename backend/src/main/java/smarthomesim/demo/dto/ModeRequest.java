package smarthomesim.demo.dto;
import jakarta.validation.constraints.NotBlank;

public record ModeRequest(@NotBlank String mode) {}
