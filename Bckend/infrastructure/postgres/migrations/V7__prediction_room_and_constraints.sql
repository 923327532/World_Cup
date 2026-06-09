-- Add room context to predictions so scores and rankings can be calculated per private room.
ALTER TABLE predictions
    ADD COLUMN IF NOT EXISTS room_id BIGINT;

-- Keep the migration safe for existing local data. New application requests validate room_id as required.
ALTER TABLE predictions
    ADD CONSTRAINT fk_predictions_room
    FOREIGN KEY (room_id) REFERENCES rooms(id)
    ON DELETE CASCADE;

CREATE UNIQUE INDEX IF NOT EXISTS uq_predictions_user_room_match
    ON predictions(user_id, room_id, match_id)
    WHERE room_id IS NOT NULL;

CREATE INDEX IF NOT EXISTS idx_predictions_room_id
    ON predictions(room_id);
