# Notification Service - API Endpoints

## Base URL
```
/notifications
```

## NotificationController

### Obtener notificaciones de un usuario
```
GET /notifications/user/{userId}
```
- **Response:** `List<NotificationDTO>`

### Obtener notificaciones no leídas de un usuario
```
GET /notifications/user/{userId}/unread
```
- **Response:** `List<NotificationDTO>`

### Marcar notificación como leída
```
PUT /notifications/{id}/read
```
- **Response:** 200 OK
