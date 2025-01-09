-- V1__Create_URL_Table.sql

CREATE TABLE IF NOT EXISTS url
(
    id        SERIAL PRIMARY KEY,
    short_url VARCHAR(255) NOT NULL,
    full_url  TEXT         NOT NULL
);
