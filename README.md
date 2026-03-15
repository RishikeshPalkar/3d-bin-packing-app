# 3D Bin Packing App

A full-stack 3D bin packing optimization system built as a final year project. The system uses a **Bottom-Left-Fill (BLF) heuristic** engine (with optional PyTorch RL model support) to compute optimal 3D placement of items inside a container, exposed via a REST API and visualized in a React Native mobile app.

---

## Architecture

```
React Native (Expo)
       │
       │  REST / JSON
       ▼
Spring Boot 3.3  ──────────────────────────────────────────┐
  - REST API gateway                                        │
  - Input validation                                        │  HTTP / JSON
  - Job persistence (PostgreSQL)                            │
  - WebClient proxy to ML service                          ▼
                                              FastAPI + Python
                                                - BLF heuristic engine
                                                - Optional PyTorch RL model
                                                - POST /predict
```

The mobile app **only talks to Spring Boot**. The Python service is internal and never exposed publicly.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Mobile | React Native · Expo SDK 55 · TypeScript |
| Backend API | Spring Boot 3.3 · Java 21 · Maven |
| ML Service | FastAPI 0.111 · Python 3.11 · Pydantic v2 |
| Database | PostgreSQL 16 |
| Containerization | Docker · Docker Compose |
| 3D Visualization | Three.js · @react-three/fiber |

---

## Project Structure

```
3dbinPacking/
├── docker-compose.yml
├── README.md
│
├── backend/
│   └── binpacking/
│       ├── Dockerfile
│       ├── pom.xml
│       └── src/
│           └── main/java/com/example/binpacking/
│               ├── BinpackingApplication.java
│               ├── controller/
│               │   └── PackingController.java
│               ├── service/
│               │   └── PackingService.java
│               ├── model/
│               │   ├── PackingJob.java
│               │   ├── PackingItem.java
│               │   ├── PlacementResult.java
│               │   └── JobStatus.java
│               ├── repository/
│               │   ├── PackingRepository.java
│               │   ├── PackingItemRepository.java
│               │   └── PlacementResultRepository.java
│               ├── dto/
│               │   ├── PackingRequestDTO.java
│               │   ├── PackingResponseDTO.java
│               │   ├── ItemDTO.java
│               │   └── PlacementDTO.java
│               └── exception/
│                   ├── JobNotFoundException.java
│                   └── GlobalExceptionHandler.java
│
├── ml-service/
│   ├── Dockerfile
│   ├── requirements.txt
│   ├── main.py
│   ├── packing.py
│   └── schemas.py
│
└── Frontend/
    └── mobile/                  ← React Native (Expo)
        ├── app.json
        ├── app/
        │   ├── _layout.tsx
        │   ├── index.tsx
        │   ├── result.tsx
        │   └── history.tsx
        └── src/
            ├── config.ts
            ├── api/
            ├── hooks/
            ├── types/
            └── components/
```

---

## Getting Started

### Prerequisites

- Docker Desktop
- Java 21 (for local IntelliJ development)
- Python 3.11 (for local ML service development)
- Node.js 18+ (for React Native)
- Expo Go app on your phone

---

### Run with Docker Compose (Recommended)

This starts all three services — PostgreSQL, FastAPI ML service, and Spring Boot — with a single command.

```bash
# From project root
docker compose up --build
```

Services will be available at:

| Service | URL |
|---|---|
| Spring Boot API | http://localhost:8123 |
| FastAPI ML service | http://localhost:8000 |
| PostgreSQL | localhost:5432 |

To stop:
```bash
docker compose down
```

To stop and remove all data:
```bash
docker compose down -v
```

---

### Run Locally (Without Docker)

#### 1. Start PostgreSQL

```bash
docker compose up postgres -d
```

#### 2. Start the FastAPI ML Service

```bash
cd ml-service
python -m venv venv
venv\Scripts\activate        # Windows
# source venv/bin/activate   # Mac/Linux
pip install -r requirements.txt
uvicorn main:app --reload --port 8000
```

#### 3. Start the Spring Boot Backend

Open `backend/binpacking` in IntelliJ and run `BinpackingApplication`, or:

```bash
cd backend/binpacking
mvn spring-boot:run
```

#### 4. Start the React Native App

```bash
cd Frontend/mobile
npx expo start
```

Scan the QR code with Expo Go. Make sure your phone and PC are on the same WiFi network, and update `src/config.ts` with your local IP address.

---

## API Reference

Base URL: `http://localhost:8123`

### POST /api/v1/pack

Submit a packing job. Returns placement coordinates for each item.

**Request:**
```json
{
  "containerL": 10,
  "containerW": 10,
  "containerH": 10,
  "items": [
    { "name": "Box A", "length": 3, "width": 3, "height": 3 },
    { "name": "Box B", "length": 4, "width": 4, "height": 4 }
  ]
}
```

**Response:**
```json
{
  "jobId": "5bb80ba7-f0ab-4f50-b04d-0ac71e93acfe",
  "status": "DONE",
  "createdAt": "2026-03-15T11:58:32.946046",
  "containerL": 10.0,
  "containerW": 10.0,
  "containerH": 10.0,
  "utilization": 9.1,
  "placements": [
    {
      "itemId": "13b735ef-b470-47ab-9de7-b1902ef43e36",
      "itemName": "Box B",
      "x": 0.0, "y": 0.0, "z": 0.0,
      "placed": true,
      "rotation": "LWH"
    }
  ]
}
```

### GET /api/v1/jobs

Returns all packing jobs sorted by creation date.

### GET /api/v1/jobs/{id}

Returns a single job with full placement details.

### DELETE /api/v1/jobs/{id}

Deletes a job and all its associated placements.

### GET /api/v1/health

Health check. Returns `200 OK`.

---

## ML Service API

Base URL: `http://localhost:8000` (internal — called by Spring Boot only)

### GET /health

```json
{ "status": "ok", "model_loaded": false, "engine": "BLF heuristic" }
```

### POST /predict

Called internally by Spring Boot. Accepts container dimensions and item list, returns placement coordinates computed by the BLF engine.

---

## Database Schema

```sql
packing_job       -- job metadata, container dims, status, utilization %
packing_item      -- individual items per job (l, w, h, name)
placement_result  -- x, y, z coordinates + rotation per item per job
```

Inspect data directly:

```bash
docker exec -it binpacking-db psql -U postgres -d binpacking
```

```sql
SELECT
    j.id AS job_id, j.status,
    ROUND(j.utilization::numeric, 2) AS utilization_pct,
    i.name AS item_name, i.length, i.width, i.height,
    pr.x, pr.y, pr.z, pr.placed, pr.rotation
FROM packing_job j
LEFT JOIN packing_item i      ON i.job_id = j.id
LEFT JOIN placement_result pr ON pr.item_id = i.id AND pr.job_id = j.id
ORDER BY j.created_at DESC, i.name;
```

---

## Packing Algorithm

The BLF (Bottom-Left-Fill) heuristic engine in `ml-service/packing.py`:

1. Sorts items by volume descending (largest first)
2. Generates 6 rotation candidates per item (LWH, LHW, WLH, WHL, HLW, HWL)
3. Generates candidate positions from corners of already-placed boxes
4. Scores positions by gravity preference: lowest Y first, then X, then Z
5. Uses AABB collision detection to reject overlapping placements
6. Falls back gracefully — items that don't fit are marked `placed: false`

To add a trained PyTorch model, implement `inference.py` following the interface in `packing.py` and update `main.py` to load it at startup with BLF as fallback.

---

## Environment Variables

Spring Boot reads these from the environment (Docker) or falls back to `application.properties` defaults (local):

| Variable | Default | Description |
|---|---|---|
| `SERVER_PORT` | `8123` | Spring Boot port |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/binpacking` | PostgreSQL URL |
| `SPRING_DATASOURCE_USERNAME` | `postgres` | DB username |
| `SPRING_DATASOURCE_PASSWORD` | *(set in docker-compose)* | DB password |
| `ML_SERVICE_URL` | `http://localhost:8000` | FastAPI service URL |

---

## Development Roadmap

| Phase | Description | Status |
|---|---|---|
| 1 | Project setup, monorepo, Docker baseline | ✅ Done |
| 2 | Spring Boot REST API + PostgreSQL | ✅ Done |
| 3 | FastAPI ML service + BLF engine | ✅ Done |
| 4 | React Native mobile app | 🔄 In progress |
| 5 | Security, testing, UI polish | ⬜ Pending |
| 6 | Cloud deployment (Render / Railway) | ⬜ Pending |

---

## Known Issues / Risk Register

| Severity | Issue | Mitigation |
|---|---|---|
| HIGH | PyTorch model architecture mismatch on load | Match `state_dict().keys()` exactly from training script |
| HIGH | BLF O(n³) slowdown with 30+ items | Cap candidate positions, skip rotations for large sets |
| MED | Spring Boot WebClient timeout on slow inference | Timeout set to 60s; BLF fallback for large item sets |
| LOW | CORS misconfiguration in production | Lock `allowedOrigins` to Spring Boot hostname in prod |

---




