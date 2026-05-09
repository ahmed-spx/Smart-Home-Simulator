import { Button } from 'primereact/button';
import { Clock } from './Clock';

export function Header({ multiplier, onAddDevice }) {
  return (
    <header className="app-header">
      <div className="app-header-left">
        <i className="pi pi-home app-logo" />
        <h1>Smart Home Simulator</h1>
      </div>
      <div className="app-header-right">
        <Button
          label="Add Device"
          icon="pi pi-plus"
          onClick={onAddDevice}
          className="add-device-btn"
        />
        <Clock multiplier={multiplier} />
      </div>
    </header>
  );
}