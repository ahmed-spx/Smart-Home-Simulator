package smarthomesim.demo.domain.dto;
import jakarta.validation.constraints.NotBlank;

public record ColorRequest(@NotBlank String color) {}