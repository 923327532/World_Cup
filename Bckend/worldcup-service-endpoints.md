# World Cup Service - API Endpoints

## TournamentController

### GET /tournaments
Obtener todos los torneos

**Response 200:** `List<TournamentDTO>`

### GET /tournaments/current
Obtener torneo actual

**Response 200:** `TournamentDTO`

### GET /tournaments/{id}
Obtener torneo por ID

**Response 200:** `TournamentDTO`

---

## TeamController

### GET /teams?name={name}
Obtener todos los equipos (opcional: buscar por nombre)

**Response 200:** `List<TeamDTO>`

### GET /teams/{id}
Obtener equipo por ID

**Response 200:** `TeamDTO`

---

## PlayerController

### GET /players/team/{teamId}
Obtener jugadores por equipo

**Response 200:** `List<PlayerDTO>`

### GET /players/{id}
Obtener jugador por ID

**Response 200:** `PlayerDTO`

---

## MatchController

### GET /matches/date/{date}
Obtener partidos por fecha

**Response 200:** `List<MatchDTO>`

### GET /matches/{id}
Obtener partido por ID

**Response 200:** `MatchDTO`
