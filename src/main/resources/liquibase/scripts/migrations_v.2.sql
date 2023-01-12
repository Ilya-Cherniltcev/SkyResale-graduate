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
    ads_price       BIGINT       NOT NULL,
    ads_image_id      VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS ads_image
(
    ads_image_id  SERIAL PRIMARY KEY,
    ads_id        BIGINT REFERENCES ads (ads_id),
    file_path     VARCHAR(255) ,
    file_size     BIGINT       NOT NULL,
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

CREATE INDEX ads_comment_author_id ON ads_comment (author_user_id);
CREATE INDEX ads_comment_ads_id ON ads_comment (ads_id);

CREATE TABLE IF NOT EXISTS users_avatars
(
    avatar_id      SERIAL PRIMARY KEY,
    author_id      BIGINT REFERENCES users (user_id),
    file_path     VARCHAR(255) ,
    file_size     BIGINT       NOT NULL,
    media_type     VARCHAR(255) NOT NULL,
    avatar_preview oid          NOT NULL
);

