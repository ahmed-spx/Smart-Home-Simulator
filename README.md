# Smart Home Simulator

## Introduction

Smart Home Simulator is a full-stack web application that simulates a connected smart home environment. It provides a realistic interface for controlling and monitoring various smart home devices in real time.

## What Does the Application Do?

The Smart Home Simulator enables you to:

- **Control Smart Devices**: Toggle and manage various smart home devices including lights, thermostats, door locks, and fans
- **Adjust Device Settings**: Fine-tune device parameters like brightness and color for lights, temperature and mode for thermostats, and speed for fans
- **Real-Time Updates**: Receive live updates of device state changes via Server-Sent Events (SSE)
- **Device Management**: Register, remove, and list devices in the system
- **Event Streaming**: Subscribe to device events and receive real-time notifications
- **Data Persistence**: All device states and audit logs are persisted to disk

The application consists of a **Spring Boot REST API** backend that manages device state and logic, and a **React/Vite frontend** that provides an interactive web UI for controlling devices.

## Demo Videos

- [App Demo](#) - [*Add video link here*](https://teams.microsoft.com/l/meetingrecap?driveId=b%21DdAUydCzskyV1Yz-cXdM8hMGMbzKFElClPTtTXFeERzfP0MdjVMmQYLnY0E6AQ53&driveItemId=01RZ63GYQ667CYIGH4HZF3VXPE2TOJQRVZ&sitePath=https%3A%2F%2Fkennesawedu-my.sharepoint.com%2F%3Av%3A%2Fg%2Fpersonal%2Faalsaad1_students_kennesaw_edu%2FIQAe98WEGPw-S7rd5NTcmEa5AavBrnxJJSNYpxksHeIruPc&fileUrl=https%3A%2F%2Fkennesawedu-my.sharepoint.com%2Fpersonal%2Faalsaad1_students_kennesaw_edu%2FDocuments%2FRecordings%2FCall+with+Ahmad+Ibrahim-20260508_233410-Meeting+Recording.mp4%3Fweb%3D1&threadId=19%3A1bb4d7c6-416b-498e-a719-f933ce67c965_31972d3d-c8ca-4b62-afd5-4bbaea67506f%40unq.gbl.spaces&callId=534aa289-c262-4192-b206-ae99daba9ff1&threadType=OneOnOneChat&meetingType=Unknown&subType=RecapSharingLink_RecapCore)
- [Architechture Walkthrough](#) - *Add video link here*

## Prerequisites

**Only Docker and Docker Compose are required.** No additional tools, dependencies, or API keys are needed.

Ensure you have:
- [Docker](https://www.docker.com/products/docker-desktop) (version 20.10 or later)
- [Docker Compose](https://docs.docker.com/compose/) (version 2.0 or later, typically included with Docker Desktop)

## Setup and Run Instructions

### Quick Start

Clone the repository and start the application with a single command:

```bash
docker compose up --build
```

This command will:
1. Build the Spring Boot backend (Java 21, Maven)
2. Build the React frontend (Node.js, Vite)
3. Start both services and connect them
4. Initialize the application with default devices

The first run may take 2-3 minutes as Docker builds the images.

### Stopping the Application

To stop the running containers:

```bash
docker compose down
```

To remove all containers and volumes (fresh restart):

```bash
docker compose down -v
```

## Accessing the Application

Once `docker compose up --build` completes, access the application at:

### Frontend UI
- **URL**: [http://localhost:5173](http://localhost:5173)
- **Description**: Interactive web interface for controlling and monitoring smart home devices

### Backend API
- **Base URL**: [http://localhost:8080/api](http://localhost:8080/api)
- **Description**: REST API endpoints for device management and control

### API Documentation (Swagger/OpenAPI)
- **URL**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **Description**: Interactive API documentation with the ability to test endpoints directly

## How to Run Tests

### Backend Tests

Run the Spring Boot test suite inside the backend container:

```bash
docker compose run --rm backend mvn test
```

This executes all JUnit tests in the `backend/src/test` directory using Maven.

### Frontend Tests

Run the React test suite:

```bash
docker compose run --rm frontend npm test
```

This executes all Vitest tests in the `frontend/src` directory.

To run tests in watch mode (re-run on file changes):

```bash
docker compose run --rm frontend npm run test:watch
```

## Using the Bruno API Collection

The repository includes a [Bruno](https://www.usebruno.com/) API collection for testing and exploring the backend API without needing external tools.

### Location
- Bruno collection: `./bruno` directory in the repository root

### How to Use

1. **Install Bruno**: Download and install Bruno from [https://www.usebruno.com/](https://www.usebruno.com/)

2. **Open the Collection**:
   - Launch Bruno
   - Click **File** → **Open Collection**
   - Navigate to the `./bruno` directory in the repository and open it

3. **Configure Environment**:
   - The collection includes an environment configuration file: `bruno/environments/Local.bru`
   - Set the API base URL to `http://localhost:8080` (default for local testing)

4. **Explore Endpoints**: The collection includes organized requests for:
   - **Devices**: List, register, remove, reset devices, view history
   - **Lights**: Toggle, set brightness, set color, get state
   - **Thermostats**: Toggle, set temperature, set mode, set ambient temperature multiplier, get state
   - **Fans**: Toggle, set speed, get state
   - **Door Locks**: Toggle, get state
   - **Events**: Subscribe to real-time device events

## Project Structure

```
Smart-Home-Simulator/
├── backend/              # Spring Boot REST API
│   ├── src/
│   │   ├── main/java/    # Java source code
│   │   └── test/java/    # Unit tests
│   ├── pom.xml          # Maven dependencies
│   └── Dockerfile       # Backend container configuration
├── frontend/            # React + Vite web application
│   ├── src/
│   │   ├── components/  # React components
│   │   ├── api/         # API client code
│   │   ├── __tests__/   # Component tests
│   │   └── lib/         # Utility functions
│   ├── package.json     # NPM dependencies
│   └── Dockerfile       # Frontend container configuration
├── bruno/               # Bruno API collection for testing
├── data/                # Persistent data directory (mounted volume)
│   ├── state.json       # Device state persistence
│   └── audit.jsonl      # Event audit log
└── docker-compose.yml   # Multi-container orchestration
```

## API Endpoints Overview

### Devices
- `GET /api/devices` - List all devices
- `POST /api/devices/register` - Register a new device
- `DELETE /api/devices/{id}` - Remove a device
- `POST /api/devices/reset` - Reset all devices to defaults
- `GET /api/devices/history` - Get device event history

### Lights
- `GET /api/lights/{id}` - Get light state
- `POST /api/lights/{id}/toggle` - Toggle light on/off
- `POST /api/lights/{id}/brightness` - Set brightness (0-100)
- `POST /api/lights/{id}/color` - Set color (hex code)

### Thermostats
- `GET /api/thermostats/{id}` - Get thermostat state
- `POST /api/thermostats/{id}/toggle` - Toggle on/off
- `POST /api/thermostats/{id}/temperature` - Set target temperature
- `POST /api/thermostats/{id}/mode` - Set mode (Heat/Cool/Auto)
- `POST /api/thermostats/{id}/ambient-temp-multiplier` - Adjust temperature simulation

### Fans
- `GET /api/fans/{id}` - Get fan state
- `POST /api/fans/{id}/toggle` - Toggle on/off
- `POST /api/fans/{id}/speed` - Set speed (0-100)

### Door Locks
- `GET /api/doorlocks/{id}` - Get lock state
- `POST /api/doorlocks/{id}/toggle` - Toggle locked/unlocked

### Events
- `GET /api/events/subscribe` - Subscribe to real-time device events (Server-Sent Events)

## Technology Stack

**Backend:**
- Java 21
- Spring Boot 4.0.6
- Maven (build tool)
- SpringDoc OpenAPI (Swagger documentation)

**Frontend:**
- React 19
- Vite (build tool)
- Vitest (testing framework)
- PrimeReact (UI components)
- ESLint (code quality)

**DevOps:**
- Docker & Docker Compose (containerization)
- Nginx (frontend web server)

## Architecture

The application follows a three-tier architecture:

1. **Frontend Tier**: React web UI served by Nginx, communicates with backend via REST API and SSE
2. **Backend Tier**: Spring Boot REST API handling business logic and device state management
3. **Data Tier**: Persistent JSON-based storage for device state and audit logs

Communication between frontend and backend occurs via:
- **REST API**: Standard HTTP requests for device operations
- **Server-Sent Events (SSE)**: Real-time push notifications for device state changes

## Troubleshooting

### Port Already in Use
If port 5173 (frontend) or 8080 (backend) is already in use:

```bash
# Find and stop the process using the port (Windows)
netstat -ano | findstr :5173
taskkill /PID <PID> /F
```

### Build Fails
- Ensure Docker and Docker Compose are correctly installed
- Try removing dangling images: `docker image prune -a`
- Rebuild from scratch: `docker compose down -v && docker compose up --build`

### Frontend Cannot Connect to Backend
- Verify backend is running: `curl http://localhost:8080/api/devices`
- Check container logs: `docker compose logs backend`

## Development Notes

- **Backend State**: Persisted in `./data/state.json` (mounted volume)
- **Audit Logs**: Available in `./data/audit.jsonl`
- **Frontend Proxy**: The Vite dev server is configured to proxy API calls to the backend at `http://localhost:8080`
- **Hot Module Replacement**: Frontend supports HMR during development
