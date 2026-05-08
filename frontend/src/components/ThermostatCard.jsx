import { InputSwitch } from 'primereact/inputswitch';
import { SelectButton } from 'primereact/selectbutton';
import { Slider } from 'primereact/slider';
import { CardShell } from './CardShell';
import {
  toggleThermostat, setThermostatMode,
  setThermostatTemperature, setThermostatMultiplier,
} from '../api/devices';

const modeOptions = [
  { label: 'Heat', value: 'Heating' },
  { label: 'Cool', value: 'Cooling' },
  { label: 'Auto', value: 'Auto' },
];

const multiplierOptions = [
  { label: '1x', value: 'x1' },
  { label: '2x', value: 'x2' },
  { label: '5x', value: 'x5' },
  { label: '10x', value: 'x10' },
];

export function ThermostatCard({ device, onUpdate }) {
  const isOn = device.state !== 'Off';
  return (
    <CardShell icon="pi-chart-line" name={device.name} typeLabel="Thermostat"
               isActive={isOn} statusLabel={device.state}>
      <div className="control-row">
        <span className="control-label">Power</span>
        <InputSwitch checked={isOn}
          onChange={async () => onUpdate(await toggleThermostat(device.id))} />
      </div>
      <div className="thermo-display">
        <div>
          <span className="thermo-target">{device.desiredTemperature}</span>
          <span className="thermo-target-unit">°F target</span>
        </div>
        <div>
          <div className="thermo-ambient-label">Ambient</div>
          <div className="thermo-ambient-value">{device.ambientTemperature}°F</div>
        </div>
      </div>
      <div className="control-row">
        <span className="control-label">Target</span>
        <Slider value={device.desiredTemperature} min={50} max={90} disabled={!isOn}
          style={{ flex: 1 }}
          onChange={async (e) => onUpdate(await setThermostatTemperature(device.id, e.value))} />
        <span className="control-value">{device.desiredTemperature}°F</span>
      </div>
      <div className="control-row">
        <span className="control-label">Mode</span>
        <SelectButton value={device.mode} options={modeOptions} disabled={!isOn}
          onChange={async (e) => e.value && onUpdate(await setThermostatMode(device.id, e.value))} />
      </div>
      <div className="control-row">
        <span className="control-label">Multiplier</span>
        <SelectButton value={device.multiplier} options={multiplierOptions}
          onChange={async (e) => e.value && onUpdate(await setThermostatMultiplier(device.id, e.value))} />
      </div>
    </CardShell>
  );
}