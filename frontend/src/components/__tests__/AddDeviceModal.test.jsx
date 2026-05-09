import { describe, it, expect, vi } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { AddDeviceModal } from '../AddDeviceModal';

describe('AddDeviceModal', () => {
  const defaultProps = {
    visible: true,
    onHide: vi.fn(),
    onSubmit: vi.fn(),
    existingLocations: ['Living Room', 'Bedroom', 'Kitchen'],
    existingThermostatLocations: [],
  };

  it('renders the modal header and form fields when visible', () => {
    render(<AddDeviceModal {...defaultProps} />);
    expect(screen.getByText('Add Device')).toBeInTheDocument();
    expect(screen.getByText('Type')).toBeInTheDocument();
    expect(screen.getByText('Name')).toBeInTheDocument();
    expect(screen.getByText('Location')).toBeInTheDocument();
  });

  it('disables Add button when name is empty', () => {
    render(<AddDeviceModal {...defaultProps} />);
    const addButton = screen.getByRole('button', { name: /Add/i });
    expect(addButton).toBeDisabled();
  });

  it('disables Add button when location is empty', async () => {
    const user = userEvent.setup();
    render(<AddDeviceModal {...defaultProps} />);
    
    const nameInput = screen.getByPlaceholderText('e.g. Hallway Light');
    await user.type(nameInput, 'Test Light');

    const addButton = screen.getByRole('button', { name: /Add/i });
    expect(addButton).toBeDisabled();
  });

  it('enables Add button when both name and location are filled', async () => {
    const user = userEvent.setup();
    render(<AddDeviceModal {...defaultProps} />);
    
    const nameInput = screen.getByPlaceholderText('e.g. Hallway Light');
    await user.type(nameInput, 'Test Light');

    const locationDropdown = screen.getByPlaceholderText('Select or type a location');
    await user.click(locationDropdown);
    await user.type(locationDropdown, 'Living Room');
    await user.keyboard('{Enter}');

    const addButton = screen.getByRole('button', { name: /Add/i });
    expect(addButton).not.toBeDisabled();
  });

  it('trims whitespace from name and location on submit', async () => {
    const user = userEvent.setup();
    const onSubmit = vi.fn().mockResolvedValue(undefined);
    render(<AddDeviceModal {...defaultProps} onSubmit={onSubmit} />);
    
    const nameInput = screen.getByPlaceholderText('e.g. Hallway Light');
    await user.type(nameInput, '  Test Light  ');

    const locationDropdown = screen.getByPlaceholderText('Select or type a location');
    await user.click(locationDropdown);
    await user.type(locationDropdown, '  Living Room  ');
    await user.keyboard('{Enter}');

    const addButton = screen.getByRole('button', { name: /Add/i });
    await user.click(addButton);

    await waitFor(() => {
      expect(onSubmit).toHaveBeenCalledWith({
        type: 'Light',
        name: 'Test Light',
        location: 'Living Room',
      });
    });
  });

  it('shows validation warning for duplicate thermostat location (case-insensitive)', async () => {
    const user = userEvent.setup();
    render(
      <AddDeviceModal
        {...defaultProps}
        existingThermostatLocations={['Living Room', 'Kitchen']}
      />
    );

    // Change type to Thermostat
    const typeDropdown = screen.getByRole('combobox', { hidden: false });
    await user.click(typeDropdown);
    const thermostatOption = screen.getByText('Thermostat');
    await user.click(thermostatOption);

    const nameInput = screen.getByPlaceholderText('e.g. Hallway Light');
    await user.type(nameInput, 'Main Thermostat');

    const locationDropdown = screen.getByPlaceholderText('Select or type a location');
    await user.click(locationDropdown);
    await user.type(locationDropdown, 'living room'); // lowercase to test case-insensitivity
    await user.keyboard('{Enter}');

    expect(screen.getByText(/living room already has a thermostat/i)).toBeInTheDocument();
  });

  it('disables Add button when thermostat would conflict', async () => {
    const user = userEvent.setup();
    render(
      <AddDeviceModal
        {...defaultProps}
        existingThermostatLocations={['Living Room']}
      />
    );

    // Change type to Thermostat
    const typeDropdown = screen.getByRole('combobox', { hidden: false });
    await user.click(typeDropdown);
    const thermostatOption = screen.getByText('Thermostat');
    await user.click(thermostatOption);

    const nameInput = screen.getByPlaceholderText('e.g. Hallway Light');
    await user.type(nameInput, 'Main Thermostat');

    const locationDropdown = screen.getByPlaceholderText('Select or type a location');
    await user.click(locationDropdown);
    await user.type(locationDropdown, 'Living Room');
    await user.keyboard('{Enter}');

    const addButton = screen.getByRole('button', { name: /Add/i });
    expect(addButton).toBeDisabled();
  });

  it('allows thermostat if location does not have existing thermostat', async () => {
    const user = userEvent.setup();
    render(
      <AddDeviceModal
        {...defaultProps}
        existingThermostatLocations={['Kitchen']}
      />
    );

    // Change type to Thermostat
    const typeDropdown = screen.getByRole('combobox', { hidden: false });
    await user.click(typeDropdown);
    const thermostatOption = screen.getByText('Thermostat');
    await user.click(thermostatOption);

    const nameInput = screen.getByPlaceholderText('e.g. Hallway Light');
    await user.type(nameInput, 'Main Thermostat');

    const locationDropdown = screen.getByPlaceholderText('Select or type a location');
    await user.click(locationDropdown);
    await user.type(locationDropdown, 'Living Room');
    await user.keyboard('{Enter}');

    const addButton = screen.getByRole('button', { name: /Add/i });
    expect(addButton).not.toBeDisabled();
  });

  it('resets form when modal opens', async () => {
    const user = userEvent.setup();
    const { rerender } = render(
      <AddDeviceModal {...defaultProps} visible={false} />
    );

    const nameInput = screen.getByPlaceholderText('e.g. Hallway Light');
    await user.type(nameInput, 'Old Light');

    rerender(
      <AddDeviceModal {...defaultProps} visible={true} />
    );

    const resetNameInput = screen.getByPlaceholderText('e.g. Hallway Light');
    expect(resetNameInput).toHaveValue('');
  });

  it('clears submit error when user changes any field', async () => {
    const user = userEvent.setup();
    const onSubmit = vi.fn().mockRejectedValue(new Error('Server error'));
    const { rerender } = render(
      <AddDeviceModal {...defaultProps} onSubmit={onSubmit} />
    );

    const nameInput = screen.getByPlaceholderText('e.g. Hallway Light');
    await user.type(nameInput, 'Test Light');

    const locationDropdown = screen.getByPlaceholderText('Select or type a location');
    await user.click(locationDropdown);
    await user.type(locationDropdown, 'Living Room');
    await user.keyboard('{Enter}');

    const addButton = screen.getByRole('button', { name: /Add/i });
    await user.click(addButton);

    await waitFor(() => {
      expect(screen.getByText('Server error')).toBeInTheDocument();
    });

    // Change name - error should clear
    await user.clear(nameInput);
    await user.type(nameInput, 'New Light');

    await waitFor(() => {
      expect(screen.queryByText('Server error')).not.toBeInTheDocument();
    });
  });

  it('displays server error when submit fails', async () => {
    const user = userEvent.setup();
    const onSubmit = vi.fn().mockRejectedValue(new Error('Location already exists'));
    render(
      <AddDeviceModal {...defaultProps} onSubmit={onSubmit} />
    );

    const nameInput = screen.getByPlaceholderText('e.g. Hallway Light');
    await user.type(nameInput, 'Test Light');

    const locationDropdown = screen.getByPlaceholderText('Select or type a location');
    await user.click(locationDropdown);
    await user.type(locationDropdown, 'Living Room');
    await user.keyboard('{Enter}');

    const addButton = screen.getByRole('button', { name: /Add/i });
    await user.click(addButton);

    await waitFor(() => {
      expect(screen.getByText('Location already exists')).toBeInTheDocument();
    });
  });

  it('disables Add button while submitting', async () => {
    const user = userEvent.setup();
    const onSubmit = vi.fn(() => new Promise(() => {})); // Never resolves
    render(
      <AddDeviceModal {...defaultProps} onSubmit={onSubmit} />
    );

    const nameInput = screen.getByPlaceholderText('e.g. Hallway Light');
    await user.type(nameInput, 'Test Light');

    const locationDropdown = screen.getByPlaceholderText('Select or type a location');
    await user.click(locationDropdown);
    await user.type(locationDropdown, 'Living Room');
    await user.keyboard('{Enter}');

    const addButton = screen.getByRole('button', { name: /Add/i });
    await user.click(addButton);

    await waitFor(() => {
      expect(addButton).toBeDisabled();
    });
  });

  it('calls onHide after successful submit', async () => {
    const user = userEvent.setup();
    const onHide = vi.fn();
    const onSubmit = vi.fn().mockResolvedValue(undefined);
    render(
      <AddDeviceModal {...defaultProps} onHide={onHide} onSubmit={onSubmit} />
    );

    const nameInput = screen.getByPlaceholderText('e.g. Hallway Light');
    await user.type(nameInput, 'Test Light');

    const locationDropdown = screen.getByPlaceholderText('Select or type a location');
    await user.click(locationDropdown);
    await user.type(locationDropdown, 'Living Room');
    await user.keyboard('{Enter}');

    const addButton = screen.getByRole('button', { name: /Add/i });
    await user.click(addButton);

    await waitFor(() => {
      expect(onHide).toHaveBeenCalled();
    });
  });

  it('calls onHide when Cancel button is clicked', async () => {
    const user = userEvent.setup();
    const onHide = vi.fn();
    render(
      <AddDeviceModal {...defaultProps} onHide={onHide} />
    );

    const cancelButton = screen.getByRole('button', { name: /Cancel/i });
    await user.click(cancelButton);

    expect(onHide).toHaveBeenCalled();
  });
});
