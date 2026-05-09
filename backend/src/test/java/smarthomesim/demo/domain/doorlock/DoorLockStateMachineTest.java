package smarthomesim.demo.domain.doorlock;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("DoorLock state machine")
class DoorLockStateMachineTest {

  private DoorLock newLock() {
    return new DoorLock("lock-test", "Front Door", "Living Room");
  }

  @Test
  void newDoorLock_DefaultState_IsUnlocked() {
    assertThat(newLock().getDoorLockState()).isEqualTo(DoorLockStates.Unlocked);
  }

  @Test
  void toggleLock_FromUnlocked_TransitionsToLocked() {
    DoorLock lock = newLock();
    lock.toggleLock();
    assertThat(lock.getDoorLockState()).isEqualTo(DoorLockStates.Locked);
  }

  @Test
  void toggleLock_FromLocked_TransitionsToUnlocked() {
    DoorLock lock = newLock();
    lock.toggleLock(); // -> Locked
    lock.toggleLock(); // -> Unlocked
    assertThat(lock.getDoorLockState()).isEqualTo(DoorLockStates.Unlocked);
  }

  @Test
  void toggleLock_TwiceFromUnlocked_ReturnsToUnlocked() {
    DoorLock lock = newLock();
    DoorLockStates initial = lock.getDoorLockState();
    lock.toggleLock();
    lock.toggleLock();
    assertThat(lock.getDoorLockState()).isEqualTo(initial);
  }

  @Test
  void doorLockStatesEnum_HasExactlyLockedAndUnlocked() {
    assertThat(DoorLockStates.values())
        .containsExactly(DoorLockStates.Locked, DoorLockStates.Unlocked);
  }
}
