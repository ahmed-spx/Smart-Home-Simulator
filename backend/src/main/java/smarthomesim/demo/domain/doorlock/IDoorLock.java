package smarthomesim.demo.domain.doorlock;

//  State strategy for a DoorLock. Implementations represent Locked and Unlocked. 

public interface IDoorLock {
    public DoorLockStates getDoorLockState();
    public void toggleLock(DoorLock doorLock);
}
