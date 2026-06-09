# Leaderboard Service - API Endpoints

## LeaderboardController

### GET /leaderboard/global?limit={limit}
Obtener ranking global

**Query Params:** `limit` (default: 100)

**Response 200:** `List<LeaderboardEntryDTO>`

### GET /leaderboard/campus/{campusId}?limit={limit}
Obtener ranking por campus

**Query Params:** `limit` (default: 50)

**Response 200:** `List<LeaderboardEntryDTO>`

### GET /leaderboard/career/{careerId}?limit={limit}
Obtener ranking por carrera

**Query Params:** `limit` (default: 50)

**Response 200:** `List<LeaderboardEntryDTO>`

### GET /leaderboard/department/{departmentId}?limit={limit}
Obtener ranking por departamento

**Query Params:** `limit` (default: 50)

**Response 200:** `List<LeaderboardEntryDTO>`
