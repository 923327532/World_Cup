# README 01: Requisitos del Proyecto - Proceso de Ingeniero de Requisitos

## PROMPT PARA EL EQUIPO DE DESARROLLO

Como ingeniero de requisitos, tu tarea es comprender y documentar completamente el proyecto Tecsup World Cup Challenge 2026 antes de iniciar cualquier desarrollo.

---

## 1. VISIÓN GENERAL DEL PROYECTO

### Nombre del Proyecto
**Tecsup World Cup Challenge 2026**

### Descripción
Desarrollar una plataforma web de predicciones deportivas en tiempo real enfocada exclusivamente en la Copa Mundial de la FIFA, diseñada para la comunidad académica de Tecsup.

### Objetivo Principal
Crear una aplicación web escalable basada en microservicios que permita a más de 10,000 estudiantes y docentes participar simultáneamente en predicciones deportivas del Mundial, obteniendo puntuaciones dinámicas según sus aciertos y compitiendo en un ranking general institucional.

---

## 2. REGLAS DE NEGOCIO FUNDAMENTALES

### Participantes
- **Estudiantes**: Correo institucional Tecsup + Código de alumno
- **Docentes**: Correo institucional Tecsup

### Sedes Disponibles
1. Lima
2. Arequipa
3. Trujillo

### Departamentos
1. Tecnología Digital
2. Mecánica y Aviación
3. Electricidad y Electrónica
4. Mecatrónica
5. Minería, Procesos Químicos y Metalúrgicos
6. Gestión y Producción
7. Seguridad y Salud en el Trabajo
8. Tecnología Agrícola

### Reglas de Predicciones
- **Edición permitida**: Hasta 5 minutos antes del inicio del partido
- **Bloqueo automático**: Después de los 5 minutos, la predicción queda bloqueada
- **Modo Diario**: Usuario predice únicamente partidos del día
- **Modo Semanal**: Puede dejar todas sus predicciones de la semana registradas

---

## 3. SISTEMA DE PUNTUACIÓN

### Puntos por Predicción
| Predicción | Puntos |
|------------|--------|
| Ganador del partido | 10 |
| Empate | 15 |
| Marcador exacto | 50 |
| Equipo que anota primero | 15 |
| Primer goleador | 30 |
| Goleador del partido | 20 |
| Cantidad exacta de goles | 25 |
| Más de 2.5 goles | 10 |
| Menos de 2.5 goles | 10 |
| Ambos anotan | 10 |
| Cantidad exacta de córners | 30 |
| Cantidad exacta de amarillas | 25 |
| Cantidad exacta de rojas | 40 |
| MVP del partido | 20 |
| Equipo con más posesión | 15 |

### Bonus Mundial
- **3 aciertos seguidos** = +20 puntos
- **5 aciertos seguidos** = +50 puntos
- **10 aciertos seguidos** = +150 puntos

### Partido Perfecto
Si acierta: ganador + marcador exacto + primer goleador = +100 puntos extra

---

## 4. RANKINGS DEL SISTEMA

### Tipos de Rankings
1. **Ranking Global**: Todos contra todos
2. **Ranking por Sede**: Lima, Arequipa, Trujillo
3. **Ranking por Departamento**: Los 8 departamentos compiten entre sí
4. **Ranking por Carrera**: Todas las carreras compiten entre sí
5. **Ranking Estudiantes**: Solo estudiantes
6. **Ranking Docentes**: Solo docentes
7. **Ranking Mixto**: Docentes y estudiantes juntos

---

## 5. FUNCIONALIDADES PRINCIPALES

### Dashboard Mundial
- Partidos del día
- Usuarios conectados
- Predicciones realizadas
- Tendencias de apuestas
- Ranking en vivo
- Noticias deportivas

### Centro de Predicciones
- Selección de partido
- Tipo de predicción
- Puntos potenciales
- Interfaz moderna y dinámica

### Ranking Global
- Top 100 estudiantes
- Top por sede
- Top por carrera
- Top docentes
- Top semanal
- Top general

### Estadísticas Personales
- Aciertos
- Fallos
- Porcentaje de precisión
- Posición actual
- Evolución histórica

### Centro de Partidos en Vivo
- Marcador en vivo (WebSocket)
- Goles
- Estadísticas
- Eventos
- Comentarios
- Reacciones

### Predicción Colectiva Tecsup
- Porcentaje de estudiantes que apuesta por cada resultado
- Qué carrera tiene mejores predicciones
- Qué sede lidera el ranking
- Mapa de calor de participación
- Tendencias institucionales

---

## 6. INSIGNIAS Y GAMIFICACIÓN

### Ejemplos de Insignias
| Insignia | Requisito |
|----------|-----------|
| Primer Gol | Primera predicción acertada |
| Racha 3 | 3 aciertos consecutivos |
| Racha 5 | 5 aciertos consecutivos |
| Racha 10 | 10 aciertos consecutivos |
| Experto Mundialista | 1000 puntos |
| Maestro de Predicciones | 2500 puntos |
| Leyenda Tecsup | Top 10 |
| Campeón Mundial | Top 1 |

---

## 7. REQUISITOS NO FUNCIONALES

### Escalabilidad
- Más de 10,000 usuarios concurrentes
- Miles de predicciones simultáneas
- Actualizaciones en tiempo real mediante WebSockets
- Cálculo instantáneo de rankings
- Alta disponibilidad durante partidos del mundial

### Seguridad
- Verificación por correo institucional
- Código OTP de 6 dígitos para activación
- JWT Authentication
- Roles: ADMIN, STUDENT, TEACHER

---

## 8. STACK TECNOLÓGICO DEFINIDO

### Backend
- Java 21
- Spring Boot 3
- Spring Security
- Spring Cloud Gateway
- Spring WebSocket
- Spring Data JPA
- PostgreSQL
- Redis Cache
- RabbitMQ
- Docker

### Frontend
- Angular 20
- Angular Material
- RxJS
- NgRx (opcional)

### Infraestructura
- AWS (EC2, RDS PostgreSQL, Redis, S3)
- Docker Compose
- Kubernetes (opcional)

---

## 9. API EXTERNA A CONSUMIR

### Opciones Evaluadas
1. **API-Football** (Recomendada)
   - fixtures, standings, teams, players, statistics, events en vivo
   
2. **Sportmonks Football API**
   - Más profesional
   
3. **Football-Data.org**
   - Más simple

### Estrategia de Consumo
- NO hacer que Angular consuma la API deportiva directamente
- WorldCup Service consume la API externa
- Datos se almacenan en PostgreSQL
- Redis para caché
- Angular consume desde API Gateway

---

## 10. MICROSERVICIOS DEFINIDOS (8 servicios de negocio)

1. **Auth Service** - Autenticación y autorización
2. **Organization Service** - Información institucional Tecsup
3. **World Cup Service** - Información deportiva y API externa
4. **Prediction Service** - Núcleo principal del sistema
5. **Scoring Service** - Sistema de puntos
6. **Leaderboard Service** - Rankings
7. **Social Service** - Comentarios y comunidad
8. **Gamification Service** - Insignias y premios

---

## 11. TABLAS DE BASE DE DATOS (29 tablas)

### Seguridad (4 tablas)
- roles
- users
- email_verification_tokens
- audit_logs

### Organización (6 tablas)
- campuses
- departments
- careers
- avatars
- student_profiles
- teacher_profiles

### Mundial (7 tablas)
- tournaments
- stages
- groups
- teams
- players
- matches
- match_events

### Predicciones (3 tablas)
- prediction_types
- predictions
- prediction_results

### Bloqueo de Predicciones (1 tabla)
- user_predictions_lock

### Scoring (4 tablas)
- scoring_rules
- user_scores
- score_history
- achievement_rules

### Rankings (2 tablas)
- leaderboards
- leaderboard_entries

### Social (2 tablas)
- comments
- comment_reactions

### Gamificación (3 tablas)
- badges
- user_badges
- rewards

### Notificaciones (1 tabla)
- notifications

---

## 12. PLAZO DE ENTREGA

**Importante**: El proyecto debe completarse en 8 días.

---

## TAREA SIGUIENTE

Una vez comprendidos estos requisitos, procede al README-02-base-datos.md para diseñar el esquema completo de la base de datos.

ya tenemos las 12 microservico con toda la base seria que complemnntes de acuerdo acada prom quye te demos
world-cup-platform
│
├── api-gateway
├── discovery-service
├── config-service
│
├── auth-service
├── organization-service
├── worldcup-service
├── prediction-service
├── scoring-service
├── leaderboard-service
├── social-service
├── gamification-service
├── notification-service
│
├── common-library
│
├── docker
│
├── docs
│
└── docker-compose.yml