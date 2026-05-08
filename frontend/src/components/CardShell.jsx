export function CardShell({ icon, name, typeLabel, isActive, statusLabel, children }) {
  return (
    <div className={`device-card ${isActive ? '' : 'off'}`}>
      <div className="device-card-header">
        <div className="device-card-title">
          <i className={`pi ${icon} device-icon`}></i>
          <div>
            <p className="device-name">{name}</p>
            <p className="device-type">{typeLabel}</p>
          </div>
        </div>
        <span className={`status-pill ${isActive ? '' : 'off'}`}>{statusLabel}</span>
      </div>
      {children}
    </div>
  );
}