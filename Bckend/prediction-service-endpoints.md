# Prediction Service - API Endpoints

## PredictionController

### POST /predictions
Crear predicción

**Headers requeridos:** `X-User-Id`

**Request Body:**
| Campo | Tipo | Requerido |
|-------|------|-----------|
| `matchId` | Long | Sí |
| `predictionTypeId` | Long | Sí |
| `predictionValue` | String | Sí |

**Response 200:** `PredictionDTO`

### PUT /predictions/{id}
Actualizar predicción

**Headers requeridos:** `X-User-Id`

**Request Body:**
| Campo | Tipo | Requerido |
|-------|------|-----------|
| `predictedValue` | String | Sí |

**Response 200:** `PredictionDTO`

### GET /predictions/user/{userId}
Obtener predicciones de un usuario

**Response 200:** `List<PredictionDTO>`

### GET /predictions/match/{matchId}
Obtener predicciones de un partido

**Response 200:** `List<PredictionDTO>`

---

### PredictionDTO (Schema)
| Campo | Tipo |
|-------|------|
| `id` | Long |
| `userId` | Long |
| `matchId` | Long |
| `homeTeam` | String |
| `awayTeam` | String |
| `predictionTypeId` | Long |
| `predictionType` | String |
| `predictionValue` | String |
| `points` | Integer |
| `createdAt` | LocalDateTime |
| `updatedAt` | LocalDateTime |
| `isLocked` | Boolean |

---

## PredictionTypeController

### GET /prediction-types
Obtener todos los tipos de predicción

**Response 200:** `List<PredictionTypeDTO>`

---

### PredictionTypeDTO (Schema)
| Campo | Tipo |
|-------|------|
| `id` | Long |
| `code` | String |
| `name` | String |
| `points` | Integer |
