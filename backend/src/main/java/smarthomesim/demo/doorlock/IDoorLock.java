package smarthomesim.demo.doorlock;

public interface IDoorLock {
    public DoorLockStates getDoorLockState();
    public void toggleLock(DoorLock doorLock);
}
