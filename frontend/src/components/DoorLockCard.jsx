import { Button } from 'primereact/button';
import { CardShell } from './CardShell';
import { toggleDoorLock } from '../api/devices';

export function DoorLockCard({ device, onUpdate, onRemove, onError }) {
  const isUnlocked = device.state === 'Unlocked';

  const safe = async (fn) => {
    try { onUpdate(await fn()); }
    catch (e) { onError?.(e); }
  };

  return (
    <CardShell icon="pi-lock" name={device.name} typeLabel="Door Lock"
               isActive={true} statusLabel="On" onRemove={onRemove}>
      <div className="control-row">
        <span className="control-label">{device.state}</span>
        <Button
          label={isUnlocked ? 'Lock' : 'Unlock'}
          severity={isUnlocked ? 'secondary' : 'success'}
          size="small"
          onClick={() => safe(() => toggleDoorLock(device.id))}
        />
      </div>
    </CardShell>
  );
}