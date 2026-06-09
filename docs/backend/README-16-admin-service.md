# README 16: Admin Service - Operaciones Administrativas

## PROPÓSITO

Este documento resume los endpoints reales implementados para operaciones administrativas del backend.

Base path interno del servicio: `/matches` y `/audit`

Ruteo vía API Gateway: `/api/admin/**`

---

## 1. ENDPOINTS DE PARTIDOS ADMINISTRADOS

### GET /matches
Obtiene todos los partidos manuales.

### GET /matches/{id}
Obtiene un partido por ID.

### GET /matches/status/{status}
Obtiene partidos filtrados por estado.

### POST /matches
Crea un partido manual.

Headers requeridos:
- `X-User-Id`
- `X-Admin-Email`

Body esperado:
- `homeTeam` (string, requerido)
- `awayTeam` (string, requerido)
- `startTime` (LocalDateTime, requerido)
- `venue` (string, opcional)
- `stage` (string, opcional)
- `groupName` (string, opcional)

### PUT /matches/{id}
Actualiza un partido manual.

Headers requeridos:
- `X-User-Id`
- `X-Admin-Email`

### PUT /matches/{id}/result
Actualiza resultado de un partido.

Headers requeridos:
- `X-User-Id`
- `X-Admin-Email`

Body esperado:
- `homeScore` (integer, requerido)
- `awayScore` (integer, requerido)
- `winner` (string, opcional)

### PATCH /matches/{id}/status?status={status}
Actualiza el estado del partido.

Headers requeridos:
- `X-User-Id`
- `X-Admin-Email`

### DELETE /matches/{id}
Elimina un partido manual.

Headers requeridos:
- `X-User-Id`
- `X-Admin-Email`

---

## 2. ENDPOINTS DE AUDITORÍA ADMIN

### GET /audit
Obtiene todos los logs de auditoría.

### GET /audit/admin/{adminId}
Obtiene logs por administrador.

### GET /audit/entity?entityType={type}&entityId={id}
Obtiene logs por tipo e ID de entidad.

---

## 3. REFERENCIAS DE CÓDIGO

Controladores fuente:
- `Bckend/admin-service/admin-service/src/main/java/admin_service/controller/AdminMatchController.java`
- `Bckend/admin-service/admin-service/src/main/java/admin_service/controller/AdminAuditController.java`

Documento de apoyo:
- `Bckend/admin-service-endpoints.md`
