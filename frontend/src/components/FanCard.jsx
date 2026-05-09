import { InputSwitch } from 'primereact/inputswitch';
import { SelectButton } from 'primereact/selectbutton';
import { CardShell } from './CardShell';
import { toggleFan, setFanSpeed } from '../api/devices';

const speedOptions = [
  { label: 'Low',  value: 'Low' },
  { label: 'Med',  value: 'Medium' },
  { label: 'High', value: 'High' },
];

export function FanCard({ device, onUpdate, onRemove, onError }) {
  const isOn = device.state === 'On';

  const safe = async (fn) => {
    try { onUpdate(await fn()); }
    catch (e) { onError?.(e); }
  };

  return (
    <CardShell icon="pi-cog" name={device.name} typeLabel="Fan"
               isActive={isOn} statusLabel={device.state} onRemove={onRemove}>
      <div className="control-row">
        <span className="control-label">Power</span>
        <InputSwitch checked={isOn}
          onChange={() => safe(() => toggleFan(device.id))} />
      </div>
      <div className="control-row">
        <span className="control-label">Speed</span>
        <SelectButton className="compact-select"
          value={device.speed} options={speedOptions} disabled={!isOn}
          onChange={(e) => e.value && safe(() => setFanSpeed(device.id, e.value))} />
      </div>
    </CardShell>
  );
}