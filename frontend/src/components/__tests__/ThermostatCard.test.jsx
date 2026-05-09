import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { ThermostatCard } from '../ThermostatCard';

vi.mock('../../api/devices', () => ({
  toggleThermostat: vi.fn(async (id) => ({ id, type: 'Thermostat', state: 'Heating', mode: 'Auto', desiredTemperature: 70, ambientTemperature: 65, name: 'T', location: 'Living Room' })),
  setThermostatMode: vi.fn(async (id, mode) => ({ id, type: 'Thermostat', state: 'Heating', mode, desiredTemperature: 70, ambientTemperature: 65, name: 'T', location: 'Living Room' })),
  setThermostatTemperature: vi.fn(async (id, temperature) => ({ id, type: 'Thermostat', state: 'Heating', mode: 'Auto', desiredTemperature: temperature, ambientTemperature: 65, name: 'T', location: 'Living Room' })),
}));

import { toggleThermostat, setThermostatMode } from '../../api/devices';

const offThermostat = {
  id: 'thermo-1', type: 'Thermostat',
  state: 'Off', mode: 'Auto',
  desiredTemperature: 70, ambientTemperature: 65,
  name: 'T', location: 'Living Room',
};
const heatingThermostat = { ...offThermostat, state: 'Heating' };

describe('ThermostatCard', () => {
  it('renders desired temperature prominently and ambient secondarily', () => {
    render(<ThermostatCard device={offThermostat} onUpdate={() => {}} />);
    expect(screen.getByText('70')).toBeInTheDocument();
    expect(screen.getByText('°F target')).toBeInTheDocument();
    expect(screen.getByText('65°F')).toBeInTheDocument();
    expect(screen.getByText('Ambient')).toBeInTheDocument();
  });

  it('shows .off class when state is Off', () => {
    const { container } = render(<ThermostatCard device={offThermostat} onUpdate={() => {}} />);
    expect(container.querySelector('.device-card.off')).toBeInTheDocument();
  });

  it('does NOT show .off class when state is Heating', () => {
    const { container } = render(<ThermostatCard device={heatingThermostat} onUpdate={() => {}} />);
    expect(container.querySelector('.device-card.off')).not.toBeInTheDocument();
  });

  it('disables mode selector when Off', () => {
    const { container } = render(<ThermostatCard device={offThermostat} onUpdate={() => {}} />);
    const selectButton = container.querySelector('.p-selectbutton');
    expect(selectButton.className).toContain('p-disabled');
  });

  it('disables target slider when Off', () => {
    const { container } = render(<ThermostatCard device={offThermostat} onUpdate={() => {}} />);
    const slider = container.querySelector('.p-slider');
    expect(slider.className).toContain('p-disabled');
  });

  it('enables target slider with min=60 max=80 when On', () => {
    const { container } = render(<ThermostatCard device={heatingThermostat} onUpdate={() => {}} />);
    const handle = container.querySelector('.p-slider-handle');
    // PrimeReact stores aria-valuemin/valuemax on the handle
    expect(handle.getAttribute('aria-valuemin')).toBe('60');
    expect(handle.getAttribute('aria-valuemax')).toBe('80');
  });

  it('calls toggleThermostat + onUpdate when power switch toggled', async () => {
    const user = userEvent.setup();
    const onUpdate = vi.fn();
    const { container } = render(<ThermostatCard device={offThermostat} onUpdate={onUpdate} />);
    await user.click(container.querySelector('.p-inputswitch'));
    expect(toggleThermostat).toHaveBeenCalledWith('thermo-1');
    expect(onUpdate).toHaveBeenCalled();
  });

  it('calls setThermostatMode when a mode button is clicked while On', async () => {
    const user = userEvent.setup();
    render(<ThermostatCard device={heatingThermostat} onUpdate={() => {}} />);
    await user.click(screen.getByText('Cool'));
    expect(setThermostatMode).toHaveBeenCalledWith('thermo-1', 'Cooling');
  });
});
