package smarthomesim.demo.domain.dto;
import jakarta.validation.constraints.NotBlank;

public record ModeRequest(@NotBlank String mode) {}
