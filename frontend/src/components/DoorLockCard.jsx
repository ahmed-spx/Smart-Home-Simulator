import { InputSwitch } from 'primereact/inputswitch';
import { CardShell } from './CardShell';
import { toggleDoorLock } from '../api/devices';

export function DoorLockCard({ device, onUpdate, onDelete }) {
  const isUnlocked = device.state === 'Unlocked';
  return (
    <CardShell icon="pi-lock" name={device.name} typeLabel="Door Lock"
               isActive={true} statusLabel={device.state} onDelete={onDelete}>
      <div className="control-row">
        <span className="control-label">{isUnlocked ? 'Unlocked' : 'Locked'}</span>
        <InputSwitch checked={isUnlocked}
          onChange={async () => onUpdate(await toggleDoorLock(device.id))} />
      </div>
    </CardShell>
  );
}