package smarthomesim.demo.domain.dto;
import jakarta.validation.constraints.NotBlank;

public record RegisterDeviceRequest(
    @NotBlank String type,
    @NotBlank String name,
    @NotBlank String location
) {}