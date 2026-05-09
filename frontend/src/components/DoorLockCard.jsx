import { Button } from 'primereact/button';
import { CardShell } from './CardShell';
import { toggleDoorLock } from '../api/devices';

export function DoorLockCard({ device, onUpdate }) {
  const isUnlocked = device.state === 'Unlocked';
  return (
    <CardShell icon="pi-lock" name={device.name} typeLabel="Door Lock"
               isActive={true} statusLabel="On">
      <div className="control-row">
        <span className="control-label">{device.state}</span>
        <Button
          label={isUnlocked ? 'Lock' : 'Unlock'}
          severity={isUnlocked ? 'secondary' : 'success'}
          size="small"
          onClick={async () => onUpdate(await toggleDoorLock(device.id))}
        />
      </div>
    </CardShell>
  );
}