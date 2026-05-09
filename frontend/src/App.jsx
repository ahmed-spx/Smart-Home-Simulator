import { useEffect, useRef, useState } from 'react';
import { ConfirmDialog, confirmDialog } from 'primereact/confirmdialog';
import { Toast } from 'primereact/toast';
import {
  getAllDevices,
  registerDevice, removeDevice, resetAllDevices,
  setThermostatMultiplier, setThermostatAmbient,
} from './api/devices';
import { Header } from './components/Header';
import { FilterBar } from './components/FilterBar';
import { SimulationSettings } from './components/SimulationSettings';
import { AddDeviceModal } from './components/AddDeviceModal';
import { LightCard } from './components/LightCard';
import { FanCard } from './components/FanCard';
import { ThermostatCard } from './components/ThermostatCard';
import { DoorLockCard } from './components/DoorLockCard';

function App() {
  const [devices, setDevices] = useState([]);
  const [filters, setFilters] = useState({ status: 'all', location: 'all', type: 'all' });
  const [multiplier, setMultiplier] = useState(1);
  const [showAdd, setShowAdd] = useState(false);
  const toast = useRef(null);

  const showError = (e) => toast.current?.show({
    severity: 'error',
    summary: 'Error',
    detail: e?.message ?? String(e),
    life: 4000,
  });

  const showInfo = (summary, detail) => toast.current?.show({
    severity: 'info', summary, detail, life: 2500,
  });

  useEffect(() => {
    const refresh = () => getAllDevices().then(setDevices).catch(showError);
    refresh();
    const es = new EventSource('http://localhost:8080/api/events');
    ['device-changed', 'device-added', 'device-removed'].forEach(name => {
      es.addEventListener(name, refresh);
    });
    es.onerror = () => toast.current?.show({
      severity: 'warn',
      summary: 'Live updates disconnected',
      detail: 'Backend unreachable. Reconnecting…',
      life: 3000,
    });
    return () => es.close();
  }, []);

  const replaceDevice = (updated) =>
    setDevices(prev => prev.map(d => d.id === updated.id ? updated : d));

  // Derived
  const thermostats = devices.filter(d => d.type === 'Thermostat');
  const locations = [...new Set(devices.map(d => d.location))];
  const types = [...new Set(devices.map(d => d.type))];

  const ambientByLocation = {};
  for (const loc of locations) {
    const t = thermostats.find(t => t.location === loc);
    if (t) ambientByLocation[loc] = t.ambientTemperature;
  }

  const visible = devices.filter(d => {
    if (filters.location !== 'all' && d.location !== filters.location) return false;
    if (filters.type !== 'all' && d.type !== filters.type) return false;
    if (filters.status !== 'all') {
      if (d.type === 'Lock') {
        if (filters.status === 'off') return false;
      } else {
        const isOn = d.state !== 'Off';
        if (filters.status === 'on'  && !isOn) return false;
        if (filters.status === 'off' &&  isOn) return false;
      }
    }
    return true;
  });

  const byLocation = visible.reduce((acc, d) => {
    (acc[d.location] = acc[d.location] || []).push(d);
    return acc;
  }, {});

  const handleAmbient = async (location, value) => {
    try {
      const inLoc = thermostats.filter(t => t.location === location);
      await Promise.all(inLoc.map(t => setThermostatAmbient(t.id, value)));
    } catch (e) { showError(e); }
  };

  const handleMultiplier = async (value) => {
    setMultiplier(value);
    try {
      const key = `x${value}`;
      await Promise.all(thermostats.map(t => setThermostatMultiplier(t.id, key)));
    } catch (e) { showError(e); }
  };

  // Modal calls this; rethrow keeps the modal open on error.
  const handleAdd = async (data) => {
    try { await registerDevice(data); }
    catch (e) { showError(e); throw e; }
  };

  const handleRemove = (deviceId) => {
    confirmDialog({
      message: 'Remove this device? Its persisted state will be deleted.',
      header: 'Remove Device',
      icon: 'pi pi-exclamation-triangle',
      acceptClassName: 'p-button-danger',
      accept: async () => {
        try { await removeDevice(deviceId); }
        catch (e) { showError(e); }
      },
    });
  };

  const handleReset = () => {
    confirmDialog({
      message: 'Reset ALL devices to factory defaults? This overwrites persisted state.',
      header: 'Reset All Devices',
      icon: 'pi pi-exclamation-triangle',
      acceptClassName: 'p-button-danger',
      accept: async () => {
        try {
          await resetAllDevices();
          setMultiplier(1);
          showInfo('Devices reset', 'All devices restored to defaults.');
        } catch (e) { showError(e); }
      },
    });
  };

  const renderCard = (d) => {
    const props = {
      key: d.id, device: d, onUpdate: replaceDevice,
      onRemove: () => handleRemove(d.id),
      onError: showError,
    };
    if (d.type === 'Light')      return <LightCard {...props} />;
    if (d.type === 'Fan')        return <FanCard {...props} />;
    if (d.type === 'Thermostat') return <ThermostatCard {...props} />;
    if (d.type === 'Lock')       return <DoorLockCard {...props} />;
    return null;
  };

  return (
    <div className="app-shell">
      <Toast ref={toast} position="bottom-right" />
      <ConfirmDialog />
      <AddDeviceModal
        visible={showAdd}
        onHide={() => setShowAdd(false)}
        onSubmit={handleAdd}
        existingLocations={locations}
      />

      <Header multiplier={multiplier} onAddDevice={() => setShowAdd(true)} />

      <div className="app-body">
        <main className="app-main">
          <FilterBar filters={filters} onChange={setFilters}
            locations={locations} types={types} />

          {Object.entries(byLocation).map(([location, items]) => (
            <div key={location} className="location-section">
              <div className="location-header">
                <i className="pi pi-map-marker" style={{ color: '#ef4444' }} />
                <span>{location}</span>
                <span className="device-count">
                  {items.length} {items.length === 1 ? 'device' : 'devices'}
                </span>
              </div>
              <div className="device-grid">{items.map(renderCard)}</div>
            </div>
          ))}

          {visible.length === 0 && (
            <p className="empty-state">No devices match the current filters.</p>
          )}
        </main>

        <SimulationSettings
          ambientByLocation={ambientByLocation}
          onAmbientChange={handleAmbient}
          multiplier={multiplier}
          onMultiplierChange={handleMultiplier}
          onReset={handleReset}
        />
      </div>
    </div>
  );
}

export default App;