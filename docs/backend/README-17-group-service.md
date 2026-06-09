# README 17: Group Service - Salas y Comunidad

## PROPÓSITO

Este documento resume los endpoints reales implementados para gestión de grupos/salas.

Base path interno del servicio: `/groups`

Ruteo vía API Gateway: `/api/groups/**`

---

## 1. ENDPOINTS DE ROOMS

### GET /groups
Lista todas las salas.

### GET /groups/{id}
Obtiene una sala por ID.

### POST /groups
Crea una sala.

Header requerido:
- `X-User-Id`

### PUT /groups/{id}
Actualiza una sala.

Header requerido:
- `X-User-Id`

### DELETE /groups/{id}
Elimina una sala.

Header requerido:
- `X-User-Id`

### POST /groups/join/{inviteCode}
Permite unirse a una sala mediante código de invitación.

Header requerido:
- `X-User-Id`

---

## 2. ENDPOINTS DE MIEMBROS

### GET /groups/{roomId}/members
Lista miembros de la sala.

### DELETE /groups/{roomId}/members/{memberId}
Expulsa miembro de la sala.

Header requerido:
- `X-User-Id`

### PATCH /groups/{roomId}/members/{memberId}/role?role={role}
Actualiza rol de un miembro.

Header requerido:
- `X-User-Id`

---

## 3. ENDPOINTS DE INVITACIONES

### GET /groups/{roomId}/invites
Lista invitaciones de una sala.

### POST /groups/{roomId}/invites
Crea una invitación.

Header requerido:
- `X-User-Id`

### POST /groups/invites/{inviteId}/respond?response={response}
Responde una invitación.

Header requerido:
- `X-User-Id`

---

## 4. ENDPOINTS DE REPORTES

### GET /groups/{roomId}/reports
Lista reportes de una sala.

### POST /groups/{roomId}/reports
Registra un reporte.

Header requerido:
- `X-User-Id`

### PUT /groups/reports/{reportId}/resolve?resolution={resolution}&note={note}
Resuelve un reporte.

Header requerido:
- `X-User-Id`

---

## 5. ENDPOINTS DE BANEOS

### GET /groups/{roomId}/bans
Lista usuarios baneados de una sala.

### POST /groups/{roomId}/bans?userId={userId}&reason={reason}&permanent={bool}
Banea un usuario.

Header requerido:
- `X-User-Id`

### DELETE /groups/{roomId}/bans/{userId}
Desbanea un usuario.

Header requerido:
- `X-User-Id`

---

## 6. REFERENCIAS DE CÓDIGO

Controlador fuente:
- `Bckend/group-service/group-service/src/main/java/group_service/controller/RoomController.java`
