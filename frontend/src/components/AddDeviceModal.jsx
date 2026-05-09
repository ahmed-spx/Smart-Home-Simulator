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

export function AddDeviceModal({
  visible,
  onHide,
  onSubmit,
  existingLocations,
  existingThermostatLocations = [],
}) {
  const [type, setType] = useState('Light');
  const [name, setName] = useState('');
  const [location, setLocation] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [submitError, setSubmitError] = useState(null);

  // Reset form whenever the modal opens
  useEffect(() => {
    if (visible) {
      setType('Light');
      setName('');
      setLocation('');
      setSubmitError(null);
    }
  }, [visible]);

  // Clear server error as soon as the user changes anything (so the stale
  // message doesn't linger after they fix it).
  useEffect(() => {
    setSubmitError(null);
  }, [type, name, location]);

  // Defense in depth: the backend is the source of truth (one thermostat per
  // location). This is a UX hint that catches the conflict before the request
  // is even sent. Case-insensitive to match the backend's check.
  const normalizedLoc = location.trim().toLowerCase();
  const wouldConflict =
    type === 'Thermostat' &&
    normalizedLoc.length > 0 &&
    existingThermostatLocations
      .map((l) => l.toLowerCase())
      .includes(normalizedLoc);

  const canSubmit =
    name.trim().length > 0 &&
    location.trim().length > 0 &&
    !wouldConflict &&
    !submitting;

  const handleSubmit = async () => {
    if (!canSubmit) return;
    setSubmitting(true);
    setSubmitError(null);
    try {
      await onSubmit({
        type,
        name: name.trim(),
        location: location.trim(),
      });
      onHide();
    } catch (e) {
      setSubmitError(e?.message ?? String(e));
    } finally {
      setSubmitting(false);
    }
  };

  const locationOptions = existingLocations.map((l) => ({ label: l, value: l }));

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
        <Dropdown
          value={type}
          options={typeOptions}
          onChange={(e) => setType(e.value)}
        />
      </div>

      <div className="form-row">
        <label>Name</label>
        <InputText
          value={name}
          onChange={(e) => setName(e.target.value)}
          placeholder="e.g. Hallway Light"
        />
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

      {/* Proactive (client-side) hint — soft warning */}
      {wouldConflict && (
        <div className="form-warning">
          {location} already has a thermostat. Only one thermostat per location is allowed.
        </div>
      )}

      {/* Backend-rejected error (post-submit) — hard error */}
      {submitError && <div className="form-error">{submitError}</div>}

      <div className="form-actions">
        <Button label="Cancel" outlined onClick={onHide} />
        <Button
          label="Add"
          onClick={handleSubmit}
          loading={submitting}
          disabled={!canSubmit}
        />
      </div>
    </Dialog>
  );
}