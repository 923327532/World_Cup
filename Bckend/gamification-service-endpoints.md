# Gamification Service - API Endpoints

## BadgeController

### GET /badges
Obtener todos los badges

**Response 200:** `List<BadgeDTO>`

### GET /badges/user/{userId}
Obtener badges de un usuario

**Response 200:** `List<UserBadgeDTO>`

---

## RewardController

### GET /rewards
Obtener todos los rewards

**Response 200:** `List<RewardDTO>`
