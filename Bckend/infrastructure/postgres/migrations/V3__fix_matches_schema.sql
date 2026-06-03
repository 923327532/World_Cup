-- ============================================
-- Tecsup World Cup Challenge 2026
-- Migración V3: Correcciones de schema
-- ============================================

-- Agregar columna venue a matches (solo si no existe)
ALTER TABLE matches ADD COLUMN IF NOT EXISTS venue VARCHAR(100);

-- Agregar columna group_id a matches (solo si no existe)
ALTER TABLE matches ADD COLUMN IF NOT EXISTS group_id BIGINT;

-- FK para group_id (solo si no existe la constraint)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_match_group'
          AND conrelid = 'matches'::regclass
    ) THEN
        ALTER TABLE matches ADD CONSTRAINT fk_match_group
            FOREIGN KEY (group_id) REFERENCES "groups"(id);
    END IF;
END $$;
