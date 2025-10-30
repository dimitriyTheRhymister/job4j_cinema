--liquibase formatted sql

--changeset author:job4j id:003
CREATE TABLE films
(
    id                  SERIAL PRIMARY KEY,
    name                VARCHAR                    NOT NULL,
    description         VARCHAR                    NOT NULL,
    release_year        INT                        NOT NULL,
    genre_id            INT REFERENCES genres (id) NOT NULL,
    minimal_age         INT                        NOT NULL,
    duration_in_minutes INT                        NOT NULL,
    file_id             INT REFERENCES files (id)  NOT NULL
);