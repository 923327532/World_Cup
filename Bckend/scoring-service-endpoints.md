# Scoring Service - API Endpoints

## ScoreController

### GET /scoring/user/{userId}
Obtener puntaje de un usuario

**Response 200:** `UserScoreDTO`

### GET /scoring/user/{userId}/history
Obtener historial de puntajes de un usuario

**Response 200:** `List<ScoreHistoryDTO>`
