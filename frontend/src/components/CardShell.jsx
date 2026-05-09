import { Button } from 'primereact/button';

export function CardShell({ icon, name, typeLabel, isActive, statusLabel, onRemove, children }) {
  return (
    <div className={`device-card ${isActive ? '' : 'off'}`}>
      <div className="device-card-header">
        <div className="device-card-title">
          <i className={`pi ${icon} device-icon`} />
          <div>
            <p className="device-name">{name}</p>
            <p className="device-type">{typeLabel}</p>
          </div>
        </div>
        <div className="device-card-actions">
          <span className={`status-pill ${isActive ? '' : 'off'}`}>{statusLabel}</span>
          {onRemove && (
            <Button icon="pi pi-times" text rounded size="small"
              className="remove-btn" onClick={onRemove}
              aria-label="Remove device" />
          )}
        </div>
      </div>
      {children}
    </div>
  );
}