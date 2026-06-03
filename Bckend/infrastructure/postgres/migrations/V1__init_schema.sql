-- ============================================
-- Tecsup World Cup Challenge 2026
-- Migración V1: Esquema completo (29 tablas)
-- ============================================

-- ============================================
-- 1. SEGURIDAD (4 tablas)
-- ============================================

CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(255)
);

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

CREATE TABLE email_verification_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token VARCHAR(10) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    used BOOLEAN DEFAULT FALSE,
    
    FOREIGN KEY(user_id) REFERENCES users(id)
);

CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    action VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- 2. ORGANIZACIÓN TECSUP (6 tablas)
-- ============================================

CREATE TABLE campuses (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

CREATE TABLE departments (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL
);

CREATE TABLE careers (
    id BIGSERIAL PRIMARY KEY,
    department_id BIGINT NOT NULL,
    name VARCHAR(200) NOT NULL,
    
    FOREIGN KEY (department_id)
        REFERENCES departments(id)
);

CREATE TABLE avatars (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100),
    image_url VARCHAR(500)
);

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

CREATE TABLE teacher_profiles (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT UNIQUE NOT NULL,
    campus_id BIGINT NOT NULL,
    department_id BIGINT NOT NULL,
    
    FOREIGN KEY(user_id) REFERENCES users(id),
    FOREIGN KEY(campus_id) REFERENCES campuses(id),
    FOREIGN KEY(department_id) REFERENCES departments(id)
);

-- ============================================
-- 3. MUNDIAL (7 tablas)
-- ============================================

CREATE TABLE tournaments (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200),
    year INT,
    start_date DATE,
    end_date DATE
);

CREATE TABLE stages (
    id BIGSERIAL PRIMARY KEY,
    tournament_id BIGINT,
    name VARCHAR(100),
    
    FOREIGN KEY(tournament_id)
        REFERENCES tournaments(id)
);

CREATE TABLE "groups" (
    id BIGSERIAL PRIMARY KEY,
    tournament_id BIGINT,
    name VARCHAR(5),
    
    FOREIGN KEY(tournament_id)
        REFERENCES tournaments(id)
);

CREATE TABLE teams (
    id BIGSERIAL PRIMARY KEY,
    api_team_id BIGINT,
    name VARCHAR(100),
    code VARCHAR(10),
    flag_url VARCHAR(500)
);

CREATE TABLE players (
    id BIGSERIAL PRIMARY KEY,
    api_player_id BIGINT,
    team_id BIGINT,
    full_name VARCHAR(200),
    
    FOREIGN KEY(team_id)
        REFERENCES teams(id)
);

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

-- ============================================
-- 4. PREDICCIONES (3 tablas)
-- ============================================

CREATE TABLE prediction_types (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50),
    name VARCHAR(100),
    points INT
);

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

CREATE TABLE prediction_results (
    id BIGSERIAL PRIMARY KEY,
    prediction_id BIGINT,
    is_correct BOOLEAN,
    points_earned INT,
    
    FOREIGN KEY(prediction_id)
        REFERENCES predictions(id)
);

-- ============================================
-- 5. BLOQUEO DE PREDICCIONES (1 tabla)
-- ============================================

CREATE TABLE user_predictions_lock (
    id BIGSERIAL PRIMARY KEY,
    prediction_id BIGINT,
    locked_at TIMESTAMP,
    reason VARCHAR(255),
    
    FOREIGN KEY(prediction_id) REFERENCES predictions(id)
);

-- ============================================
-- 6. SCORING (3 tablas)
-- ============================================

CREATE TABLE scoring_rules (
    id BIGSERIAL PRIMARY KEY,
    prediction_type_id BIGINT,
    points INT,
    
    FOREIGN KEY(prediction_type_id)
        REFERENCES prediction_types(id)
);

CREATE TABLE user_scores (
    user_id BIGINT PRIMARY KEY,
    total_points BIGINT DEFAULT 0,
    
    FOREIGN KEY(user_id)
        REFERENCES users(id)
);

CREATE TABLE score_history (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    points INT,
    reason VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY(user_id)
        REFERENCES users(id)
);

-- ============================================
-- 7. RANKINGS (2 tablas)
-- ============================================

CREATE TABLE leaderboards (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100),
    type VARCHAR(50)
);

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

-- ============================================
-- 8. SOCIAL (2 tablas)
-- ============================================

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

-- ============================================
-- 9. GAMIFICACIÓN (4 tablas)
-- ============================================

CREATE TABLE badges (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100),
    description TEXT,
    icon VARCHAR(255)
);

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

CREATE TABLE rewards (
    id BIGSERIAL PRIMARY KEY,
    position INT,
    title VARCHAR(200),
    description TEXT
);

CREATE TABLE achievement_rules (
    id BIGSERIAL PRIMARY KEY,
    badge_id BIGINT,
    required_points INT,
    required_streak INT,
    required_position INT,
    
    FOREIGN KEY(badge_id) REFERENCES badges(id)
);

-- ============================================
-- 10. NOTIFICACIONES (1 tabla)
-- ============================================

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
