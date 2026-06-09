# Social Service - API Endpoints

## CommentController

### POST /comments
Crear comentario

**Headers requeridos:** `X-User-Id`

**Request Body:**
| Campo | Tipo |
|-------|------|
| `matchId` | Long |
| `content` | String |

**Response 200:** `CommentDTO`

### GET /comments/match/{matchId}
Obtener comentarios de un partido

**Response 200:** `List<CommentDTO>`

---

### CommentDTO (Schema)
| Campo | Tipo |
|-------|------|
| `id` | Long |
| `userId` | Long |
| `matchId` | Long |
| `content` | String |
| `createdAt` | LocalDateTime |

---

## ReactionController

### POST /reactions
Agregar reacción a un comentario

**Headers requeridos:** `X-User-Id`

**Request Body:**
| Campo | Tipo |
|-------|------|
| `commentId` | Long |
| `reaction` | String |

**Response 200:** OK

### DELETE /reactions/{commentId}
Eliminar reacción de un comentario

**Headers requeridos:** `X-User-Id`

**Response 200:** OK

---

### ReactionDTO (Schema)
| Campo | Tipo |
|-------|------|
| `id` | Long |
| `userId` | Long |
| `commentId` | Long |
| `reaction` | String |
