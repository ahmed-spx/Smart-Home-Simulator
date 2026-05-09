import { useState } from 'react';
import { Dialog } from 'primereact/dialog';
import { InputText } from 'primereact/inputtext';
import { Dropdown } from 'primereact/dropdown';
import { Button } from 'primereact/button';
import { createDevice } from '../api/devices';

const TYPE_OPTIONS = [
  { label: 'Light',     value: 'Light' },
  { label: 'Fan',       value: 'Fan' },
  { label: 'Thermostat', value: 'Thermostat' },
  { label: 'Door Lock', value: 'Lock' },
];

export function AddDeviceModal({ locations, onClose, onAdd }) {
  const [type, setType]                     = useState('Light');
  const [name, setName]                     = useState('');
  const [location, setLocation]             = useState(locations[0] || '');
  const [customLocation, setCustomLocation] = useState('');
  const [submitting, setSubmitting]         = useState(false);
  const [error, setError]                   = useState('');

  const locationOptions = [
    ...locations.map((l) => ({ label: l, value: l })),
    { label: '+ New location…', value: '__new__' },
  ];

  const effectiveLocation = location === '__new__' ? customLocation.trim() : location;

  const handleAdd = async () => {
    if (!name.trim() || !effectiveLocation) return;
    setSubmitting(true);
    setError('');
    try {
      const newDevice = await createDevice(type, name.trim(), effectiveLocation);
      onAdd(newDevice);
    } catch (e) {
      setError(e.message || 'Failed to add device. Please try again.');
      setSubmitting(false);
    }
  };

  const footer = (
    <div style={{ display: 'flex', gap: 8, justifyContent: 'flex-end' }}>
      <Button label="Cancel" severity="secondary" outlined onClick={onClose} />
      <Button
        label="Add Device"
        icon="pi pi-plus"
        loading={submitting}
        disabled={!name.trim() || !effectiveLocation}
        onClick={handleAdd}
      />
    </div>
  );

  const header = (
    <span>
      <i className="pi pi-plus-circle" style={{ marginRight: 8, color: '#6366f1' }} />
      Add Device
    </span>
  );

  return (
    <Dialog
      header={header}
      visible
      onHide={onClose}
      footer={footer}
      style={{ width: '420px', maxWidth: '95vw' }}
      modal
      draggable={false}
      resizable={false}
    >
      <div className="modal-form">
        <div className="form-field">
          <label>Type</label>
          <Dropdown
            value={type}
            onChange={(e) => setType(e.value)}
            options={TYPE_OPTIONS}
          />
        </div>

        <div className="form-field">
          <label>Name</label>
          <InputText
            value={name}
            onChange={(e) => setName(e.target.value)}
            placeholder="e.g. Desk Lamp"
          />
        </div>

        <div className="form-field">
          <label>Location</label>
          <Dropdown
            value={location}
            onChange={(e) => setLocation(e.value)}
            options={locationOptions}
            placeholder="Select location"
          />
        </div>

        {location === '__new__' && (
          <div className="form-field">
            <label>New Location Name</label>
            <InputText
              value={customLocation}
              onChange={(e) => setCustomLocation(e.target.value)}
              placeholder="e.g. Garage"
            />
          </div>
        )}

        {error && <small className="p-error">{error}</small>}
      </div>
    </Dialog>
  );
}
