import { InputSwitch } from 'primereact/inputswitch';
import { SelectButton } from 'primereact/selectbutton';
import { CardShell } from './CardShell';
import { toggleFan, setFanSpeed } from '../api/devices';

const speedOptions = [
  { label: 'Low', value: 'Low' },
  { label: 'Med', value: 'Medium' },
  { label: 'High', value: 'High' },
];

export function FanCard({ device, onUpdate, onRemove }) {
  const isOn = device.state === 'On';
  return (
    <CardShell icon="pi-cog" name={device.name} typeLabel="Fan"
               isActive={isOn} statusLabel={device.state} onRemove={onRemove}>
      <div className="control-row">
        <span className="control-label">Power</span>
        <InputSwitch checked={isOn}
          onChange={async () => onUpdate(await toggleFan(device.id))} />
      </div>
      <div className="control-row">
        <span className="control-label">Speed</span>
        <SelectButton className="compact-select" value={device.speed} options={speedOptions} disabled={!isOn}
          onChange={async (e) => e.value && onUpdate(await setFanSpeed(device.id, e.value))} />
      </div>
    </CardShell>
  );
}