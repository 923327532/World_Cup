# Admin Service - API Endpoints

## AdminMatchController

### GET /matches
Obtener todos los partidos

**Response 200:** `List<ManualMatchResponse>`

### GET /matches/{id}
Obtener partido por ID

**Response 200:** `ManualMatchResponse`

### GET /matches/status/{status}
Obtener partidos por estado

**Response 200:** `List<ManualMatchResponse>`

### POST /matches
Crear partido

**Headers requeridos:** `X-User-Id`, `X-Admin-Email`

**Request Body:**
| Campo | Tipo | Requerido |
|-------|------|-----------|
| `homeTeam` | String | Sí |
| `awayTeam` | String | Sí |
| `startTime` | LocalDateTime | Sí |
| `venue` | String | No |
| `stage` | String | No |
| `groupName` | String | No |

**Response 201:** `ManualMatchResponse`

### PUT /matches/{id}
Actualizar partido

**Headers requeridos:** `X-User-Id`, `X-Admin-Email`

**Request Body:** (mismo schema que POST /matches)

**Response 200:** `ManualMatchResponse`

### PUT /matches/{id}/result
Actualizar resultado del partido

**Headers requeridos:** `X-User-Id`, `X-Admin-Email`

**Request Body:**
| Campo | Tipo | Requerido |
|-------|------|-----------|
| `homeScore` | Integer | Sí |
| `awayScore` | Integer | Sí |
| `winner` | String | No |

**Response 200:** `ManualMatchResponse`

### PATCH /matches/{id}/status?status={status}
Actualizar estado del partido

**Headers requeridos:** `X-User-Id`, `X-Admin-Email`

**Response 200:** `ManualMatchResponse`

### DELETE /matches/{id}
Eliminar partido

**Headers requeridos:** `X-User-Id`, `X-Admin-Email`

**Response 204:** No Content

---

### ManualMatchResponse (Schema)
| Campo | Tipo |
|-------|------|
| `id` | Long |
| `homeTeam` | String |
| `awayTeam` | String |
| `startTime` | LocalDateTime |
| `status` | String |
| `homeScore` | Integer |
| `awayScore` | Integer |
| `winner` | String |
| `sourceType` | String |
| `createdBy` | String |
| `updatedBy` | String |
| `createdAt` | LocalDateTime |
| `updatedAt` | LocalDateTime |
| `venue` | String |
| `stage` | String |
| `groupName` | String |

---

## AdminAuditController

### GET /audit
Obtener todos los logs de auditoría

**Response 200:** `List<AdminAuditLogResponse>`

### GET /audit/admin/{adminId}
Obtener logs por admin

**Response 200:** `List<AdminAuditLogResponse>`

### GET /audit/entity?entityType={type}&entityId={id}
Obtener logs por entidad

**Response 200:** `List<AdminAuditLogResponse>`

---

### AdminAuditLogResponse (Schema)
| Campo | Tipo |
|-------|------|
| `id` | Long |
| `adminId` | Long |
| `adminEmail` | String |
| `action` | String |
| `entityType` | String |
| `entityId` | Long |
| `details` | String |
| `createdAt` | LocalDateTime |
