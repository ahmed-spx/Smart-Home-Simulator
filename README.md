# Smart Home Simulator

A small full-stack OO design exercise (SWE-4743) that simulates a connected
home: lights, fans, thermostats, and door locks — each implemented with the
**State** pattern on the backend and rendered as live, interactive cards on
the frontend. Device state is persisted to disk between runs and every state
change is recorded in an audit log.

---

## What does the application do?

- Models four device types (Light, Fan, Thermostat, Door Lock) as classes that
  delegate behaviour to their current **state** object.
- Exposes a Spring Boot REST API that lets clients query and command devices.
- Runs a background **simulation tick** for thermostats — ambient temperature
  drifts toward the target while the unit is heating/cooling, at a multiplier
  the user can adjust (1×, 2×, 5×, 10×).
- Presents a React SPA dashboard:
  - Devices grouped by room, sorted alphabetically inside each group.
  - Combinable filters: **On**, **Off**, **Location**, **Device Type**.
  - Add / delete devices with confirmation prompts.
  - Inline controls (toggle switches, sliders, color picker, mode buttons)
    that disable themselves when the device is off.
  - Simulation Config modal that shows ambient temperature per thermostat
    location and lets you change each thermostat's tick rate.
- Persists every device's full state to `data/state.json` and writes an
  append-only audit log to `data/audit.jsonl`.

---

## Architecture at a glance

```
┌────────────────────────┐        HTTP        ┌──────────────────────┐
│  React 19 + Vite SPA   │  ─── /api/* ──▶   │  Spring Boot 4.0.6   │
│  PrimeReact components │                    │  Java 21             │
│  served by nginx       │                    │  data/state.json     │
└────────────────────────┘                    └──────────────────────┘
        port 5173                                      port 8080
```

- **Backend:** Spring Boot 4.0.6, Java 21, Maven. File-based persistence via
  `DeviceStore` — no database required.
- **Frontend:** React 19, Vite 8, PrimeReact 10, primeicons.
- **In Docker:** nginx in the frontend container reverse-proxies `/api/*` to
  the backend container, so the browser only ever talks to one origin.

---

## Section 6 — Setup & Run

### 6.1 Prerequisites

The only thing you need is:

- **Docker Desktop** (or Docker Engine + Docker Compose v2) — verified working
  on Docker 24+ / Compose v2.20+.

You do **not** need Java, Maven, Node.js, or any IDE installed on the host to
run the app — every dependency builds and runs inside containers.

> No API keys, no JWT, no external services are required for the base
> application.

### 6.2 Run with Docker (clone-and-go)

```bash
git clone https://github.com/<your-username>/Smart-Home-Simulator.git
cd Smart-Home-Simulator
docker compose up --build
```

The first build takes 1–3 minutes (Maven dependency download + Vite build).
Subsequent runs are cached. When you see `Started DemoApplication` in the
logs, the app is ready.

To stop:

```bash
docker compose down              # stop containers, keep persisted state
docker compose down -v           # also remove the backend-data volume (full reset)
```

The application is **fully seeded on first run** — `DeviceStore.seed()` populates
the hub with sample devices (Living Room, Kitchen, Bedroom). No manual
migration or import step.

### 6.3 Where to access things

| What           | URL                                          |
| -------------- | -------------------------------------------- |
| Web UI         | <http://localhost:5173>                      |
| REST API       | <http://localhost:8080/api/devices>          |
| Swagger UI     | <http://localhost:8080/swagger-ui.html>      |
| OpenAPI spec   | <http://localhost:8080/v3/api-docs>          |
| Actuator health| <http://localhost:8080/actuator/health>      |

### 6.4 Running tests

**Backend (JUnit / Spring Boot Test)** — uses the Maven wrapper, no host
Maven needed:

```bash
# Linux / macOS
./mvnw -f backend/pom.xml test

# Windows PowerShell
.\mvnw.cmd -f backend/pom.xml test
```

**Frontend** — no unit-test framework is wired up yet; lint runs in its place:

```bash
cd frontend
npm install
npm run lint
```

### 6.5 Bruno API collection

A [Bruno](https://www.usebruno.com/) collection is checked in at
[`bruno/`](bruno/). Open Bruno, choose **Open Collection**, point it at the
`bruno/` folder, and select the **Local** environment (`baseUrl =
http://localhost:8080`). Folders cover every endpoint:

- `Devices/` — list, history, **create (POST)**, **delete (DELETE)**
- `Lights/`, `Fans/`, `Thermostats/`, `DoorLocks/` — toggles and per-device
  commands

The `Local` environment also defines `lightId`, `fanId`, `thermostatId`,
`lockId`, and `deviceId` so requests resolve out-of-the-box against the
seeded data.

### 6.6 Test credentials

Not applicable — JWT authentication is not implemented in this project. All
endpoints are open on `localhost`.

---

## 6.3 Local development (without Docker)

For hot-reload work you can run each side directly. CORS is configured to
allow the Vite dev origin and Vite is configured to proxy `/api/*` to
`localhost:8080`, so no env vars are needed.

### Backend

```bash
# Linux / macOS
./mvnw -f backend/pom.xml spring-boot:run

# Windows PowerShell
.\mvnw.cmd -f backend/pom.xml spring-boot:run
```

Backend listens on <http://localhost:8080>. State is persisted to
`backend/data/state.json`.

### Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend dev server runs on <http://localhost:5173> and proxies API calls to
the backend automatically.

### Useful npm scripts

| Command          | What it does                              |
| ---------------- | ----------------------------------------- |
| `npm run dev`    | Vite dev server (HMR) on port 5173        |
| `npm run build`  | Production build → `frontend/dist/`       |
| `npm run preview`| Serve the built bundle locally on 4173    |
| `npm run lint`   | Run ESLint over the frontend source       |

---

## Project layout

```
Smart-Home-Simulator/
├── backend/                      Spring Boot service
│   ├── src/main/java/smarthomesim/demo/
│   │   ├── device/               Base Device + DeviceController (CRUD)
│   │   ├── light/  fan/  thermostat/  doorlock/   Concrete devices + states
│   │   ├── audit/                AuditLog + AuditEntry
│   │   └── persistence/          DeviceStore + DeviceSnapshot (state.json)
│   ├── pom.xml
│   └── Dockerfile
├── frontend/                     React + Vite SPA
│   ├── src/
│   │   ├── api/devices.js        Single point of contact with the backend
│   │   ├── components/           Card components + modals
│   │   └── App.jsx               Dashboard, filters, modals
│   ├── vite.config.js            Dev proxy → localhost:8080
│   ├── nginx.conf                Reverse-proxy /api → backend (used in Docker)
│   └── Dockerfile
├── bruno/                        API collection (checked in)
├── docker-compose.yml            Builds + runs both services
└── README.md                     This file
```

---

## Troubleshooting

| Symptom                                          | Likely cause / fix                                                                          |
| ------------------------------------------------ | ------------------------------------------------------------------------------------------- |
| `Web server failed to start. Port 8080 was already in use` | Another Spring Boot process is still running. Kill it (`lsof -i :8080` / Task Manager) or change the host-side port mapping in `docker-compose.yml`. |
| Frontend loads but cards never appear            | Backend isn't ready yet — wait for `Started DemoApplication`, or check `docker compose logs backend`. |
| Want to reset all device state to seed defaults  | `docker compose down -v` (removes the `backend-data` volume), then `docker compose up`.     |
| Local dev: API calls 404 in the browser          | The Vite dev proxy only forwards `/api/*`. Make sure URLs in `frontend/src/api/devices.js` start with `/api`. |
