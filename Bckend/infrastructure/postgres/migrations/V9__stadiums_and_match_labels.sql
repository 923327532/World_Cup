-- ============================================
-- Tecsup World Cup Challenge 2026
-- Migración V9: Stadiums y labels para partidos
-- ============================================

CREATE TABLE IF NOT EXISTS stadiums (
    id BIGSERIAL PRIMARY KEY,
    api_stadium_id BIGINT UNIQUE,
    name VARCHAR(200),
    fifa_name VARCHAR(200),
    city VARCHAR(200),
    country VARCHAR(120),
    capacity INT
);

ALTER TABLE matches
    ADD COLUMN IF NOT EXISTS stadium_id BIGINT;

ALTER TABLE matches
    ADD COLUMN IF NOT EXISTS home_team_label VARCHAR(150);

ALTER TABLE matches
    ADD COLUMN IF NOT EXISTS away_team_label VARCHAR(150);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_match_stadium'
          AND conrelid = 'matches'::regclass
    ) THEN
        ALTER TABLE matches
            ADD CONSTRAINT fk_match_stadium
            FOREIGN KEY (stadium_id) REFERENCES stadiums(id);
    END IF;
END $$;
