import { Dialog } from 'primereact/dialog';
import { Button } from 'primereact/button';
import { SelectButton } from 'primereact/selectbutton';
import { setThermostatMultiplier } from '../api/devices';

const multiplierOptions = [
  { label: '1×',  value: 'x1'  },
  { label: '2×',  value: 'x2'  },
  { label: '5×',  value: 'x5'  },
  { label: '10×', value: 'x10' },
];

export function SimConfigModal({ thermostats, onUpdate, onClose }) {
  const header = (
    <span>
      <i className="pi pi-sliders-h" style={{ marginRight: 8, color: '#6366f1' }} />
      Simulation Config
    </span>
  );

  const footer = (
    <div style={{ textAlign: 'right' }}>
      <Button label="Close" onClick={onClose} />
    </div>
  );

  return (
    <Dialog
      header={header}
      visible
      onHide={onClose}
      footer={footer}
      style={{ width: '680px', maxWidth: '95vw' }}
      modal
      draggable={false}
      resizable={false}
    >
      <p style={{ margin: '0 0 16px', fontSize: 13, color: '#6b7280' }}>
        Current ambient temperatures and tick-rate multiplier per thermostat location.
      </p>

      {thermostats.length === 0 ? (
        <p style={{ color: '#9ca3af', fontSize: 14 }}>No thermostats registered.</p>
      ) : (
        <div className="sim-table-wrap">
          <table className="sim-table">
            <thead>
              <tr>
                <th>Location</th>
                <th>Name</th>
                <th>Ambient</th>
                <th>Target</th>
                <th>Tick Rate</th>
              </tr>
            </thead>
            <tbody>
              {thermostats.map((t) => (
                <tr key={t.id}>
                  <td>{t.location}</td>
                  <td>{t.name}</td>
                  <td className="temp-cell">{t.ambientTemperature}°F</td>
                  <td className="temp-cell">{t.desiredTemperature}°F</td>
                  <td>
                    <SelectButton
                      value={t.multiplier}
                      options={multiplierOptions}
                      onChange={async (e) =>
                        e.value && onUpdate(await setThermostatMultiplier(t.id, e.value))
                      }
                    />
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </Dialog>
  );
}
