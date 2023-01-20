-- liquibase formatted sql

-- changeset pecheneg:1

CREATE TABLE IF NOT EXISTS users
(
    user_id      SERIAL PRIMARY KEY,
    first_name   VARCHAR(100)        NOT NULL,
    last_name    VARCHAR(100)        NOT NULL,
    phone_number VARCHAR(18)         NOT NULL,
    email        VARCHAR(100) UNIQUE NOT NULL,
    password     VARCHAR(255)        NOT NULL,
    role         VARCHAR(10)         NOT NULL
        CONSTRAINT role_check CHECK ( role IN ('USER', 'ADMIN'))
);

CREATE TABLE IF NOT EXISTS ads
(
    ads_id          SERIAL PRIMARY KEY,
    author_user_id  BIGINT REFERENCES users (user_id),
    ads_title       VARCHAR(100) NOT NULL,
    ads_description TEXT,
    ads_price       BIGINT       NOT NULL
);

CREATE TABLE IF NOT EXISTS ads_image
(
    ads_image_id  SERIAL PRIMARY KEY,
    ads_id        BIGINT REFERENCES ads (ads_id),
    file_path     VARCHAR(255),
    file_size     BIGINT NOT NULL,
    media_type    VARCHAR(255),
    image_preview oid
);

CREATE TABLE IF NOT EXISTS ads_comment
(
    comment_id         SERIAL PRIMARY KEY,
    author_user_id     BIGINT REFERENCES users (user_id),
    ads_id             BIGINT REFERENCES ads (ads_id),
    comment_text       TEXT      NOT NULL,
    comment_created_at TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS ads_comment_author_id ON ads_comment (author_user_id);
CREATE INDEX  IF NOT EXISTS ads_comment_ads_id ON ads_comment (ads_id);

CREATE TABLE IF NOT EXISTS users_avatars
(
    avatar_id      SERIAL PRIMARY KEY,
    author_id      BIGINT REFERENCES users (user_id),
    file_path      VARCHAR(255),
    file_size      BIGINT       NOT NULL,
    media_type     VARCHAR(255) NOT NULL,
    avatar_preview oid          NOT NULL
);

-- changeset cherniltcev:2
ALTER TABLE IF EXISTS users
    RENAME COLUMN email TO login;

ALTER TABLE IF EXISTS ads
    ALTER COLUMN ads_id SET DATA TYPE BIGINT;

ALTER TABLE IF EXISTS users
    ALTER COLUMN user_id SET DATA TYPE BIGINT;

ALTER TABLE IF EXISTS ads_comment
    ALTER COLUMN ads_id SET DATA TYPE BIGINT;

ALTER TABLE IF EXISTS ads_comment
    ALTER COLUMN comment_id SET DATA TYPE BIGINT;

ALTER TABLE IF EXISTS ads_image
    ALTER COLUMN ads_id SET DATA TYPE BIGINT;

ALTER TABLE IF EXISTS ads_image
    ALTER COLUMN ads_image_id SET DATA TYPE BIGINT;

ALTER TABLE IF EXISTS users_avatars
    ALTER COLUMN author_id SET DATA TYPE BIGINT;

ALTER TABLE IF EXISTS users_avatars
    ALTER COLUMN avatar_id SET DATA TYPE BIGINT;

-- changeset yukov:3

ALTER TABLE IF EXISTS users
    ADD COLUMN reg_date  VARCHAR(255);

ALTER TABLE IF EXISTS users
    ADD COLUMN city  VARCHAR(255);

-- changeset yukov:4

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

ALTER TABLE IF EXISTS ads_image
    ALTER COLUMN ads_image_id DROP DEFAULT,
    ALTER COLUMN ads_image_id SET DATA TYPE UUID USING (uuid_generate_v4()),
    ALTER COLUMN ads_image_id SET DEFAULT uuid_generate_v4();

ALTER TABLE IF EXISTS ads_image
    RENAME COLUMN ads_image_id TO ads_image_uuid;

ALTER TABLE IF EXISTS users_avatars
    ALTER COLUMN avatar_id DROP DEFAULT,
    ALTER COLUMN avatar_id SET DATA TYPE UUID USING (uuid_generate_v4()),
    ALTER COLUMN avatar_id SET DEFAULT uuid_generate_v4();

ALTER TABLE IF EXISTS users_avatars
    RENAME COLUMN avatar_id TO avatar_uuid;

-- changeset yukov:5

INSERT INTO users (first_name, last_name, phone_number, login, password, role, reg_date, city)
VALUES
    ('ADMIN', 'ADMINOV', '+77777777777', 'admin@gmail.com', '{bcrypt}$2a$10$2DvMDVwM5xOt56ExukLCYOxFIdKbBkImjQFBJ5TAblYqIaL0Xaw2G', 'ADMIN', null, null),
    ('USER', 'USEROV', '+79878885566', 'user@gmail.com', '{bcrypt}$2a$10$OKSRlOWKDk7uBaO8UoP0dOVAhuKF6GzjEYwbMuX8cTuc78kdjMS.m', 'USER', null, null);