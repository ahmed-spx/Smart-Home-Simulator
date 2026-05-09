import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { LightCard } from '../LightCard';

// Mock the API module so the component never touches fetch.
vi.mock('../../api/devices', () => ({
  toggleLight: vi.fn(async (id) => ({ id, type: 'Light', state: 'On', brightness: 100, color: '#FFFFFF', name: 'Lamp', location: 'Bedroom' })),
  setLightBrightness: vi.fn(async (id, brightness) => ({ id, type: 'Light', state: 'On', brightness, color: '#FFFFFF', name: 'Lamp', location: 'Bedroom' })),
  setLightColor: vi.fn(async (id, color) => ({ id, type: 'Light', state: 'On', brightness: 100, color, name: 'Lamp', location: 'Bedroom' })),
}));

import { toggleLight, setLightBrightness } from '../../api/devices';

const offDevice = {
  id: 'light-1', type: 'Light',
  state: 'Off', brightness: 100, color: '#FFFFFF',
  name: 'Lamp', location: 'Bedroom',
};
const onDevice = { ...offDevice, state: 'On' };

describe('LightCard', () => {
  it('renders name, type label, and current state', () => {
    render(<LightCard device={offDevice} onUpdate={() => {}} />);
    expect(screen.getByText('Lamp')).toBeInTheDocument();
    expect(screen.getByText('Light')).toBeInTheDocument();
    expect(screen.getByText('Off')).toBeInTheDocument();
  });

  it('renders the current brightness percentage', () => {
    render(<LightCard device={{ ...onDevice, brightness: 42 }} onUpdate={() => {}} />);
    expect(screen.getByText('42%')).toBeInTheDocument();
  });

  it('shows the .off class on the card when the light is off', () => {
    const { container } = render(<LightCard device={offDevice} onUpdate={() => {}} />);
    expect(container.querySelector('.device-card.off')).toBeInTheDocument();
  });

  it('omits the .off class when the light is on', () => {
    const { container } = render(<LightCard device={onDevice} onUpdate={() => {}} />);
    expect(container.querySelector('.device-card.off')).not.toBeInTheDocument();
  });

  it('disables brightness slider when the light is Off', () => {
    const { container } = render(<LightCard device={offDevice} onUpdate={() => {}} />);
    // PrimeReact Slider sets aria-disabled on the slider handle
    const slider = container.querySelector('.p-slider');
    expect(slider.className).toContain('p-disabled');
  });

  it('enables brightness slider when the light is On', () => {
    const { container } = render(<LightCard device={onDevice} onUpdate={() => {}} />);
    const slider = container.querySelector('.p-slider');
    expect(slider.className).not.toContain('p-disabled');
  });

  it('disables color picker when the light is Off', () => {
    const { container } = render(<LightCard device={offDevice} onUpdate={() => {}} />);
    const picker = container.querySelector('.p-colorpicker');
    expect(picker.className).toContain('p-disabled');
  });

  it('calls toggleLight + onUpdate when the power switch is clicked', async () => {
    const user = userEvent.setup();
    const onUpdate = vi.fn();
    const { container } = render(<LightCard device={offDevice} onUpdate={onUpdate} />);

    const switchEl = container.querySelector('.p-inputswitch');
    await user.click(switchEl);

    expect(toggleLight).toHaveBeenCalledWith('light-1');
    expect(onUpdate).toHaveBeenCalledTimes(1);
    expect(onUpdate.mock.calls[0][0].state).toBe('On');
  });

  it('does NOT call setLightBrightness when slider is disabled (Off)', async () => {
    const user = userEvent.setup();
    const { container } = render(<LightCard device={offDevice} onUpdate={() => {}} />);
    const handle = container.querySelector('.p-slider-handle');
    if (handle) await user.click(handle);
    expect(setLightBrightness).not.toHaveBeenCalled();
  });

  it('routes API errors to onError prop instead of throwing', async () => {
    toggleLight.mockRejectedValueOnce(new Error('boom'));
    const onError = vi.fn();
    const user = userEvent.setup();
    const { container } = render(
      <LightCard device={offDevice} onUpdate={() => {}} onError={onError} />
    );
    await user.click(container.querySelector('.p-inputswitch'));
    expect(onError).toHaveBeenCalledTimes(1);
    expect(onError.mock.calls[0][0].message).toBe('boom');
  });
});
