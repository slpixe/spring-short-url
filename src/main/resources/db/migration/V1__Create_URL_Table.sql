-- V1__Create_URL_Table.sql

CREATE TABLE IF NOT EXISTS URL
(
    id        SERIAL PRIMARY KEY,
    short_url VARCHAR(255) NOT NULL,
    full_url  TEXT         NOT NULL
);

-- To execute this migration, save it in the Flyway migrations folder (e.g., resources/db/migration)
