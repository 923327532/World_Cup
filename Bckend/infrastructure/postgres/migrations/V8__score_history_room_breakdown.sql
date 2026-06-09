ALTER TABLE score_history
    ADD COLUMN IF NOT EXISTS room_id BIGINT,
    ADD COLUMN IF NOT EXISTS match_id BIGINT,
    ADD COLUMN IF NOT EXISTS prediction_id BIGINT,
    ADD COLUMN IF NOT EXISTS base_points INT,
    ADD COLUMN IF NOT EXISTS streak_bonus INT DEFAULT 0,
    ADD COLUMN IF NOT EXISTS early_bonus INT DEFAULT 0;

CREATE INDEX IF NOT EXISTS idx_score_history_room_id
    ON score_history(room_id);

CREATE INDEX IF NOT EXISTS idx_score_history_room_user
    ON score_history(room_id, user_id);

CREATE INDEX IF NOT EXISTS idx_score_history_match_id
    ON score_history(match_id);
