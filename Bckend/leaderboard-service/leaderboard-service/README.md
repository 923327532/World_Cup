# Leaderboard Service - API Endpoints

## Base URL
```
/leaderboard
```

## LeaderboardController

### Obtener ranking global
```
GET /leaderboard/global?limit={limit}
```
- **Query Params:** `limit` (default: 100)
- **Response:** `List<LeaderboardEntryDTO>`

### Obtener ranking por campus
```
GET /leaderboard/campus/{campusId}?limit={limit}
```
- **Query Params:** `limit` (default: 50)
- **Response:** `List<LeaderboardEntryDTO>`

### Obtener ranking por carrera
```
GET /leaderboard/career/{careerId}?limit={limit}
```
- **Query Params:** `limit` (default: 50)
- **Response:** `List<LeaderboardEntryDTO>`

### Obtener ranking por departamento
```
GET /leaderboard/department/{departmentId}?limit={limit}
```
- **Query Params:** `limit` (default: 50)
- **Response:** `List<LeaderboardEntryDTO>`
