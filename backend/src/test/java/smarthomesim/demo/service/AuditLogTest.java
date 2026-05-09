package smarthomesim.demo.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import smarthomesim.demo.repository.AuditEntry;

/**
 * AuditLog writes to data/audit.jsonl in the working directory. To avoid
 * polluting the project's real audit log this test backs up the existing file
 * (if any) before each test, runs against a fresh empty file, and restores
 * the original on tear-down.
 */
@DisplayName("AuditLog")
class AuditLogTest {

  private static final File LOG_FILE = new File("data/audit.jsonl");
  private static final File BACKUP   = new File("data/audit.jsonl.test-backup");

  private final AuditLog auditLog = new AuditLog();

  @BeforeEach
  void backupExistingLog() throws IOException {
    LOG_FILE.getParentFile().mkdirs();
    if (LOG_FILE.exists()) {
      Files.copy(LOG_FILE.toPath(), BACKUP.toPath(), StandardCopyOption.REPLACE_EXISTING);
      LOG_FILE.delete();
    }
  }

  @AfterEach
  void restoreOriginalLog() throws IOException {
    if (LOG_FILE.exists()) LOG_FILE.delete();
    if (BACKUP.exists()) {
      Files.move(BACKUP.toPath(), LOG_FILE.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }
  }

  @Test
  void record_OnEmptyLog_PersistsEntry() {
    auditLog.record("light-1", "toggle");
    List<AuditEntry> history = auditLog.historyFor("light-1");

    assertThat(history).hasSize(1);
    assertThat(history.get(0).deviceId()).isEqualTo("light-1");
    assertThat(history.get(0).operation()).isEqualTo("toggle");
    assertThat(history.get(0).timestamp())
        .as("timestamp must be a parseable ISO-8601 instant")
        .satisfies(ts -> Instant.parse(ts));
  }

  @Test
  void record_AppendsMultipleEntriesWithoutOverwriting() {
    auditLog.record("light-1", "toggle");
    auditLog.record("light-1", "setBrightness:50");
    auditLog.record("light-1", "setColor:#FF0000");

    List<AuditEntry> history = auditLog.historyFor("light-1");
    assertThat(history).hasSize(3);
    assertThat(history)
        .extracting(AuditEntry::operation)
        .containsExactly("toggle", "setBrightness:50", "setColor:#FF0000");
  }

  @Test
  void historyFor_FiltersByDeviceId() {
    auditLog.record("light-1", "toggle");
    auditLog.record("fan-1",   "toggle");
    auditLog.record("light-1", "setBrightness:30");
    auditLog.record("lock-2",  "toggleLock");

    assertThat(auditLog.historyFor("light-1")).hasSize(2);
    assertThat(auditLog.historyFor("fan-1")).hasSize(1);
    assertThat(auditLog.historyFor("lock-2")).hasSize(1);
  }

  @Test
  void historyFor_UnknownDevice_ReturnsEmptyList() {
    auditLog.record("light-1", "toggle");
    assertThat(auditLog.historyFor("does-not-exist")).isEmpty();
  }

  @Test
  void historyFor_NoLogFile_ReturnsEmptyList() {
    if (LOG_FILE.exists()) LOG_FILE.delete();
    assertThat(auditLog.historyFor("anything")).isEmpty();
  }

  @Test
  void historyFor_ReturnsEntriesInChronologicalOrder() {
    // Entries are appended in order — historyFor must preserve that order.
    auditLog.record("thermo-1", "first");
    auditLog.record("thermo-1", "second");
    auditLog.record("thermo-1", "third");

    List<String> operations = auditLog.historyFor("thermo-1").stream()
        .map(AuditEntry::operation)
        .toList();
    assertThat(operations).containsExactly("first", "second", "third");

    List<Instant> timestamps = auditLog.historyFor("thermo-1").stream()
        .map(e -> Instant.parse(e.timestamp()))
        .toList();
    for (int i = 1; i < timestamps.size(); i++) {
      assertThat(timestamps.get(i))
          .as("entry %d must not predate entry %d", i, i - 1)
          .isAfterOrEqualTo(timestamps.get(i - 1));
    }
  }
}
