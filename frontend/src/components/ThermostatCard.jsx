import { InputSwitch } from 'primereact/inputswitch';
import { SelectButton } from 'primereact/selectbutton';
import { Slider } from 'primereact/slider';
import { CardShell } from './CardShell';
import {
  toggleThermostat, setThermostatMode, setThermostatTemperature,
} from '../api/devices';

const modeOptions = [
  { label: 'Heat', value: 'Heating' },
  { label: 'Cool', value: 'Cooling' },
  { label: 'Auto', value: 'Auto' },
];

export function ThermostatCard({ device, onUpdate, onRemove, onError }) {
  const isOn = device.state !== 'Off';

  const safe = async (fn) => {
    try { onUpdate(await fn()); }
    catch (e) { onError?.(e); }
  };

  return (
    <CardShell icon="pi-chart-line" name={device.name} typeLabel="Thermostat"
               isActive={isOn} statusLabel={isOn ? 'On' : 'Off'} onRemove={onRemove}>
      <div className="control-row">
        <span className="control-label">Power</span>
        <InputSwitch checked={isOn}
          onChange={() => safe(() => toggleThermostat(device.id))} />
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
        <Slider value={device.desiredTemperature} min={60} max={80} disabled={!isOn}
          style={{ flex: 1 }}
          onChange={(e) => safe(() => setThermostatTemperature(device.id, e.value))} />
        <span className="control-value">{device.desiredTemperature}°F</span>
      </div>
      <div className="control-row">
        <span className="control-label">Mode</span>
        <SelectButton className="compact-select" value={device.mode} options={modeOptions} disabled={!isOn}
          onChange={(e) => e.value && safe(() => setThermostatMode(device.id, e.value))} />
      </div>
    </CardShell>
  );
}