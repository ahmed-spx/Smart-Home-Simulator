package smarthomesim.demo.domain.dto;

import jakarta.validation.constraints.NotNull;

public record AmbientTemperatureRequest(@NotNull Integer ambientTemperature) {}