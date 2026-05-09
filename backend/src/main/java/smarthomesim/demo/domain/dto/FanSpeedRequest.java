package smarthomesim.demo.domain.dto;
import jakarta.validation.constraints.NotBlank;

public record FanSpeedRequest(@NotBlank String speed) {}
