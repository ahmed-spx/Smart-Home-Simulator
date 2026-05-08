package smarthomesim.demo.doorlock;

public class UnlockedState implements IDoorLock {
    @Override
    public DoorLockStates getDoorLockState() {
        return DoorLockStates.Unlocked;
    }

    @Override
    public void toggleLock(DoorLock doorLock) {
        doorLock.changeState(new LockedState());
    }
    
}
