package smarthomesim.demo.audit;

public record AuditEntry(
    String timestamp,
    String deviceId,
    String operation
) {}