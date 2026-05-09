// Central place for every backend API call. Surfaces backend ProblemDetail
// errors as JS Errors so the UI can react.

const BASE_URL = '/api';

async function handle(res) {
  if (!res.ok) {
    let message = `${res.status} ${res.statusText}`;
    try {
      const problem = await res.json();
      if (problem.detail)      message = problem.detail;
      else if (problem.title)  message = problem.title;
    } catch { /* response wasn't JSON */ }
    const err = new Error(message);
    err.status = res.status;
    throw err;
  }
  return res.status === 204 ? null : res.json();
}

async function postJson(url, body) {
  const res = await fetch(url, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: body ? JSON.stringify(body) : undefined,
  });
  return handle(res);
}

// Device APIs
export async function getAllDevices() {
  return handle(await fetch(`${BASE_URL}/devices`));
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
  postJson(`${BASE_URL}/thermostats/${id}/temperature`, { temperature });

export const setThermostatMultiplier = (id, multiplier) =>
  postJson(`${BASE_URL}/thermostats/${id}/multiplier`, { multiplier });

export const setThermostatAmbient = (id, ambientTemperature) =>
  postJson(`${BASE_URL}/thermostats/${id}/ambient`, { ambientTemperature });

// DoorLock API
export const toggleDoorLock = (id) =>
  postJson(`${BASE_URL}/doorlocks/${id}/toggle`);

// Device management APIs
export const registerDevice = (data) =>
  postJson(`${BASE_URL}/devices`, data);

export const removeDevice = (id) =>
  fetch(`${BASE_URL}/devices/${id}`, { method: 'DELETE' }).then(handle);

export const resetAllDevices = () =>
  postJson(`${BASE_URL}/devices/reset`);