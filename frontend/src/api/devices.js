// Central place for every backend API call. The rest of the React app
// imports these functions and never touches `fetch` directly.

const BASE_URL = 'http://localhost:8080/api';

// Small helper: POST a JSON body and parse the JSON response.
async function postJson(url, body) {
  const res = await fetch(url, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: body ? JSON.stringify(body) : undefined,
  });
  return res.json();
}

//Device APIs
export async function getAllDevices() {
  const res = await fetch(`${BASE_URL}/devices`);
  return res.json();
}

// Light APIs
export const toggleLight = (id) =>
  postJson(`${BASE_URL}/lights/${id}/toggle`);

export const setLightBrightness = (id, brightness) =>
  postJson(`${BASE_URL}/lights/${id}/brightness`, { brightness });

export const setLightColor = (id, color) =>
  postJson(`${BASE_URL}/lights/${id}/color`, { color });

// Fan APIs
export const toggleFan = (id) =>
  postJson(`${BASE_URL}/fans/${id}/toggle`);

export const setFanSpeed = (id, speed) =>
  postJson(`${BASE_URL}/fans/${id}/speed`, { speed });

// Thermostat APIs
export const toggleThermostat = (id) =>
  postJson(`${BASE_URL}/thermostats/${id}/toggle`);

export const setThermostatMode = (id, mode) =>
  postJson(`${BASE_URL}/thermostats/${id}/mode`, { mode });

export const setThermostatTemperature = (id, temperature) =>
  postJson(`${BASE_URL}/thermostats/${id}/temperature`, { temperature })

export const setThermostatMultiplier = (id, multiplier) =>
  postJson(`${BASE_URL}/thermostats/${id}/multiplier`, { multiplier });

// DoorLock API
export const toggleDoorLock = (id) =>
  postJson(`${BASE_URL}/doorlocks/${id}/toggle`);