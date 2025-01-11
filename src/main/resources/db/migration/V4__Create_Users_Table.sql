-- V4__Create_Users_Table.sql
CREATE TABLE users
(
    id          SERIAL PRIMARY KEY,
    username    VARCHAR(50) NOT NULL UNIQUE,
    otp_secret  VARCHAR(255) NOT NULL -- Stores the secret key for generating one-time passwords
);
