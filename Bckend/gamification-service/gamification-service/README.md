# Gamification Service - API Endpoints

## Base URL
```
/badges
```

## BadgeController

### Obtener todos los badges
```
GET /badges
```
- **Response:** `List<BadgeDTO>`

### Obtener badges de un usuario
```
GET /badges/user/{userId}
```
- **Response:** `List<UserBadgeDTO>`

---

## Base URL
```
/rewards
```

## RewardController

### Obtener todos los rewards
```
GET /rewards
```
- **Response:** `List<RewardDTO>`
