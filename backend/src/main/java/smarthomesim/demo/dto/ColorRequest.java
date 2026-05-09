package smarthomesim.demo.dto;
import jakarta.validation.constraints.NotBlank;

public record ColorRequest(@NotBlank String color) {}