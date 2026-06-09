ALTER TABLE users
    ADD COLUMN IF NOT EXISTS student_code VARCHAR(30);

CREATE UNIQUE INDEX IF NOT EXISTS uk_users_student_code
    ON users (student_code)
    WHERE student_code IS NOT NULL;
