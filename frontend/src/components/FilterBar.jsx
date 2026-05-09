import { SelectButton } from 'primereact/selectbutton';
import { Dropdown } from 'primereact/dropdown';

const statusOptions = [
  { label: 'All', value: 'all' },
  { label: 'On', value: 'on' },
  { label: 'Off', value: 'off' },
];

export function FilterBar({ filters, onChange, locations, types }) {
  const locationOptions = [
    { label: 'All Locations', value: 'all' },
    ...locations.map(l => ({ label: l, value: l })),
  ];
  const typeOptions = [
    { label: 'All Types', value: 'all' },
    ...types.map(t => ({ label: t, value: t })),
  ];

  return (
    <div className="filter-bar">
      <div className="filter-group">
        <span className="filter-label">STATUS</span>
        <SelectButton
          className="compact-select"
          value={filters.status}
          options={statusOptions}
          onChange={(e) => e.value && onChange({ ...filters, status: e.value })}
        />
      </div>
      <div className="filter-group">
        <span className="filter-label">LOCATION</span>
        <Dropdown
          value={filters.location}
          options={locationOptions}
          onChange={(e) => onChange({ ...filters, location: e.value })}
          className="filter-dropdown"
        />
      </div>
      <div className="filter-group">
        <span className="filter-label">TYPE</span>
        <Dropdown
          value={filters.type}
          options={typeOptions}
          onChange={(e) => onChange({ ...filters, type: e.value })}
          className="filter-dropdown"
        />
      </div>
    </div>
  );
}