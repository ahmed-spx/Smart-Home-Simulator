package smarthomesim.demo.domain.doorlock;
import smarthomesim.demo.domain.Device;
import smarthomesim.demo.domain.DeviceType;

public class DoorLock extends Device{

    //field
    private IDoorLock DoorLockState;

    //constructor
    public DoorLock(String id, String name, String location) {
        super(id, name, location, DeviceType.Lock);
        this.DoorLockState = new UnlockedState();
    }

    //setter
    public void toggleLock() {
        this.DoorLockState.toggleLock(this);
    }
    //getter
    public DoorLockStates getDoorLockState() {
        return this.DoorLockState.getDoorLockState();
    }

    //transition
    public void changeState(IDoorLock newState) {
        this.DoorLockState = newState;
    }
}
