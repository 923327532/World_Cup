# World Cup Service - API Endpoints

## Base URL
```
/tournaments
```

## TournamentController

### Obtener todos los torneos
```
GET /tournaments
```
- **Response:** `List<TournamentDTO>`

### Obtener torneo actual
```
GET /tournaments/current
```
- **Response:** `TournamentDTO`

### Obtener torneo por ID
```
GET /tournaments/{id}
```
- **Response:** `TournamentDTO`

---

## Base URL
```
/teams
```

## TeamController

### Obtener todos los equipos (opcional: buscar por nombre)
```
GET /teams?name={name}
```
- **Response:** `List<TeamDTO>`

### Obtener equipo por ID
```
GET /teams/{id}
```
- **Response:** `TeamDTO`

---

## Base URL
```
/players
```

## PlayerController

### Obtener jugadores por equipo
```
GET /players/team/{teamId}
```
- **Response:** `List<PlayerDTO>`

### Obtener jugador por ID
```
GET /players/{id}
```
- **Response:** `PlayerDTO`

---

## Base URL
```
/matches
```

## MatchController

### Obtener partidos por fecha
```
GET /matches/date/{date}
```
- **Response:** `List<MatchDTO>`

### Obtener partido por ID
```
GET /matches/{id}
```
- **Response:** `MatchDTO`
