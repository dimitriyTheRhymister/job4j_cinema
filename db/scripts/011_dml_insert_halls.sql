--liquibase formatted sql

--changeset author:job4j id:011
INSERT INTO halls (name, row_count, place_count, description) VALUES
    ('Red Hall', 10, 100, 'Main hall with best sound system'),
    ('Blue Hall', 8, 80, 'VIP hall with luxury seats'),
    ('Green Hall', 6, 60, 'Small hall for indie movies'),
    ('Gold Hall', 12, 120, 'Premium IMAX experience'),
    ('Silver Hall', 7, 70, 'Standard hall with good acoustics');