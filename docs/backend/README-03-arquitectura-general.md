# README 03: Arquitectura General - Microservicios

## PROMPT PARA EL EQUIPO DE DESARROLLO

Como arquitecto de software, tu tarea es comprender e implementar la arquitectura de microservicios para el sistema Tecsup World Cup Challenge 2026.

---

## 1. ESTRUCTURA GENERAL DEL PROYECTO

```
world-cup-platform
│
├── api-gateway
│
├── discovery-service
│
├── config-service
│
├── auth-service
│
├── organization-service
│
├── worldcup-service
│
├── prediction-service
│
├── scoring-service
│
├── leaderboard-service
│
├── social-service
│
├── gamification-service
│
├── admin-service
│
├── group-service
│
├── notification-service
│
├── common-library
│
├── infrastructure
│   ├── postgres
│   ├── redis
│   ├── rabbitmq
│   ├── monitoring
│   └── nginx
│
├── docs
│
├── docker-compose.yml
│
├── .env
│
└── README.md
```

---

## 2. STACK TECNOLÓGICO

### Backend
- **Java 21**
- **Spring Boot 3**
- **Spring Security**
- **Spring Cloud Gateway**
- **Spring WebSocket**
- **Spring Data JPA**
- **PostgreSQL**
- **Redis Cache**
- **RabbitMQ**
- **Docker**

### Infraestructura
- **Eureka Server** - Service Discovery
- **Config Server** - Configuración centralizada
- **API Gateway** - Gateway único
- **PostgreSQL** - Base de datos principal
- **Redis** - Cache y sesiones
- **RabbitMQ** - Message Broker para eventos
- **Prometheus + Grafana** - Monitoreo (opcional)

---

## 3. MICROSERVICIOS DE NEGOCIO (11 servicios)

### 1. Auth Service
- **Responsabilidad**: Autenticación y autorización
- **Tablas**: roles, users, email_verification_tokens, audit_logs
- **Funciones**: Registro, Login, JWT, Validación institucional

### 2. Organization Service
- **Responsabilidad**: Información institucional de Tecsup
- **Tablas**: campuses, departments, careers, avatars, student_profiles, teacher_profiles
- **Funciones**: Gestión de sedes, departamentos, carreras, perfiles

### 3. World Cup Service
- **Responsabilidad**: Información deportiva y API externa
- **Tablas**: tournaments, stages, groups, teams, players, matches, match_events
- **Funciones**: Consumir API externa, sincronizar partidos, estadísticas en vivo

### 4. Prediction Service
- **Responsabilidad**: Núcleo principal del sistema
- **Tablas**: prediction_types, predictions, prediction_results, user_predictions_lock
- **Funciones**: Crear predicción, editar, bloquear 5 minutos antes

### 5. Scoring Service
- **Responsabilidad**: Sistema de puntos
- **Tablas**: scoring_rules, user_scores, score_history, achievement_rules
- **Funciones**: Calcular puntos, bonificaciones, rachas

### 6. Leaderboard Service
- **Responsabilidad**: Rankings
- **Tablas**: leaderboards, leaderboard_entries
- **Funciones**: Ranking global, por carrera, por sede, por departamento

### 7. Social Service
- **Responsabilidad**: Comentarios y comunidad
- **Tablas**: comments, comment_reactions
- **Funciones**: Comentarios en vivo, reacciones, chat del partido

### 8. Gamification Service
- **Responsabilidad**: Insignias y premios
- **Tablas**: badges, user_badges, rewards
- **Funciones**: Insignias, logros, premios Top 10

### 9. Notification Service
- **Responsabilidad**: Notificaciones
- **Tablas**: notifications
- **Funciones**: Notificaciones, emails, alertas

### 10. Admin Service
- **Responsabilidad**: Administración operativa del torneo
- **Tablas**: manual_matches, admin_audit_logs
- **Funciones**: CRUD de partidos manuales, actualización de resultados, trazabilidad de acciones administrativas

### 11. Group Service
- **Responsabilidad**: Gestión de comunidades privadas
- **Tablas**: rooms, room_members, room_invites, room_reports, room_bans
- **Funciones**: Crear salas, gestionar miembros/roles, invitaciones, reportes de moderación, baneos

---

## 4. SERVICIOS DE INFRAESTRUCTURA (3 servicios)

### API Gateway
- **Responsabilidad**: Recibir todas las peticiones
- **Funciones**: Validar JWT, redireccionar requests, rate limiting, logging

### Discovery Service (Eureka)
- **Responsabilidad**: Registro y descubrimiento de microservicios
- **Funciones**: Registro automático, descubrimiento dinámico

### Config Service
- **Responsabilidad**: Configuración centralizada
- **Funciones**: Configuración de todos los servicios en un solo lugar

---

## 5. LIBRERÍA COMÚN

### common-library
```
common-library
│
├── exception
│   ├── BusinessException
│   └── NotFoundException
│
├── dto
│   └── ApiResponse
│
├── event
│   ├── PredictionCreatedEvent
│   ├── PredictionScoredEvent
│   ├── MatchFinishedEvent
│   ├── BadgeUnlockedEvent
│   └── NotificationEvent
│
└── util
    ├── JwtUtil
    └── DateUtil
```

---

## 6. FLUJO DE EVENTOS (RabbitMQ)

```
Usuario crea predicción
        ↓
PredictionCreatedEvent
        ↓
Scoring Service

Scoring actualizado
        ↓
ScoreUpdatedEvent
        ↓
Leaderboard Service

Ranking actualizado
        ↓
LeaderboardUpdatedEvent
        ↓
Notification Service

Gol en partido
        ↓
MatchGoalEvent
        ↓
Social Service
```

---

## 7. ARQUITECTURA DE COMUNICACIÓN

### Síncrona
- **REST API** - Para operaciones CRUD
- **API Gateway** - Punto único de entrada
- **Feign Client** - Comunicación entre servicios

### Asíncrona
- **RabbitMQ** - Para eventos entre servicios
- **WebSocket** - Para actualizaciones en tiempo real

---

## 8. ESTRATEGIA DE BASE DE DATOS

### PostgreSQL
- Base de datos relacional principal
- 29 tablas distribuidas entre servicios
- Cada servicio tiene su propio schema

### Redis
- Cache de rankings
- Sesiones de usuarios
- Usuarios conectados
- Estadísticas en vivo
- Cache de predicciones frecuentes

---

## 9. SEGURIDAD

### JWT Authentication
- Token generado por Auth Service
- Validado en API Gateway
- Propagado a todos los servicios

### Roles
- **ADMIN**: Acceso total
- **STUDENT**: Acceso limitado a predicciones y rankings
- **TEACHER**: Acceso similar a estudiante

### Verificación de Correo
- Código OTP de 6 dígitos
- Enviado al correo institucional
- Requerido para activar cuenta

---

## 10. ESCALABILIDAD

### Horizontal Scaling
- Cada microservicio puede escalar independientemente
- Docker Compose para desarrollo
- Kubernetes para producción (opcional)

### Caching Strategy
- Redis para datos frecuentemente accedidos
- Rankings cacheados
- Estadísticas en vivo en Redis

### Load Balancing
- API Gateway como balanceador
- Múltiples instancias de cada servicio

---

## 11. MONITOREO (Opcional pero recomendado)

### Prometheus
- Métricas de todos los servicios
- Usuarios conectados
- Predicciones por minuto
- Tiempo de respuesta
- Uso de memoria y CPU
- Mensajes RabbitMQ

### Grafana
- Dashboards visuales
- Alertas en tiempo real
- Histórico de métricas

---

## 12. ESTRUCTURA DE CADA MICROSERVICIO

### Estructura estándar
```
service-name
│
├── controller
│   └── *Controller
│
├── service
│   └── *Service
│
├── repository
│   └── *Repository
│
├── entity
│   └── *Entity
│
├── dto
│   └── *DTO
│
├── config
│   └── *Config
│
├── exception
│   └── *Exception
│
├── listener
│   └── *Listener (para RabbitMQ)
│
├── scheduler
│   └── *Scheduler (para tareas programadas)
│
├── resources
│   ├── application.yml
│   └── application-dev.yml
│
└── *Application.java
```

---

## TAREA SIGUIENTE

Una vez comprendida la arquitectura general, procede al README-04-infraestructura.md para configurar la infraestructura base (Docker, Redis, RabbitMQ, etc.).
