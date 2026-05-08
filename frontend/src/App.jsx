import { useEffect, useState } from 'react';
import {
  getAllDevices,
  toggleLight, setLightBrightness,
  toggleFan, setFanSpeed,
  toggleThermostat, setThermostatMode, setThermostatTemperature, setThermostatSpeed,
  toggleDoorLock,
} from './api/devices';
import './App.css';

function App() {
  const [devices, setDevices] = useState([]);
  const [error, setError] = useState(null);

  // Load all devices once when the page loads.
  useEffect(() => {
    getAllDevices()
      .then(setDevices)
      .catch((e) => setError(e.message));
  }, []);

  useEffect(() => {
    const intervalId = setInterval(() => {
      getAllDevices().then(setDevices).catch(() => {});
    }, 500);
    return () => clearInterval(intervalId);
  }, []);

  // Helper: replace one device in the list with the updated copy returned
  // by the backend, so the UI re-renders with fresh state.
  const replaceDevice = (updated) =>
    setDevices((prev) => prev.map((d) => (d.id === updated.id ? updated : d)));

  if (error) return <p>Could not reach the backend: {error}</p>;

  return (
    <div style={{ padding: 24, fontFamily: 'sans-serif' }}>
      <h1>Smart Home Simulator</h1>
      {devices.map((device) => {
        if (device.type === 'Light') {
          return <LightCard key={device.id} device={device} onUpdate={replaceDevice} />;
        }
        if (device.type === 'Fan') {
          return <FanCard key={device.id} device={device} onUpdate={replaceDevice} />;
        }
        if (device.type === 'Thermostat') {
          return <ThermostatCard key={device.id} device={device} onUpdate={replaceDevice} />;
        }
        if (device.type === 'Lock') {
          return <DoorLockCard key={device.id} device={device} onUpdate={replaceDevice} />;
        }
        return null;
      })}
    </div>
  );
}

function Card({ children }) {
  return (
    <div style={{ border: '1px solid #ccc', borderRadius: 8, padding: 16, margin: '12px 0' }}>
      {children}
    </div>
  );
}

function LightCard({ device, onUpdate }) {
  return (
    <Card>
      <h2>{device.name} <small>({device.location})</small></h2>
      <p>State: <b>{device.state}</b> &nbsp; Brightness: <b>{device.brightness}</b></p>
      <button onClick={async () => onUpdate(await toggleLight(device.id))}>Toggle</button>{' '}
      <input
        type="range" min="0" max="100" value={device.brightness}
        onChange={async (e) => onUpdate(await setLightBrightness(device.id, Number(e.target.value)))}
      />
    </Card>
  );
}

function FanCard({ device, onUpdate }) {
  return (
    <Card>
      <h2>{device.name} <small>({device.location})</small></h2>
      <p>State: <b>{device.state}</b> &nbsp; Speed: <b>{device.speed}</b></p>
      <button onClick={async () => onUpdate(await toggleFan(device.id))}>Toggle</button>{' '}
      <select
        value={device.speed}
        onChange={async (e) => onUpdate(await setFanSpeed(device.id, e.target.value))}
      >
        <option>Off</option>
        <option>Low</option>
        <option>Medium</option>
        <option>High</option>
      </select>
    </Card>
  );
}

function ThermostatCard({ device, onUpdate }) {
  const isOn = device.state !== 'Off';
  const modeButton = (label) => (
    <button
      key={label}
      onClick={async () => onUpdate(await setThermostatMode(device.id, label))}
      style={{
        marginRight: 6,
        fontWeight: device.mode === label ? 'bold' : 'normal',
        background: device.mode === label ? '#d0e6ff' : '',
      }}
    >
      {label}
    </button>
  );

  return (
    <Card>
      <h2>{device.name} <small>({device.location})</small></h2>
      <p>
        State: <b>{device.state}</b> &nbsp;
        Desired: <b>{device.desiredTemperature}°F</b> &nbsp;
        Ambient: <b>{device.ambientTemperature}°F</b> &nbsp;
        Speed: <b>{device.multiplier}</b>
      </p>
      <button
        onClick={async () => onUpdate(await toggleThermostat(device.id))}
        style={{ marginRight: 12, fontWeight: 'bold' }}
      >
        {isOn ? 'On' : 'Off'}
      </button>
      {['Auto', 'Heating', 'Cooling'].map(modeButton)}
      {' '}
      <input
        type="number" value={device.desiredTemperature}
        onChange={async (e) => onUpdate(await setThermostatTemperature(device.id, Number(e.target.value)))}
      />{' '}
      <label>
        Sim speed:{' '}
        <select
          value={device.multiplier}
          onChange={async (e) => onUpdate(await setThermostatSpeed(device.id, e.target.value))}
        >
          <option value="x1">1x</option>
          <option value="x2">2x</option>
          <option value="x5">5x</option>
          <option value="x10">10x</option>
        </select>
      </label>
    </Card>
  );
}

function DoorLockCard({ device, onUpdate }) {
  const isLocked = device.state === 'Locked';
  return (
    <Card>
      <h2>{device.name} <small>({device.location})</small></h2>
      <p>State: <b>{device.state}</b></p>
      <button onClick={async () => onUpdate(await toggleDoorLock(device.id))}>
        {isLocked ? 'Unlock' : 'Lock'}
      </button>
    </Card>
  );
}

export default App;