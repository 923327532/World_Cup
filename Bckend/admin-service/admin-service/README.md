# Admin Service - API Endpoints

## Base URL
```
/matches
```

## AdminMatchController

### Obtener todos los partidos
```
GET /matches
```
- **Response:** `List<ManualMatchResponse>`

### Obtener partido por ID
```
GET /matches/{id}
```
- **Response:** `ManualMatchResponse`

### Obtener partidos por estado
```
GET /matches/status/{status}
```
- **Response:** `List<ManualMatchResponse>`

### Crear partido
```
POST /matches
```
- **Headers:** `X-User-Id`, `X-Admin-Email`
- **Body:** `ManualMatchRequest` (homeTeam, awayTeam, startTime, venue, stage, groupName)
- **Response:** `ManualMatchResponse` (201 Created)

### Actualizar partido
```
PUT /matches/{id}
```
- **Headers:** `X-User-Id`, `X-Admin-Email`
- **Body:** `ManualMatchRequest`
- **Response:** `ManualMatchResponse`

### Actualizar resultado del partido
```
PUT /matches/{id}/result
```
- **Headers:** `X-User-Id`, `X-Admin-Email`
- **Body:** `MatchResultUpdateRequest` (homeScore, awayScore, winner)
- **Response:** `ManualMatchResponse`

### Actualizar estado del partido
```
PATCH /matches/{id}/status?status={status}
```
- **Headers:** `X-User-Id`, `X-Admin-Email`
- **Response:** `ManualMatchResponse`

### Eliminar partido
```
DELETE /matches/{id}
```
- **Headers:** `X-User-Id`, `X-Admin-Email`
- **Response:** 204 No Content

---

## Base URL
```
/audit
```

## AdminAuditController

### Obtener todos los logs de auditoría
```
GET /audit
```
- **Response:** `List<AdminAuditLogResponse>`

### Obtener logs por admin
```
GET /audit/admin/{adminId}
```
- **Response:** `List<AdminAuditLogResponse>`

### Obtener logs por entidad
```
GET /audit/entity?entityType={entityType}&entityId={entityId}
```
- **Response:** `List<AdminAuditLogResponse>`
