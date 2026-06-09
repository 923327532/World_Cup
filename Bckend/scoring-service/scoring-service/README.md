# Scoring Service - API Endpoints

## Base URL
```
/scoring
```

## ScoreController

### Obtener puntaje de un usuario
```
GET /scoring/user/{userId}
```
- **Response:** `UserScoreDTO`

### Obtener historial de puntajes de un usuario
```
GET /scoring/user/{userId}/history
```
- **Response:** `List<ScoreHistoryDTO>`
