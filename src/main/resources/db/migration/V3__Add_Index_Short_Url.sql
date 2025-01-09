-- V3__Add_Index_Short_Url.sql

-- Add a unique index on the short_url column to improve lookup performance
CREATE UNIQUE INDEX idx_short_url ON url(short_url);
