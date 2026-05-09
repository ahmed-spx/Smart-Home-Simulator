import { Dialog } from 'primereact/dialog';
import { InputText } from 'primereact/inputtext';
import { Dropdown } from 'primereact/dropdown';
import { Button } from 'primereact/button';
import { useState, useEffect } from 'react';

const typeOptions = [
  { label: 'Light',     value: 'Light' },
  { label: 'Fan',       value: 'Fan' },
  { label: 'Thermostat',value: 'Thermostat' },
  { label: 'Door Lock', value: 'DoorLock' },
];

export function AddDeviceModal({ visible, onHide, onSubmit, existingLocations }) {
  const [type, setType] = useState('Light');
  const [name, setName] = useState('');
  const [location, setLocation] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [submitError, setSubmitError] = useState(null);

  useEffect(() => {
    if (visible) {
      setType('Light');
      setName('');
      setLocation('');
      setSubmitError(null);
    }
  }, [visible]);

  const handleSubmit = async () => {
    if (!name.trim() || !location.trim()) return;
    setSubmitting(true);
    setSubmitError(null);
    try {
      await onSubmit({ type, name: name.trim(), location: location.trim() });
      onHide();
    } catch (e) {
      setSubmitError(e?.message ?? String(e));
    } finally {
      setSubmitting(false);
    }
  };

  const locationOptions = existingLocations.map(l => ({ label: l, value: l }));

  return (
    <Dialog
      header="Add Device"
      visible={visible}
      onHide={onHide}
      style={{ width: '90vw', maxWidth: '420px' }}
      modal
    >
      <div className="form-row">
        <label>Type</label>
        <Dropdown value={type} options={typeOptions}
          onChange={(e) => setType(e.value)} />
      </div>
      <div className="form-row">
        <label>Name</label>
        <InputText value={name} onChange={(e) => setName(e.target.value)}
          placeholder="e.g. Hallway Light" />
      </div>
      <div className="form-row">
        <label>Location</label>
        <Dropdown
          value={location}
          options={locationOptions}
          onChange={(e) => setLocation(e.value)}
          editable
          placeholder="Select or type a location"
        />
      </div>
      {submitError && <div className="form-error">{submitError}</div>}
      <div className="form-actions">
        <Button label="Cancel" outlined onClick={onHide} />
        <Button label="Add" onClick={handleSubmit}
          loading={submitting} disabled={!name.trim() || !location.trim()} />
      </div>
    </Dialog>
  );
}