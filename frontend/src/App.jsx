import { useEffect, useState } from 'react';
import { getAllDevices } from './api/devices';
import { LightCard } from './components/LightCard';
import { FanCard } from './components/FanCard';
import { ThermostatCard } from './components/ThermostatCard';
import { DoorLockCard } from './components/DoorLockCard';

function App() {
  const [devices, setDevices] = useState([]);
  const [error, setError] = useState(null);

  useEffect(() => {
    // Initial load
    getAllDevices().then(setDevices).catch((e) => setError(e.message));

    // Live updates via SSE — push from server, no polling
    const es = new EventSource('http://localhost:8080/api/events');
    const refresh = () => getAllDevices().then(setDevices).catch(() => {});
    es.addEventListener('device-changed', refresh);
    es.addEventListener('device-added',   refresh);
    es.addEventListener('device-removed', refresh);
    es.onerror = () => {
    // EventSource auto-reconnects on transient errors; nothing to do.
    };
    return () => es.close();
  }, []);

  const replaceDevice = (updated) =>
    setDevices((prev) => prev.map((d) => (d.id === updated.id ? updated : d)));

  if (error) return <p style={{ padding: 24 }}>Could not reach the backend: {error}</p>;

  const byLocation = devices.reduce((acc, d) => {
    (acc[d.location] = acc[d.location] || []).push(d);
    return acc;
  }, {});

  const renderCard = (d) => {
    const props = { key: d.id, device: d, onUpdate: replaceDevice };
    if (d.type === 'Light') return <LightCard {...props} />;
    if (d.type === 'Fan') return <FanCard {...props} />;
    if (d.type === 'Thermostat') return <ThermostatCard {...props} />;
    if (d.type === 'Lock') return <DoorLockCard {...props} />;
    return null;
  };

  return (
    <div>
      <div className="app-header">
        <i className="pi pi-home" style={{ fontSize: 22, color: '#6366f1' }}></i>
        <h1>Smart Home Simulator</h1>
      </div>
      {Object.entries(byLocation).map(([location, items]) => (
        <div key={location} className="location-section">
          <div className="location-header">
            <i className="pi pi-map-marker" style={{ color: '#ef4444' }}></i>
            <span>{location}</span>
            <span className="device-count">{items.length} {items.length === 1 ? 'device' : 'devices'}</span>
          </div>
          <div className="device-grid">{items.map(renderCard)}</div>
        </div>
      ))}
    </div>
  );
}

export default App;