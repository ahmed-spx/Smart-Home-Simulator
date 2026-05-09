import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { DoorLockCard } from '../DoorLockCard';

vi.mock('../../api/devices', () => ({
  toggleDoorLock: vi.fn(async (id) => ({ id, type: 'Lock', state: 'Locked', name: 'Front Door', location: 'Foyer' })),
}));

import { toggleDoorLock } from '../../api/devices';

const unlocked = {
  id: 'lock-1', type: 'Lock', state: 'Unlocked',
  name: 'Front Door', location: 'Foyer',
};
const locked = { ...unlocked, state: 'Locked' };

describe('DoorLockCard', () => {
  it('shows current state in the control row', () => {
    render(<DoorLockCard device={unlocked} onUpdate={() => {}} />);
    expect(screen.getByText('Unlocked')).toBeInTheDocument();
    expect(screen.getByText('Lock')).toBeInTheDocument(); // button label
  });

  it('shows "Unlock" label on the button when currently Locked', () => {
    render(<DoorLockCard device={locked} onUpdate={() => {}} />);
    expect(screen.getByText('Locked')).toBeInTheDocument();
    expect(screen.getByText('Unlock')).toBeInTheDocument();
  });

  it('never applies the .off greying — locks are always active', () => {
    const { container: u } = render(<DoorLockCard device={unlocked} onUpdate={() => {}} />);
    expect(u.querySelector('.device-card.off')).not.toBeInTheDocument();

    const { container: l } = render(<DoorLockCard device={locked} onUpdate={() => {}} />);
    expect(l.querySelector('.device-card.off')).not.toBeInTheDocument();
  });

  it('calls toggleDoorLock when the button is clicked', async () => {
    const user = userEvent.setup();
    const onUpdate = vi.fn();
    render(<DoorLockCard device={unlocked} onUpdate={onUpdate} />);
    await user.click(screen.getByText('Lock'));
    expect(toggleDoorLock).toHaveBeenCalledWith('lock-1');
    expect(onUpdate).toHaveBeenCalled();
  });
});
