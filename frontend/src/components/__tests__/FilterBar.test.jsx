import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { FilterBar } from '../FilterBar';

describe('FilterBar', () => {
  const defaultProps = {
    filters: { status: 'all', location: 'all', type: 'all' },
    onChange: vi.fn(),
    locations: ['Living Room', 'Bedroom', 'Kitchen'],
    types: ['Light', 'Fan', 'Thermostat', 'DoorLock'],
  };

  it('renders status, location, and type filter labels', () => {
    render(<FilterBar {...defaultProps} />);
    expect(screen.getByText('STATUS')).toBeInTheDocument();
    expect(screen.getByText('LOCATION')).toBeInTheDocument();
    expect(screen.getByText('TYPE')).toBeInTheDocument();
  });

  it('renders status options (All, On, Off)', () => {
    render(<FilterBar {...defaultProps} />);
    expect(screen.getByText('All')).toBeInTheDocument();
    expect(screen.getByText('On')).toBeInTheDocument();
    expect(screen.getByText('Off')).toBeInTheDocument();
  });

  it('renders location dropdown with all provided locations', () => {
    render(<FilterBar {...defaultProps} />);
    expect(screen.getByText('All Locations')).toBeInTheDocument();
    expect(screen.getByText('Living Room')).toBeInTheDocument();
    expect(screen.getByText('Bedroom')).toBeInTheDocument();
    expect(screen.getByText('Kitchen')).toBeInTheDocument();
  });

  it('renders type dropdown with all provided types', () => {
    render(<FilterBar {...defaultProps} />);
    expect(screen.getByText('All Types')).toBeInTheDocument();
    expect(screen.getByText('Light')).toBeInTheDocument();
    expect(screen.getByText('Fan')).toBeInTheDocument();
    expect(screen.getByText('Thermostat')).toBeInTheDocument();
    expect(screen.getByText('DoorLock')).toBeInTheDocument();
  });

  it('calls onChange with updated status when a status button is clicked', async () => {
    const user = userEvent.setup();
    const onChange = vi.fn();
    render(<FilterBar {...defaultProps} onChange={onChange} />);

    const offButton = screen.getAllByText('Off')[0]; // First "Off" is the status button
    await user.click(offButton);

    expect(onChange).toHaveBeenCalledWith({
      status: 'off',
      location: 'all',
      type: 'all',
    });
  });

  it('calls onChange with updated location when location dropdown changes', async () => {
    const user = userEvent.setup();
    const onChange = vi.fn();
    render(<FilterBar {...defaultProps} onChange={onChange} />);

    // Click on the location dropdown to open it
    const locationDropdown = screen.getAllByRole('combobox')[0]; // First dropdown is location
    await user.click(locationDropdown);

    // Click on "Living Room" option
    const livingRoomOption = screen.getAllByText('Living Room')[0];
    await user.click(livingRoomOption);

    expect(onChange).toHaveBeenCalledWith({
      status: 'all',
      location: 'Living Room',
      type: 'all',
    });
  });

  it('calls onChange with updated type when type dropdown changes', async () => {
    const user = userEvent.setup();
    const onChange = vi.fn();
    render(<FilterBar {...defaultProps} onChange={onChange} />);

    // Get all comboboxes and click the type dropdown (should be one of them)
    const dropdowns = screen.getAllByRole('combobox');
    const typeDropdown = dropdowns[1]; // Second dropdown is type
    await user.click(typeDropdown);

    const lightOption = screen.getAllByText('Light')[0];
    await user.click(lightOption);

    expect(onChange).toHaveBeenCalledWith({
      status: 'all',
      location: 'all',
      type: 'Light',
    });
  });

  it('reflects current filter values in the UI', () => {
    const currentFilters = { status: 'on', location: 'Bedroom', type: 'Light' };
    const { container } = render(
      <FilterBar {...defaultProps} filters={currentFilters} />
    );

    // Check that selected values are displayed
    expect(screen.getByText('On')).toBeInTheDocument();
    expect(screen.getByText('Bedroom')).toBeInTheDocument();
  });

  it('handles empty locations array gracefully', () => {
    render(
      <FilterBar {...defaultProps} locations={[]} />
    );
    expect(screen.getByText('All Locations')).toBeInTheDocument();
  });

  it('handles empty types array gracefully', () => {
    render(
      <FilterBar {...defaultProps} types={[]} />
    );
    expect(screen.getByText('All Types')).toBeInTheDocument();
  });
});
