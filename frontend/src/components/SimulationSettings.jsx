import { Slider } from 'primereact/slider';
import { SelectButton } from 'primereact/selectbutton';
import { Button } from 'primereact/button';

const speedOptions = [
  { label: '1x',  value: 1 },
  { label: '2x',  value: 2 },
  { label: '5x',  value: 5 },
  { label: '10x', value: 10 },
];

export function SimulationSettings({
  ambientByLocation, onAmbientChange,
  multiplier, onMultiplierChange,
  onReset,
}) {
  return (
    <aside className="simulation-settings">
      <h3 className="sim-title">
        <i className="pi pi-cog" /> Simulation Settings
      </h3>

      {Object.keys(ambientByLocation).length > 0 && (
        <div className="sim-section">
          <h4 className="sim-section-title">AMBIENT TEMPERATURE BY LOCATION</h4>
          {Object.entries(ambientByLocation).map(([location, temp]) => (
            <div key={location} className="ambient-row">
              <span className="ambient-location">{location}</span>
              <Slider
                value={temp}
                min={50} max={90}
                style={{ flex: 1 }}
                onChange={(e) => onAmbientChange(location, e.value)}
              />
              <span className="ambient-value">{temp}°F</span>
            </div>
          ))}
        </div>
      )}

      <div className="sim-section">
        <h4 className="sim-section-title">SIMULATION SPEED</h4>
        <div className="sim-speed-row">
          <span className="sim-speed-label">Thermostat tick rate</span>
          <SelectButton
            className="compact-select"
            value={multiplier}
            options={speedOptions}
            onChange={(e) => e.value !== null && onMultiplierChange(e.value)}
          />
        </div>
      </div>

      <Button
        label="Reset All Devices to Defaults"
        icon="pi pi-refresh"
        severity="danger"
        outlined
        className="reset-button"
        onClick={onReset}
      />
    </aside>
  );
}