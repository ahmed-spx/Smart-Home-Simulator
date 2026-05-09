import { useEffect, useState } from 'react';
import { getAllDevices, deleteDevice } from './api/devices';
import { Button } from 'primereact/button';
import { Dropdown } from 'primereact/dropdown';
import { confirmDialog, ConfirmDialog } from 'primereact/confirmdialog';
import { LightCard } from './components/LightCard';
import { FanCard } from './components/FanCard';
import { ThermostatCard } from './components/ThermostatCard';
import { DoorLockCard } from './components/DoorLockCard';
import { AddDeviceModal } from './components/AddDeviceModal';
import { SimConfigModal } from './components/SimConfigModal';

// Sentinel used by the filter dropdowns to mean "no filter applied".
// Don't use '' or null — PrimeReact's Dropdown treats those as "unselected"
// and fires onChange inconsistently when you pick the option explicitly.
const ALL = '__ALL__';

function App() {
  const [devices, setDevices] = useState([]);
  const [error, setError] = useState(null);

  const [onFilter, setOnFilter] = useState(false);
  const [offFilter, setOffFilter] = useState(false);
  const [locationFilter, setLocationFilter] = useState(ALL);
  const [typeFilter, setTypeFilter] = useState(ALL);

  const [showAddModal, setShowAddModal] = useState(false);
  const [showSimConfig, setShowSimConfig] = useState(false);

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

  const removeDevice = (id) =>
    setDevices((prev) => prev.filter((d) => d.id !== id));

  if (error) return <p style={{ padding: 24 }}>Could not reach the backend: {error}</p>;

  const locations = [...new Set(devices.map((d) => d.location))].sort();
  const types     = [...new Set(devices.map((d) => d.type))].sort();
  const thermostats = devices.filter((d) => d.type === 'Thermostat');

  const locationOptions = [
    { label: 'All Locations', value: ALL },
    ...locations.map((l) => ({ label: l, value: l })),
  ];
  const typeOptions = [
    { label: 'All Types', value: ALL },
    ...types.map((t) => ({ label: t === 'Lock' ? 'Door Lock' : t, value: t })),
  ];

  const isActiveDevice = (d) =>
    d.type === 'Lock' || d.state === 'On' || (d.type === 'Thermostat' && d.state !== 'Off');

  const filtered = devices
    .filter((d) => {
      if (onFilter)  return isActiveDevice(d);
      if (offFilter) return d.type !== 'Lock' && !isActiveDevice(d);
      return true;
    })
    .filter((d) => locationFilter === ALL || d.location === locationFilter)
    .filter((d) => typeFilter     === ALL || d.type     === typeFilter);

  const byLocation = filtered.reduce((acc, d) => {
    (acc[d.location] = acc[d.location] || []).push(d);
    return acc;
  }, {});
  Object.values(byLocation).forEach((arr) => arr.sort((a, b) => a.name.localeCompare(b.name)));
  const sortedLocations = Object.keys(byLocation).sort();

  const hasActiveFilters =
    onFilter || offFilter || locationFilter !== ALL || typeFilter !== ALL;

  const handleDelete = (d) => {
    confirmDialog({
      message: `Delete "${d.name}"? This cannot be undone.`,
      header: 'Delete Device',
      icon: 'pi pi-exclamation-triangle',
      acceptClassName: 'p-button-danger',
      acceptLabel: 'Delete',
      rejectLabel: 'Cancel',
      accept: async () => {
        await deleteDevice(d.id);
        removeDevice(d.id);
      },
    });
  };

  const renderCard = (d) => {
    const props = { key: d.id, device: d, onUpdate: replaceDevice, onDelete: () => handleDelete(d) };
    if (d.type === 'Light')      return <LightCard     {...props} />;
    if (d.type === 'Fan')        return <FanCard        {...props} />;
    if (d.type === 'Thermostat') return <ThermostatCard {...props} />;
    if (d.type === 'Lock')       return <DoorLockCard   {...props} />;
    return null;
  };

  return (
    <div>
      <ConfirmDialog />

      {/* ── Header ── */}
      <div className="app-header">
        <i className="pi pi-home" style={{ fontSize: 22, color: '#6366f1' }} />
        <h1>Smart Home Simulator</h1>
        <div className="header-actions">
          <Button
            label="Sim Config"
            icon="pi pi-sliders-h"
            outlined
            severity="secondary"
            size="small"
            onClick={() => setShowSimConfig(true)}
          />
          <Button
            label="Add Device"
            icon="pi pi-plus"
            size="small"
            onClick={() => setShowAddModal(true)}
          />
        </div>
      </div>

      {/* ── Filter bar ── */}
      <div className="filter-bar">
        <div className="filter-group">
          <Button
            label="On"
            size="small"
            severity={onFilter ? 'success' : 'secondary'}
            outlined={!onFilter}
            onClick={() => { setOnFilter(!onFilter); setOffFilter(false); }}
          />
          <Button
            label="Off"
            size="small"
            severity="secondary"
            outlined={!offFilter}
            onClick={() => { setOffFilter(!offFilter); setOnFilter(false); }}
          />
        </div>

        <Dropdown
          value={locationFilter}
          onChange={(e) => setLocationFilter(e.value)}
          options={locationOptions}
          placeholder="All Locations"
          className="filter-dropdown"
        />

        <Dropdown
          value={typeFilter}
          onChange={(e) => setTypeFilter(e.value)}
          options={typeOptions}
          placeholder="All Types"
          className="filter-dropdown"
        />

        {hasActiveFilters && (
          <Button
            label="Clear"
            icon="pi pi-times"
            text
            severity="danger"
            size="small"
            onClick={() => {
              setOnFilter(false); setOffFilter(false);
              setLocationFilter(ALL); setTypeFilter(ALL);
            }}
          />
        )}
      </div>

      {/* ── Device grid ── */}
      {sortedLocations.map((location) => (
        <div key={location} className="location-section">
          <div className="location-header">
            <i className="pi pi-map-marker" style={{ color: '#ef4444' }} />
            <span>{location}</span>
            <span className="device-count">
              {byLocation[location].length} {byLocation[location].length === 1 ? 'device' : 'devices'}
            </span>
          </div>
          <div className="device-grid">{byLocation[location].map(renderCard)}</div>
        </div>
      ))}

      {filtered.length === 0 && (
        <div className="empty-state">
          <i className="pi pi-inbox" style={{ fontSize: 40, opacity: 0.3, display: 'block', marginBottom: 12 }} />
          No devices match the current filters.
        </div>
      )}

      {/* ── Modals ── */}
      {showAddModal && (
        <AddDeviceModal
          locations={locations}
          onClose={() => setShowAddModal(false)}
          onAdd={(newDevice) => {
            setDevices((prev) => [...prev, newDevice]);
            setShowAddModal(false);
          }}
        />
      )}

      {showSimConfig && (
        <SimConfigModal
          thermostats={thermostats}
          onUpdate={replaceDevice}
          onClose={() => setShowSimConfig(false)}
        />
      )}
    </div>
  );
}

export default App;
