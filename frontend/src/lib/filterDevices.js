// Pure predicate used by the dashboard to filter devices.
// Extracted from App.jsx so it can be unit-tested in isolation.
//
// filters shape: { status: 'all'|'on'|'off', location: 'all'|<string>, type: 'all'|<string> }
//
// Special case for Locks (latch devices): they're considered always "on"
// from the simulator's perspective, so the "Off" status filter never matches them.

export function filterDevices(devices, filters) {
  return devices.filter((d) => {
    if (filters.location !== 'all' && d.location !== filters.location) return false;
    if (filters.type     !== 'all' && d.type     !== filters.type)     return false;

    if (filters.status === 'all') return true;

    if (d.type === 'Lock') {
      // Locks never match "off" filter and always match "on".
      return filters.status !== 'off';
    }

    const isOn = d.state !== 'Off';
    if (filters.status === 'on'  && !isOn) return false;
    if (filters.status === 'off' &&  isOn) return false;
    return true;
  });
}
