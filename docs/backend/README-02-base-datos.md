# README 02: Base de Datos - Esquema Completo

## PROMPT PARA EL EQUIPO DE DESARROLLO

Como arquitecto de base de datos, tu tarea es implementar el esquema completo de 29 tablas en PostgreSQL para soportar el sistema Tecsup World Cup Challenge 2026.

---

## 1. SEGURIDAD (4 tablas)

### roles
```sql
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(255)
);

-- Datos iniciales:
-- ADMIN, STUDENT, TEACHER
```

### users
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    role_id BIGINT NOT NULL,
    avatar_id BIGINT,
    email VARCHAR(150) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email_verified BOOLEAN DEFAULT FALSE,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_user_role
        FOREIGN KEY(role_id) REFERENCES roles(id)
);
```

### email_verification_tokens
```sql
CREATE TABLE email_verification_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token VARCHAR(10) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    used BOOLEAN DEFAULT FALSE,
    
    FOREIGN KEY(user_id) REFERENCES users(id)
);
```

### audit_logs
```sql
CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    action VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

## 2. ORGANIZACIÓN TECSUP (6 tablas)

### campuses
```sql
CREATE TABLE campuses (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

-- Datos iniciales:
-- Lima, Arequipa, Trujillo
```

### departments
```sql
CREATE TABLE departments (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL
);

-- Datos iniciales:
-- Tecnología Digital
-- Mecánica y Aviación
-- Electricidad y Electrónica
-- Mecatrónica
-- Minería, Procesos Químicos y Metalúrgicos
-- Gestión y Producción
-- Seguridad y Salud en el Trabajo
-- Tecnología Agrícola
```

### careers
```sql
CREATE TABLE careers (
    id BIGSERIAL PRIMARY KEY,
    department_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    
    FOREIGN KEY (department_id)
        REFERENCES departments(id)
);
```

### avatars
```sql
CREATE TABLE avatars (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100),
    image_url VARCHAR(500)
);
```

### student_profiles
```sql
CREATE TABLE student_profiles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL,
    campus_id BIGINT NOT NULL,
    career_id BIGINT NOT NULL,
    student_code VARCHAR(30) UNIQUE NOT NULL,
    cycle VARCHAR(20),
    
    FOREIGN KEY(user_id) REFERENCES users(id),
    FOREIGN KEY(campus_id) REFERENCES campuses(id),
    FOREIGN KEY(career_id) REFERENCES careers(id)
);
```

### teacher_profiles
```sql
CREATE TABLE teacher_profiles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL,
    campus_id BIGINT NOT NULL,
    department_id BIGINT NOT NULL,
    
    FOREIGN KEY(user_id) REFERENCES users(id),
    FOREIGN KEY(campus_id) REFERENCES campuses(id),
    FOREIGN KEY(department_id) REFERENCES departments(id)
);
```

---

## 3. MUNDIAL (7 tablas)

### tournaments
```sql
CREATE TABLE tournaments (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200),
    year INT,
    start_date DATE,
    end_date DATE
);
```

### stages
```sql
CREATE TABLE stages (
    id BIGSERIAL PRIMARY KEY,
    tournament_id BIGINT,
    name VARCHAR(100),
    
    FOREIGN KEY(tournament_id)
        REFERENCES tournaments(id)
);

-- Datos iniciales:
-- Grupos, Octavos, Cuartos, Semifinal, Final
```

### groups
```sql
CREATE TABLE groups (
    id BIGSERIAL PRIMARY KEY,
    tournament_id BIGINT,
    name VARCHAR(5),
    
    FOREIGN KEY(tournament_id)
        REFERENCES tournaments(id)
);

-- Grupo A, B, C, D, E, F, G, H
```

### teams
```sql
CREATE TABLE teams (
    id BIGSERIAL PRIMARY KEY,
    api_team_id BIGINT,
    name VARCHAR(100),
    code VARCHAR(10),
    flag_url VARCHAR(500)
);
```

### players
```sql
CREATE TABLE players (
    id BIGSERIAL PRIMARY KEY,
    api_player_id BIGINT,
    team_id BIGINT,
    full_name VARCHAR(200),
    
    FOREIGN KEY(team_id)
        REFERENCES teams(id)
);
```

### matches
```sql
CREATE TABLE matches (
    id BIGSERIAL PRIMARY KEY,
    api_match_id BIGINT,
    tournament_id BIGINT,
    stage_id BIGINT,
    
    home_team_id BIGINT,
    away_team_id BIGINT,
    
    kickoff_time TIMESTAMP,
    
    home_score INT,
    away_score INT,
    
    status VARCHAR(50),
    
    FOREIGN KEY(tournament_id)
        REFERENCES tournaments(id),
    
    FOREIGN KEY(stage_id)
        REFERENCES stages(id),
    
    FOREIGN KEY(home_team_id)
        REFERENCES teams(id),
    
    FOREIGN KEY(away_team_id)
        REFERENCES teams(id)
);
```

### match_events
```sql
CREATE TABLE match_events (
    id BIGSERIAL PRIMARY KEY,
    match_id BIGINT,
    player_id BIGINT,
    event_type VARCHAR(50),
    minute INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY(match_id) REFERENCES matches(id),
    FOREIGN KEY(player_id) REFERENCES players(id)
);

-- event_type: GOAL, YELLOW_CARD, RED_CARD, SUBSTITUTION, VAR, etc.
```

---

## 4. PREDICCIONES (3 tablas)

### prediction_types
```sql
CREATE TABLE prediction_types (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50),
    name VARCHAR(100),
    points INT
);

-- Datos iniciales:
-- WINNER, EXACT_SCORE, FIRST_GOAL_SCORER, BOTH_SCORE, etc.
```

### predictions
```sql
CREATE TABLE predictions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    match_id BIGINT,
    prediction_type_id BIGINT,
    
    prediction_value VARCHAR(500),
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY(user_id)
        REFERENCES users(id),
    
    FOREIGN KEY(match_id)
        REFERENCES matches(id),
    
    FOREIGN KEY(prediction_type_id)
        REFERENCES prediction_types(id)
);
```

### prediction_results
```sql
CREATE TABLE prediction_results (
    id BIGSERIAL PRIMARY KEY,
    prediction_id BIGINT,
    is_correct BOOLEAN,
    points_earned INT,
    
    FOREIGN KEY(prediction_id)
        REFERENCES predictions(id)
);
```

---

## 5. BLOQUEO DE PREDICCIONES (1 tabla)

### user_predictions_lock
```sql
CREATE TABLE user_predictions_lock (
    id BIGSERIAL PRIMARY KEY,
    prediction_id BIGINT,
    locked_at TIMESTAMP,
    reason VARCHAR(255),
    
    FOREIGN KEY(prediction_id) REFERENCES predictions(id)
);
```

---

## 6. SCORING (4 tablas)

### scoring_rules
```sql
CREATE TABLE scoring_rules (
    id BIGSERIAL PRIMARY KEY,
    prediction_type_id BIGINT,
    points INT,
    
    FOREIGN KEY(prediction_type_id)
        REFERENCES prediction_types(id)
);
```

### user_scores
```sql
CREATE TABLE user_scores (
    user_id BIGINT PRIMARY KEY,
    total_points BIGINT DEFAULT 0,
    
    FOREIGN KEY(user_id)
        REFERENCES users(id)
);
```

### score_history
```sql
CREATE TABLE score_history (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    points INT,
    reason VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY(user_id)
        REFERENCES users(id)
);
```

### achievement_rules
```sql
CREATE TABLE achievement_rules (
    id BIGSERIAL PRIMARY KEY,
    badge_id BIGINT,
    required_points INT,
    required_streak INT,
    required_position INT,
    
    FOREIGN KEY(badge_id) REFERENCES badges(id)
);
```

---

## 7. RANKINGS (2 tablas)

### leaderboards
```sql
CREATE TABLE leaderboards (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100),
    type VARCHAR(50)
);

-- Tipos:
-- GLOBAL, CAMPUS, DEPARTMENT, CAREER, STUDENT, TEACHER
```

### leaderboard_entries
```sql
CREATE TABLE leaderboard_entries (
    id BIGSERIAL PRIMARY KEY,
    leaderboard_id BIGINT,
    user_id BIGINT,
    ranking_position INT,
    points BIGINT,
    
    FOREIGN KEY(leaderboard_id)
        REFERENCES leaderboards(id),
    
    FOREIGN KEY(user_id)
        REFERENCES users(id)
);
```

---

## 8. SOCIAL (2 tablas)

### comments
```sql
CREATE TABLE comments (
    id BIGSERIAL PRIMARY KEY,
    match_id BIGINT,
    user_id BIGINT,
    content TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY(match_id)
        REFERENCES matches(id),
    
    FOREIGN KEY(user_id)
        REFERENCES users(id)
);
```

### comment_reactions
```sql
CREATE TABLE comment_reactions (
    id BIGSERIAL PRIMARY KEY,
    comment_id BIGINT,
    user_id BIGINT,
    reaction VARCHAR(20),
    
    FOREIGN KEY(comment_id)
        REFERENCES comments(id),
    
    FOREIGN KEY(user_id)
        REFERENCES users(id)
);
```

---

## 9. GAMIFICACIÓN (3 tablas)

### badges
```sql
CREATE TABLE badges (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100),
    description TEXT,
    icon VARCHAR(255)
);
```

### user_badges
```sql
CREATE TABLE user_badges (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    badge_id BIGINT,
    earned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY(user_id)
        REFERENCES users(id),
    
    FOREIGN KEY(badge_id)
        REFERENCES badges(id)
);
```

### rewards
```sql
CREATE TABLE rewards (
    id BIGSERIAL PRIMARY KEY,
    position INT,
    title VARCHAR(200),
    description TEXT
);
```

---

## 10. NOTIFICACIONES (1 tabla)

### notifications
```sql
CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    title VARCHAR(200),
    message TEXT,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY(user_id)
        REFERENCES users(id)
);
```

---

## 11. ÍNDICES RECOMENDADOS

```sql
-- Índices para usuarios
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role_id);

-- Índices para predicciones
CREATE INDEX idx_predictions_user ON predictions(user_id);
CREATE INDEX idx_predictions_match ON predictions(match_id);
CREATE INDEX idx_predictions_type ON predictions(prediction_type_id);

-- Índices para partidos
CREATE INDEX idx_matches_tournament ON matches(tournament_id);
CREATE INDEX idx_matches_stage ON matches(stage_id);
CREATE INDEX idx_matches_kickoff ON matches(kickoff_time);

-- Índices para rankings
CREATE INDEX idx_leaderboard_entries_leaderboard ON leaderboard_entries(leaderboard_id);
CREATE INDEX idx_leaderboard_entries_user ON leaderboard_entries(user_id);
CREATE INDEX idx_leaderboard_entries_points ON leaderboard_entries(points DESC);

-- Índices para comentarios
CREATE INDEX idx_comments_match ON comments(match_id);
CREATE INDEX idx_comments_user ON comments(user_id);
CREATE INDEX idx_comments_created ON comments(created_at DESC);
```

---

## 12. SEED DATA INICIAL

### Sedes
```sql
INSERT INTO campuses (name) VALUES 
('Lima'), ('Arequipa'), ('Trujillo');
```

### Departamentos
```sql
INSERT INTO departments (name) VALUES 
('Tecnología Digital'),
('Mecánica y Aviación'),
('Electricidad y Electrónica'),
('Mecatrónica'),
('Minería, Procesos Químicos y Metalúrgicos'),
('Gestión y Producción'),
('Seguridad y Salud en el Trabajo'),
('Tecnología Agrícola');
```

### Roles
```sql
INSERT INTO roles (name, description) VALUES 
('ADMIN', 'Administrador del sistema'),
('STUDENT', 'Estudiante de Tecsup'),
('TEACHER', 'Docente de Tecsup');
```

---

## TAREA SIGUIENTE

Una vez implementado el esquema de base de datos, procede al README-03-arquitectura-general.md para comprender la arquitectura de microservicios.
