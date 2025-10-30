--liquibase formatted sql

--changeset author:job4j id:002
CREATE TABLE genres
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR UNIQUE NOT NULL
);