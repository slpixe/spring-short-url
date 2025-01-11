-- V5__Add_UserId_To_URL_Table.sql

-- Step 1: Add the user_id column as nullable
ALTER TABLE url
    ADD COLUMN user_id BIGINT;

-- Step 2: Ensure the default user exists in the users table
-- This step is crucial to avoid errors during the update
DO $$
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM users WHERE username = 'default_user') THEN
            INSERT INTO users (username, otp_secret)
            VALUES ('default_user', 'dummy_secret');
        END IF;
    END $$;

-- Step 3: Populate the user_id column for all existing rows in the url table
UPDATE url
SET user_id = (SELECT id FROM users WHERE username = 'default_user' LIMIT 1);

-- Step 4: Add the NOT NULL constraint to the user_id column
ALTER TABLE url
    ALTER COLUMN user_id SET NOT NULL;

-- Step 5: Add the foreign key constraint
ALTER TABLE url
    ADD CONSTRAINT fk_url_user FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE CASCADE;
