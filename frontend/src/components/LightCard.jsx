import { InputSwitch } from 'primereact/inputswitch';
import { Slider } from 'primereact/slider';
import { ColorPicker } from 'primereact/colorpicker';
import { CardShell } from './CardShell';
import { toggleLight, setLightBrightness, setLightColor } from '../api/devices';

export function LightCard({ device, onUpdate, onRemove }) {
  const isOn = device.state === 'On';
  const colorNoHash = device.color ? device.color.replace('#', '') : 'ffffff';

  return (
    <CardShell icon="pi-lightbulb" name={device.name} typeLabel="Light"
               isActive={isOn} statusLabel={device.state} onRemove={onRemove}>
      <div className="control-row">
        <span className="control-label">Power</span>
        <InputSwitch checked={isOn}
          onChange={async () => onUpdate(await toggleLight(device.id))} />
      </div>
      <div className="control-row">
        <span className="control-label">Brightness</span>
        <Slider value={device.brightness} disabled={!isOn} style={{ flex: 1 }}
          onChange={async (e) => onUpdate(await setLightBrightness(device.id, e.value))} />
        <span className="control-value">{device.brightness}%</span>
      </div>
      <div className="control-row">
        <span className="control-label">Color</span>
        <ColorPicker value={colorNoHash} disabled={!isOn}
          onChange={async (e) => onUpdate(await setLightColor(device.id, '#' + e.value))} />
      </div>
    </CardShell>
  );
}