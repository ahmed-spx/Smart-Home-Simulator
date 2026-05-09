import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { FanCard } from '../FanCard';

vi.mock('../../api/devices', () => ({
  toggleFan: vi.fn(async (id) => ({ id, type: 'Fan', state: 'On', speed: 'Off', name: 'Ceiling', location: 'Bedroom' })),
  setFanSpeed: vi.fn(async (id, speed) => ({ id, type: 'Fan', state: 'On', speed, name: 'Ceiling', location: 'Bedroom' })),
}));

import { toggleFan, setFanSpeed } from '../../api/devices';

const offFan = {
  id: 'fan-1', type: 'Fan',
  state: 'Off', speed: 'Off',
  name: 'Ceiling', location: 'Bedroom',
};
const onFan = { ...offFan, state: 'On', speed: 'Medium' };

describe('FanCard', () => {
  it('renders name, type label, and current state', () => {
    render(<FanCard device={offFan} onUpdate={() => {}} />);
    expect(screen.getByText('Ceiling')).toBeInTheDocument();
    expect(screen.getByText('Fan')).toBeInTheDocument();
    expect(screen.getByText('Off')).toBeInTheDocument();
  });

  it('disables speed selector while fan is Off', () => {
    const { container } = render(<FanCard device={offFan} onUpdate={() => {}} />);
    const selectButton = container.querySelector('.p-selectbutton');
    expect(selectButton.className).toContain('p-disabled');
  });

  it('enables speed selector while fan is On', () => {
    const { container } = render(<FanCard device={onFan} onUpdate={() => {}} />);
    const selectButton = container.querySelector('.p-selectbutton');
    expect(selectButton.className).not.toContain('p-disabled');
  });

  it('calls setFanSpeed when a speed button is clicked while On', async () => {
    const user = userEvent.setup();
    const onUpdate = vi.fn();
    render(<FanCard device={onFan} onUpdate={onUpdate} />);
    await user.click(screen.getByText('High'));
    expect(setFanSpeed).toHaveBeenCalledWith('fan-1', 'High');
    expect(onUpdate).toHaveBeenCalled();
  });

  it('calls toggleFan when the power switch is clicked', async () => {
    const user = userEvent.setup();
    const onUpdate = vi.fn();
    const { container } = render(<FanCard device={offFan} onUpdate={onUpdate} />);
    await user.click(container.querySelector('.p-inputswitch'));
    expect(toggleFan).toHaveBeenCalledWith('fan-1');
    expect(onUpdate).toHaveBeenCalled();
  });
});
