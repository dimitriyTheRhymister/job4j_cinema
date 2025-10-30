--liquibase formatted sql

--changeset author:job4j id:010
INSERT INTO films (name, description, release_year, genre_id, minimal_age, duration_in_minutes, file_id) VALUES
    ('Inception', 'A thief who steals corporate secrets...', 2010, 5, 16, 148, 2),
    ('The Hangover', 'A comedy about a bachelor party...', 2009, 2, 18, 100, 3),
    ('Titanic', 'A romantic drama about a ship...', 1997, 3, 12, 194, 4),
    ('The Conjuring', 'A horror film based on real events...', 2013, 4, 18, 112, 2),
    ('Mad Max: Fury Road', 'A post-apocalyptic action film...', 2015, 1, 18, 120, 3);