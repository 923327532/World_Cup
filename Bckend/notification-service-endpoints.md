# Notification Service - API Endpoints

## NotificationController

### GET /notifications/user/{userId}
Obtener notificaciones de un usuario

**Response 200:** `List<NotificationDTO>`

### GET /notifications/user/{userId}/unread
Obtener notificaciones no leídas de un usuario

**Response 200:** `List<NotificationDTO>`

### PUT /notifications/{id}/read
Marcar notificación como leída

**Response 200:** OK

---

### NotificationDTO (Schema)
| Campo | Tipo |
|-------|------|
| `id` | Long |
| `userId` | Long |
| `title` | String |
| `message` | String |
| `isRead` | Boolean |
| `createdAt` | LocalDateTime |
