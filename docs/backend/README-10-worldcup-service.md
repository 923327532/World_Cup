# README 10: World Cup Service - Información Deportiva y API Externa

## PROMPT PARA EL EQUIPO DE DESARROLLO

Como desarrollador backend, tu tarea es implementar el World Cup Service responsable de gestionar toda la información deportiva del mundial y consumir la API externa de fútbol.

---

## CONFIGURACIÓN SPRING INITIALIZR

### Project
- **Type**: Maven
- **Language**: Java
- **Spring Boot**: 3.5.x

### Project Metadata
- **Group**: pe.tecsup.worldcup
- **Artifact**: worldcup-service
- **Name**: worldcup-service
- **Package name**: pe.tecsup.worldcup.worldcup
- **Packaging**: Jar
- **Java**: 17

### Dependencies
- **Spring Web**
- **Spring Data JPA**
- **PostgreSQL**
- **OpenFeign**
- **Scheduling**
- **Validation**
- **Eureka Discovery Client**
- **Lombok**

### Generar en Spring Initializr
```
https://start.spring.io/#!type=maven-project&language=java&platformVersion=3.5.x&packaging=jar&jvmVersion=17&groupId=pe.tecsup.worldcup&artifactId=worldcup-service&name=worldcup-service&packageName=pe.tecsup.worldcup.worldcup&dependencies=web,data-jpa,postgresql,openfeign,scheduling,validation,eureka-client,lombok
```

---

## 1. ESTRUCTURA DEL PROYECTO

```
worldcup-service
│
├── controller
│   ├── MatchController
│   ├── TeamController
│   ├── TournamentController
│   └── PlayerController
│
├── service
│   ├── MatchService
│   ├── TeamService
│   ├── TournamentService
│   ├── ApiFootballService
│   └── MatchSyncScheduler
│
├── repository
│   ├── MatchRepository
│   ├── TeamRepository
│   ├── TournamentRepository
│   ├── StageRepository
│   ├── GroupRepository
│   ├── PlayerRepository
│   └── MatchEventRepository
│
├── entity
│   ├── Tournament
│   ├── Stage
│   ├── Group
│   ├── Team
│   ├── Player
│   ├── Match
│   └── MatchEvent
│
├── dto
│   ├── MatchDTO
│   ├── TeamDTO
│   ├── TournamentDTO
│   └── ApiMatchResponse
│
├── client
│   └── ApiFootballClient
│
├── resources
│   ├── application.yml
│   └── application-dev.yml
│
└── WorldCupApplication.java
```

---

## 2. DEPENDENCIAS (pom.xml)

```xml
<dependencies>
    <!-- Spring Boot Starter Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- Spring Boot Starter Data JPA -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <!-- PostgreSQL -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
    </dependency>

    <!-- Eureka Client -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>

    <!-- Config Client -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-config</artifactId>
    </dependency>

    <!-- Redis -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>

    <!-- RabbitMQ -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-amqp</artifactId>
    </dependency>

    <!-- WebClient for API calls -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webflux</artifactId>
    </dependency>

    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
</dependencies>
```

---

## 3. ENTIDADES

### entity/Tournament.java
```java
package com.tecsup.worldcup.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "tournaments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tournament {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    private Integer year;
    
    private LocalDate startDate;
    
    private LocalDate endDate;
}
```

### entity/Stage.java
```java
package com.tecsup.worldcup.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "stages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;
    
    private String name;
}
```

### entity/Group.java
```java
package com.tecsup.worldcup.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "groups")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Group {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;
    
    @Column(length = 5)
    private String name;
}
```

### entity/Team.java
```java
package com.tecsup.worldcup.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "teams")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Team {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "api_team_id")
    private Long apiTeamId;
    
    private String name;
    
    @Column(length = 10)
    private String code;
    
    @Column(length = 500)
    private String flagUrl;
}
```

### entity/Match.java
```java
package com.tecsup.worldcup.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "matches")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Match {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "api_match_id")
    private Long apiMatchId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stage_id")
    private Stage stage;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_team_id")
    private Team homeTeam;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "away_team_id")
    private Team awayTeam;
    
    @Column(name = "kickoff_time")
    private LocalDateTime kickoffTime;
    
    @Column(name = "home_score")
    private Integer homeScore;
    
    @Column(name = "away_score")
    private Integer awayScore;
    
    private String status;
}
```

### entity/MatchEvent.java
```java
package com.tecsup.worldcup.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "match_events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchEvent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id")
    private Match match;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id")
    private Player player;
    
    @Column(length = 50)
    private String eventType;
    
    private Integer minute;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
```

### entity/Player.java
```java
package com.tecsup.worldcup.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "players")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "api_player_id")
    private Long apiPlayerId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;
    
    @Column(length = 200)
    private String fullName;
}
```

---

## 4. API-SPORTS - DOCUMENTACIÓN COMPLETA

### 4.1. Información General del Mundial 2026

El FIFA World Cup 2026 será organizado por tres países: **Canadá, México y Estados Unidos**. Se llevará a cabo del **11 de junio al 19 de julio**, con **48 naciones** representadas y **104 partidos** en total.

#### Formato del Torneo
- **12 grupos de 4 equipos** (48 equipos en total, a diferencia de 32 en ediciones anteriores)
- Las reglas de clasificación: los primeros dos equipos de cada grupo avanzan, junto con los ocho mejores terceros lugares
- Esto nos da 32 clasificados que se enfrentan en la ronda de 32 antes de pasar a los octavos de final

#### Distribución de los 104 Partidos
- **11-27 de junio**: Fase de grupos con 72 partidos
- **28 de junio - 3 de julio**: Ronda de 32
- **4-7 de julio**: Octavos de final
- **9-11 de julio**: Cuartos de final
- **14-15 de julio**: Semifinales
- **18-19 de julio**: Partido por el tercer lugar y final

#### Identificadores Clave
- **league=1** y **season=2026** son los dos identificadores clave que aparecen en casi todas las llamadas a la API

---

### 4.2. Endpoints Principales de API-SPORTS

#### Base URL
```
https://v3.football.api-sports.io
```

#### Headers Requeridos
```
x-apisports-key: YOUR_API_KEY_HERE
```

---

### 4.3. Endpoints Disponibles

#### 4.3.1. Verificar Cobertura de Datos
```http
GET https://v3.football.api-sports.io/leagues?id=1&season=2026
```

**Response Coverage:**
```json
{
  "coverage": {
    "fixtures": {
      "events": true,
      "lineups": true,
      "statistics_fixtures": true,
      "statistics_players": true
    },
    "standings": true,
    "players": true,
    "top_scorers": true,
    "top_assists": true,
    "top_cards": true,
    "injuries": true,
    "predictions": true,
    "odds": true
  }
}
```

---

#### 4.3.2. Obtener Calendario Completo
```http
GET https://v3.football.api-sports.io/fixtures?league=1&season=2026
```

**Response:**
```json
{
  "get": "fixtures",
  "parameters": {
    "league": "1",
    "season": "2026"
  },
  "results": 104,
  "response": [
    {
      "fixture": {
        "id": 123456,
        "date": "2026-06-11T18:00:00+00:00",
        "venue": {
          "id": 173,
          "name": "Estadio Azteca",
          "city": "Mexico City"
        },
        "status": {
          "long": "Not Started",
          "short": "NS"
        }
      },
      "league": {
        "id": 1,
        "name": "World Cup",
        "round": "Group Stage - 1"
      },
      "teams": {
        "home": {
          "id": 10,
          "name": "England",
          "logo": "https://media.api-sports.io/football/teams/10.png"
        },
        "away": {
          "id": 3,
          "name": "Croatia",
          "logo": "https://media.api-sports.io/football/teams/3.png"
        }
      }
    }
  ]
}
```

---

#### 4.3.3. Obtener Equipos
```http
GET https://v3.football.api-sports.io/teams?league=1&season=2026
```

**Response:**
```json
{
  "get": "teams",
  "parameters": {
    "league": "1",
    "season": "2026"
  },
  "results": 48,
  "response": [
    {
      "team": {
        "id": 1,
        "name": "Belgium",
        "code": "BEL",
        "country": "Belgium",
        "founded": 1895,
        "national": true,
        "logo": "https://media.api-sports.io/football/teams/1.png"
      },
      "venue": {
        "id": 173,
        "name": "Stade Roi Baudouin",
        "address": "Avenue de Marathon 135/2, Laken",
        "city": "Brussel",
        "capacity": 50093,
        "surface": "grass",
        "image": "https://media.api-sports.io/football/venues/173.png"
      }
    }
  ]
}
```

---

#### 4.3.4. Obtener Nombres de Rondas
```http
GET https://v3.football.api-sports.io/fixtures/rounds?league=1&season=2026
```

**Response:**
```json
{
  "get": "fixtures/rounds",
  "parameters": {
    "league": "1",
    "season": "2026"
  },
  "results": 3,
  "response": [
    "Group Stage - 1",
    "Group Stage - 2",
    "Group Stage - 3"
  ]
}
```

**Parámetro opcional:** `current=true` para obtener solo la ronda activa.

---

#### 4.3.5. Partidos en Vivo
```http
GET https://v3.football.api-sports.io/fixtures?live=all
```

**Para partidos específicos del Mundial:**
```http
GET https://v3.football.api-sports.io/fixtures?league=1&season=2026&status=1H-HT-2H-ET-P-BT-LIVE
```

**Nota:** Los datos devueltos por `/fixtures` y `/fixtures/events` se actualizan cada **15 segundos**.

---

#### 4.3.6. Detalles de Partido
```http
GET https://v3.football.api-sports.io/fixtures?id=FIXTURE_ID
```

**Para múltiples partidos (hasta 20 IDs):**
```http
GET https://v3.football.api-sports.io/fixtures?ids=ID1-ID2-ID3
```

La respuesta incluye 4 objetos embebidos: `/events`, `/lineups`, `/statistics` y `/players`.

---

#### 4.3.7. Tabla de Posiciones
```http
GET https://v3.football.api-sports.io/standings?league=1&season=2026
```

**Response:**
```json
{
  "response": [
    {
      "league": {
        "id": 1,
        "name": "World Cup",
        "country": "World",
        "logo": "https://media.api-sports.io/football/leagues/1.png",
        "season": 2026,
        "standings": [
          [
            {
              "rank": 1,
              "team": {
                "id": 770,
                "name": "Czech Republic",
                "logo": "https://media.api-sports.io/football/teams/770.png"
              },
              "points": 0,
              "goalsDiff": 0,
              "group": "Group A",
              "form": null,
              "status": "same",
              "all": {
                "played": 0,
                "win": 0,
                "draw": 0,
                "lose": 0,
                "goals": {
                  "for": 0,
                  "against": 0
                }
              }
            }
          ]
        ]
      }
    }
  ]
}
```

Esta llamada devuelve las **12 tablas de grupos**. Cada fila contiene partidos jugados, victorias, empates, derrotas, goles a favor, goles en contra, diferencia de goles, puntos y forma reciente.

---

#### 4.3.8. Jugadores
```http
GET https://v3.football.api-sports.io/players?league=1&season=2026&page=1
```

Lista completa de jugadores, incluyendo posición, nacionalidad, edad, altura, peso y foto.

---

#### 4.3.9. Entrenadores
```http
GET https://v3.football.api-sports.io/coachs?team=TEAM_ID
```

Devuelve la nacionalidad del entrenador principal, edad, historial de carrera y equipo actual.

---

#### 4.3.10. Lesiones y Suspensiones
```http
GET https://v3.football.api-sports.io/injuries?league=1&season=2026
```

---

#### 4.3.11. Estadísticas de Jugadores por Partido
```http
GET https://v3.football.api-sports.io/fixtures/players?fixture=FIXTURE_ID
```

Devuelve estadísticas de rendimiento junto con una calificación entre 0-10 para cada jugador que participó en el partido.

---

#### 4.3.12. Tarjetas Amarillas
```http
GET https://v3.football.api-sports.io/players/topyellowcards?league=1&season=2026
```

---

#### 4.3.13. Predicciones de Partido
```http
GET https://v3.football.api-sports.io/predictions?fixture=FIXTURE_ID
```

Analiza forma, registros head-to-head e datos históricos. Devuelve el ganador predicho, marcador estimado y probabilidades de local, empate y visitante.

---

#### 4.3.14. Cuotas Pre-Partido
```http
GET https://v3.football.api-sports.io/odds?fixture=FIXTURE_ID
```

**Nota:** Solo se pueden recuperar datos de cuotas de los últimos 7 días.

---

#### 4.3.15. Cuotas en Vivo
```http
GET https://v3.football.api-sports.io/odds/live?fixture=FIXTURE_ID
```

---

#### 4.3.16. Historial Head-to-Head
```http
GET https://v3.football.api-sports.io/fixtures/headtohead?h2h=TEAM_A-TEAM_B
```

Ejemplo para "Inglaterra VS Croacia":
```http
GET https://v3.football.api-sports.io/fixtures/headtohead?h2h=10-3
```

---

### 4.4. Ejemplos de Código

#### 4.4.1. Obtener Calendario Completo del Mundial (Node.js + Axios)
```javascript
const axios = require('axios');

const API_KEY = 'YOUR_API_KEY_HERE';
const BASE_URL = 'https://v3.football.api-sports.io';

async function getWorldCupSchedule() {
  try {
    const response = await axios.get(`${BASE_URL}/fixtures`, {
      params: {
        league: 1,
        season: 2026
      },
      headers: {
        'x-apisports-key': API_KEY
      }
    });

    const fixtures = response.data.response;
    console.log(`Total fixtures: ${fixtures.length}`);

    fixtures.forEach(fixture => {
      const date = new Date(fixture.fixture.date).toLocaleString('en-US', {
        weekday: 'short',
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
        timeZoneName: 'short'
      });

      console.log(`[${fixture.league.round}] ${date}`);
      console.log(`  ${fixture.teams.home.name} vs ${fixture.teams.away.name}`);
      console.log(`  Venue: ${fixture.fixture.venue.name}, ${fixture.fixture.venue.city}`);
      console.log('---');
    });
  } catch (error) {
    console.error('Error fetching schedule:', error.message);
  }
}

getWorldCupSchedule();
```

---

#### 4.4.2. Obtener Datos en Vivo (Node.js + Axios)
```javascript
const axios = require('axios');

const API_KEY = 'YOUR_API_KEY_HERE';
const BASE_URL = 'https://v3.football.api-sports.io';

async function getLiveMatches() {
  try {
    const response = await axios.get(`${BASE_URL}/fixtures`, {
      params: {
        league: 1,
        season: 2026,
        status: '1H-HT-2H-ET-P-BT-LIVE'
      },
      headers: {
        'x-apisports-key': API_KEY
      }
    });

    const liveFixtures = response.data.response;

    if (liveFixtures.length === 0) {
      console.log('No World Cup matches currently live.');
      return;
    }

    console.log(`${liveFixtures.length} match(es) live right now:`);

    // Process in batches of 20
    for (let i = 0; i < liveFixtures.length; i += 20) {
      const batch = liveFixtures.slice(i, i + 20);
      const fixtureIds = batch.map(f => f.fixture.id).join('-');

      const detailsResponse = await axios.get(`${BASE_URL}/fixtures`, {
        params: {
          ids: fixtureIds
        },
        headers: {
          'x-apisports-key': API_KEY
        }
      });

      const detailedFixtures = detailsResponse.data.response;

      for (const fixture of detailedFixtures) {
        const { id, status } = fixture.fixture;
        const { home, away } = fixture.teams;
        const { home: homeGoals, away: awayGoals } = fixture.goals;

        console.log(`[${status.elapsed}'] ${home.name}  ${homeGoals} - ${awayGoals}  ${away.name}`);
        console.log(`  Status: ${status.long}`);

        if (fixture.events && fixture.events.length > 0) {
          const goals = fixture.events.filter(e => e.type === 'Goal');

          if (goals.length > 0) {
            console.log('  Goals:');
            goals.forEach(goal => {
              console.log(`    ${goal.time.elapsed}' - ${goal.player.name} (${goal.team.name})`);
            });
          }
        }

        console.log('---');
      }
    }
  } catch (error) {
    console.error('Error fetching live data:', error.message);
  }
}

async function poll() {
  await getLiveMatches();
  setTimeout(poll, 15000); // Poll every 15 seconds
}

poll();
```

---

### 4.5. Configuración de application.yml

```yaml
api:
  football:
    api-key: ${API_FOOTBALL_KEY:your-api-key-here}
    base-url: https://v3.football.api-sports.io
    league-id: 1
    season: 2026
```

---

### 4.6. Rate Limiting y Caching

#### Rate Limiting
- La API tiene límites de requests por día y por minuto
- Headers de respuesta:
  - `x-ratelimit-requests-limit`: Número de requests por día según tu suscripción
  - `x-ratelimit-requests-remaining`: Requests restantes por día
  - `X-RateLimit-Limit`: Máximo de llamadas API por minuto
  - `X-RateLimit-Remaining`: Llamadas restantes antes del límite por minuto

#### Caching
- Implementar caché para reducir el número de requests
- Para partidos en vivo: caché de 15 segundos
- Para partidos programados: caché de 1 hora
- Para standings: caché de 5 minutos

---

## 5. API FOOTBALL CLIENT

### client/ApiFootballClient.java
```java
package com.tecsup.worldcup.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class ApiFootballClient {
    
    private final WebClient webClient;
    
    @Value("${api.football.api-key}")
    private String apiKey;
    
    @Value("${api.football.base-url}")
    private String baseUrl;
    
    public ApiFootballClient() {
        this.webClient = WebClient.builder().build();
    }
    
    public Mono<String> getMatches(Long leagueId, Integer season) {
        return webClient.get()
            .uri(baseUrl + "/fixtures", Map.of(
                "league", leagueId,
                "season", season
            ))
            .header("x-rapidapi-key", apiKey)
            .header("x-rapidapi-host", "api-football-v1.p.rapidapi.com")
            .retrieve()
            .bodyToMono(String.class);
    }
    
    public Mono<String> getTeams(Long leagueId, Integer season) {
        return webClient.get()
            .uri(baseUrl + "/teams", Map.of(
                "league", leagueId,
                "season", season
            ))
            .header("x-rapidapi-key", apiKey)
            .header("x-rapidapi-host", "api-football-v1.p.rapidapi.com")
            .retrieve()
            .bodyToMono(String.class);
    }
    
    public Mono<String> getMatchStatistics(Long matchId) {
        return webClient.get()
            .uri(baseUrl + "/fixtures/statistics", Map.of("fixture", matchId))
            .header("x-rapidapi-key", apiKey)
            .header("x-rapidapi-host", "api-football-v1.p.rapidapi.com")
            .retrieve()
            .bodyToMono(String.class);
    }
    
    public Mono<String> getMatchEvents(Long matchId) {
        return webClient.get()
            .uri(baseUrl + "/fixtures/events", Map.of("fixture", matchId))
            .header("x-rapidapi-key", apiKey)
            .header("x-rapidapi-host", "api-football-v1.p.rapidapi.com")
            .retrieve()
            .bodyToMono(String.class);
    }
}
```

---

## 5. SERVICES

### service/MatchSyncScheduler.java
```java
package com.tecsup.worldcup.service;

import com.tecsup.worldcup.client.ApiFootballClient;
import com.tecsup.worldcup.repository.MatchRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchSyncScheduler {
    
    private final ApiFootballClient apiFootballClient;
    private final MatchRepository matchRepository;
    private final ObjectMapper objectMapper;
    
    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void syncMatches() {
        log.info("Starting match synchronization...");
        
        try {
            apiFootballClient.getMatches(1L, 2026)
                .subscribe(response -> {
                    JsonNode root = objectMapper.readTree(response);
                    JsonNode matches = root.get("response");
                    
                    if (matches != null) {
                        matches.forEach(matchNode -> {
                            // Process each match
                            Long apiMatchId = matchNode.get("fixture").get("id").asLong();
                            // Update or create match in database
                            log.info("Processing match: {}", apiMatchId);
                        });
                    }
                });
            
            log.info("Match synchronization completed");
        } catch (Exception e) {
            log.error("Error during match synchronization", e);
        }
    }
}
```

### service/MatchService.java
```java
package com.tecsup.worldcup.service;

import com.tecsup.worldcup.dto.MatchDTO;
import com.tecsup.worldcup.entity.Match;
import com.tecsup.worldcup.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MatchService {
    
    private final MatchRepository matchRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    
    public List<MatchDTO> getMatchesByDate(LocalDate date) {
        String cacheKey = "matches:" + date;
        
        @SuppressWarnings("unchecked")
        List<MatchDTO> cached = (List<MatchDTO>) redisTemplate.opsForValue().get(cacheKey);
        
        if (cached != null) {
            return cached;
        }
        
        List<Match> matches = matchRepository.findByKickoffTimeBetween(
            date.atStartOfDay(),
            date.plusDays(1).atStartOfDay()
        );
        
        List<MatchDTO> result = matches.stream()
            .map(this::toDTO)
            .toList();
        
        redisTemplate.opsForValue().set(cacheKey, result, 1, TimeUnit.HOURS);
        
        return result;
    }
    
    public MatchDTO getMatchById(Long id) {
        Match match = matchRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Match not found"));
        return toDTO(match);
    }
    
    private MatchDTO toDTO(Match match) {
        return new MatchDTO(
            match.getId(),
            match.getHomeTeam().getName(),
            match.getAwayTeam().getName(),
            match.getKickoffTime(),
            match.getHomeScore(),
            match.getAwayScore(),
            match.getStatus()
        );
    }
}
```

---

## 6. CONTROLLERS

### controller/MatchController.java
```java
package com.tecsup.worldcup.controller;

import com.tecsup.worldcup.dto.MatchDTO;
import com.tecsup.worldcup.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/matches")
@RequiredArgsConstructor
public class MatchController {
    
    private final MatchService matchService;
    
    @GetMapping("/date/{date}")
    public ResponseEntity<List<MatchDTO>> getMatchesByDate(
        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(matchService.getMatchesByDate(date));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<MatchDTO> getMatchById(@PathVariable Long id) {
        return ResponseEntity.ok(matchService.getMatchById(id));
    }
}
```

---

## 7. REPOSITORIES

### repository/MatchRepository.java
```java
package com.tecsup.worldcup.repository;

import com.tecsup.worldcup.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByKickoffTimeBetween(LocalDateTime start, LocalDateTime end);
    List<Match> findByStatus(String status);
}
```

---

## 8. MAIN APPLICATION

### WorldCupApplication.java
```java
package com.tecsup.worldcup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
public class WorldCupApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorldCupApplication.class, args);
    }
}
```

---

## 9. CONFIGURACIÓN DE RABBITMQ

### config/RabbitMQConfig.java
```java
package com.tecsup.worldcup.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    
    @Bean
    public Queue matchGoalQueue() {
        return QueueBuilder.durable("match.goal").build();
    }
    
    @Bean
    public Queue matchFinishedQueue() {
        return QueueBuilder.durable("match.finished").build();
    }
    
    @Bean
    public TopicExchange matchExchange() {
        return new TopicExchange("match.exchange");
    }
    
    @Bean
    public Binding matchGoalBinding() {
        return BindingBuilder.bind(matchGoalQueue())
            .to(matchExchange())
            .with("match.goal");
    }
    
    @Bean
    public Jackson2JsonMessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }
    
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter());
        return template;
    }
}
```

---

## 10. TESTING

### Obtener partidos por fecha
```bash
curl http://localhost:8083/matches/date/2026-06-15
```

### Obtener partido por ID
```bash
curl http://localhost:8083/matches/1
```

---

## TAREA SIGUIENTE

Una vez implementado el World Cup Service, procede al README-11-prediction-service.md para implementar el Prediction Service.
