package smarthomesim.demo.audit;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class AuditLog {

  private static final File LOG_FILE = new File("data/audit.jsonl");
  private final ObjectMapper mapper = new ObjectMapper();

  public synchronized void record(String deviceId, String operation) {
    AuditEntry entry = new AuditEntry(Instant.now().toString(), deviceId, operation);
    try {
      LOG_FILE.getParentFile().mkdirs();
      try (FileWriter fw = new FileWriter(LOG_FILE, true)) {
        fw.write(mapper.writeValueAsString(entry));
        fw.write(System.lineSeparator());
      }
    } catch (IOException e) {
      throw new RuntimeException("Failed to write audit entry", e);
    }
  }

  public List<AuditEntry> historyFor(String deviceId) {
    if (!LOG_FILE.exists()) return List.of();
    try {
      List<AuditEntry> result = new ArrayList<>();
      for (String line : Files.readAllLines(LOG_FILE.toPath())) {
        if (line.isBlank()) continue;
        AuditEntry entry = mapper.readValue(line, AuditEntry.class);
        if (entry.deviceId().equals(deviceId)) result.add(entry);
      }
      return result;
    } catch (IOException e) {
      throw new RuntimeException("Failed to read audit log", e);
    }
  }
}