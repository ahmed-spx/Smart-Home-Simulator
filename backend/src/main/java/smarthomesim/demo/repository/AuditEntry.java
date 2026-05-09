package smarthomesim.demo.repository;

public record AuditEntry(
    String timestamp,
    String deviceId,
    String operation
) {}