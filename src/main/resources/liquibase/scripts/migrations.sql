CREATE TABLE if not exists users
(
    id         serial       NOT NULL PRIMARY KEY,
    first_name varchar(255) NOT NULL,
    last_name  varchar(255) NOT NULL,
    phone      varchar(255) NOT NULL,
    email      varchar(255) NOT NULL,
    password   varchar(255) NOT NULL,
    role       varchar(255) NOT NULL DEFAULT 'USER'

);

CREATE TABLE if not exists images
(
    pk         serial       NOT NULL PRIMARY KEY,
    data       BYTEA,
    file_path  varchar(255) NOT NULL,
    file_size  integer      NOT NULL,
    media_type varchar(255) NOT NULL

);
CREATE TABLE if not exists ads
(
    pk          serial       NOT NULL PRIMARY KEY,
    price       integer      NOT NULL,
    title       varchar(255) NOT NULL,
    description varchar(255) NOT NULL,
    image_id    bigint       NOT NULL REFERENCES images (pk),
    author_id   bigint       NOT NULL REFERENCES users (id) --Как вариант, ведь автор должен быть
);

CREATE TABLE if not exists comments --Или как-то ещё назвать
(
    pk         serial    NOT NULL PRIMARY KEY,
    author_id  bigint    NOT NULL REFERENCES users (id),
    ads_pk     bigint    NOT NULL REFERENCES ads (pk),
    created_at timestamp NOT NULL,
    text       text      NOT NULL
);


