# Group Service - API Endpoints

## Base URL
```
/groups
```

## RoomController

### Rooms

#### Obtener todos los grupos
```
GET /groups
```
- **Response:** `List<RoomResponse>`

#### Obtener grupo por ID
```
GET /groups/{id}
```
- **Response:** `RoomResponse`

#### Crear grupo
```
POST /groups
```
- **Headers:** `X-User-Id`
- **Body:** `RoomRequest`
- **Response:** `RoomResponse` (201 Created)

#### Actualizar grupo
```
PUT /groups/{id}
```
- **Headers:** `X-User-Id`
- **Body:** `RoomRequest`
- **Response:** `RoomResponse`

#### Eliminar grupo
```
DELETE /groups/{id}
```
- **Headers:** `X-User-Id`
- **Response:** 204 No Content

#### Unirse a grupo por código
```
POST /groups/join/{inviteCode}
```
- **Headers:** `X-User-Id`
- **Response:** `RoomResponse`

### Members

#### Obtener miembros del grupo
```
GET /groups/{roomId}/members
```
- **Response:** `List<MemberResponse>`

#### Eliminar miembro del grupo
```
DELETE /groups/{roomId}/members/{memberId}
```
- **Headers:** `X-User-Id`
- **Response:** 204 No Content

#### Actualizar rol de miembro
```
PATCH /groups/{roomId}/members/{memberId}/role?role={role}
```
- **Headers:** `X-User-Id`
- **Response:** 204 No Content

### Invites

#### Obtener invitaciones del grupo
```
GET /groups/{roomId}/invites
```
- **Response:** `List<InviteResponse>`

#### Crear invitación
```
POST /groups/{roomId}/invites
```
- **Headers:** `X-User-Id`
- **Body:** `InviteRequest`
- **Response:** `InviteResponse` (201 Created)

#### Responder invitación
```
POST /groups/invites/{inviteId}/respond?response={response}
```
- **Headers:** `X-User-Id`
- **Response:** `InviteResponse`

### Reports

#### Obtener reportes del grupo
```
GET /groups/{roomId}/reports
```
- **Response:** `List<ReportResponse>`

#### Crear reporte
```
POST /groups/{roomId}/reports
```
- **Headers:** `X-User-Id`
- **Body:** `ReportRequest`
- **Response:** `ReportResponse` (201 Created)

#### Resolver reporte
```
PUT /groups/reports/{reportId}/resolve?resolution={resolution}&note={note}
```
- **Headers:** `X-User-Id`
- **Response:** `ReportResponse`

### Bans

#### Obtener baneos del grupo
```
GET /groups/{roomId}/bans
```
- **Response:** `List<BanResponse>`

#### Banear usuario
```
POST /groups/{roomId}/bans?userId={userId}&reason={reason}&permanent={permanent}
```
- **Headers:** `X-User-Id`
- **Response:** `BanResponse` (201 Created)

#### Desbanear usuario
```
DELETE /groups/{roomId}/bans/{userId}
```
- **Headers:** `X-User-Id`
- **Response:** 204 No Content
