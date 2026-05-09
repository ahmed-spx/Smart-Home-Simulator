package smarthomesim.demo.domain.dto;
import jakarta.validation.constraints.NotBlank;

public record MultiplierRequest(@NotBlank String multiplier) {}