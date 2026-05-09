package smarthomesim.demo.domain.doorlock;

public class LockedState implements IDoorLock {
    @Override
    public DoorLockStates getDoorLockState() {
        return DoorLockStates.Locked;
    }

    @Override
    public void toggleLock(DoorLock doorLock) {
        doorLock.changeState(new UnlockedState());
    }
    
}
