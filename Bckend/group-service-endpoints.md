# Group Service - API Endpoints

## RoomController

### Rooms

#### GET /groups
Obtener todos los grupos

**Response 200:** `List<RoomResponse>`

#### GET /groups/{id}
Obtener grupo por ID

**Response 200:** `RoomResponse`

#### POST /groups
Crear grupo

**Headers requeridos:** `X-User-Id`

**Request Body:**
| Campo | Tipo | Requerido |
|-------|------|-----------|
| `name` | String | Sí |
| `description` | String | No |
| `maxMembers` | Integer | No |

**Response 201:** `RoomResponse`

#### PUT /groups/{id}
Actualizar grupo

**Headers requeridos:** `X-User-Id`

**Request Body:** (mismo schema que POST /groups)

**Response 200:** `RoomResponse`

#### DELETE /groups/{id}
Eliminar grupo

**Headers requeridos:** `X-User-Id`

**Response 204:** No Content

#### POST /groups/join/{inviteCode}
Unirse a grupo por código

**Headers requeridos:** `X-User-Id`

**Response 200:** `RoomResponse`

---

### Members

#### GET /groups/{roomId}/members
Obtener miembros del grupo

**Response 200:** `List<MemberResponse>`

#### DELETE /groups/{roomId}/members/{memberId}
Eliminar miembro del grupo

**Headers requeridos:** `X-User-Id`

**Response 204:** No Content

#### PATCH /groups/{roomId}/members/{memberId}/role?role={role}
Actualizar rol de miembro

**Headers requeridos:** `X-User-Id`

**Response 204:** No Content

---

### Invites

#### GET /groups/{roomId}/invites
Obtener invitaciones del grupo

**Response 200:** `List<InviteResponse>`

#### POST /groups/{roomId}/invites
Crear invitación

**Headers requeridos:** `X-User-Id`

**Request Body:**
| Campo | Tipo | Requerido |
|-------|------|-----------|
| `invitedUserId` | Long | No |
| `invitedEmail` | String | No |

**Response 201:** `InviteResponse`

#### POST /groups/invites/{inviteId}/respond?response={response}
Responder invitación

**Headers requeridos:** `X-User-Id`

**Response 200:** `InviteResponse`

---

### Reports

#### GET /groups/{roomId}/reports
Obtener reportes del grupo

**Response 200:** `List<ReportResponse>`

#### POST /groups/{roomId}/reports
Crear reporte

**Headers requeridos:** `X-User-Id`

**Request Body:**
| Campo | Tipo | Requerido |
|-------|------|-----------|
| `reportedUserId` | Long | Sí |
| `reason` | String | Sí |
| `description` | String | No |

**Response 201:** `ReportResponse`

#### PUT /groups/reports/{reportId}/resolve?resolution={resolution}&note={note}
Resolver reporte

**Headers requeridos:** `X-User-Id`

**Response 200:** `ReportResponse`

---

### Bans

#### GET /groups/{roomId}/bans
Obtener baneos del grupo

**Response 200:** `List<BanResponse>`

#### POST /groups/{roomId}/bans?userId={userId}&reason={reason}&permanent={permanent}
Banear usuario

**Headers requeridos:** `X-User-Id`

**Response 201:** `BanResponse`

#### DELETE /groups/{roomId}/bans/{userId}
Desbanear usuario

**Headers requeridos:** `X-User-Id`

**Response 204:** No Content

---

## DTOs

### RoomResponse
| Campo | Tipo |
|-------|------|
| `id` | Long |
| `name` | String |
| `description` | String |
| `inviteCode` | String |
| `createdBy` | Long |
| `maxMembers` | Integer |
| `status` | String |
| `createdAt` | LocalDateTime |
| `updatedAt` | LocalDateTime |

### MemberResponse
| Campo | Tipo |
|-------|------|
| `id` | Long |
| `roomId` | Long |
| `userId` | Long |
| `role` | String |
| `joinedAt` | LocalDateTime |

### InviteResponse
| Campo | Tipo |
|-------|------|
| `id` | Long |
| `roomId` | Long |
| `invitedUserId` | Long |
| `invitedEmail` | String |
| `invitedBy` | Long |
| `token` | String |
| `status` | String |
| `expiresAt` | LocalDateTime |
| `createdAt` | LocalDateTime |
| `respondedAt` | LocalDateTime |

### ReportResponse
| Campo | Tipo |
|-------|------|
| `id` | Long |
| `roomId` | Long |
| `reportedBy` | Long |
| `reportedUserId` | Long |
| `reason` | String |
| `description` | String |
| `status` | String |
| `resolvedBy` | Long |
| `resolutionNote` | String |
| `createdAt` | LocalDateTime |
| `resolvedAt` | LocalDateTime |

### BanResponse
| Campo | Tipo |
|-------|------|
| `id` | Long |
| `roomId` | Long |
| `userId` | Long |
| `bannedBy` | Long |
| `reason` | String |
| `expiresAt` | LocalDateTime |
| `isPermanent` | Boolean |
| `createdAt` | LocalDateTime |
