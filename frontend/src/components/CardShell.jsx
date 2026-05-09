import { Button } from 'primereact/button';

export function CardShell({ icon, name, typeLabel, isActive, statusLabel, children, onDelete }) {
  return (
    <div className={`device-card ${isActive ? '' : 'off'}`}>
      <div className="device-card-header">
        <div className="device-card-title">
          <div className={`device-icon-wrap ${isActive ? '' : 'off'}`}>
            <i className={`pi ${icon} device-icon`}></i>
          </div>
          <div>
            <p className="device-name">{name}</p>
            <p className="device-type">{typeLabel}</p>
          </div>
        </div>
        <div className="card-header-right">
          <span className={`status-pill ${isActive ? '' : 'off'}`}>{statusLabel}</span>
          {onDelete && (
            <Button
              icon="pi pi-trash"
              text
              rounded
              severity="danger"
              size="small"
              onClick={onDelete}
              aria-label="Delete device"
              className="delete-btn"
            />
          )}
        </div>
      </div>
      {children}
    </div>
  );
}
