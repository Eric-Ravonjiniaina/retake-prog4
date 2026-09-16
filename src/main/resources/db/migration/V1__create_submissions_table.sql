CREATE TABLE submissions
(
    id            UUID PRIMARY KEY,
    email         VARCHAR(320)  NOT NULL,
    thumbnail_key VARCHAR(1024),
    created_at    TIMESTAMP     NOT NULL
);