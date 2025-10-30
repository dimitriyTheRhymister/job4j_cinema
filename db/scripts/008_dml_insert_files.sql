--liquibase formatted sql

--changeset author:job4j id:008
-- Убираем начальный '/' в path, чтобы избежать ошибки NoSuchFileException на Windows
INSERT INTO files (name, path) VALUES
    ('avatar.jpg', 'files/images/avatar.jpg'),
    ('poster1.jpg', 'files/images/poster1.jpg'),
    ('poster2.jpg', 'files/images/poster2.jpg'),
    ('poster3.jpg', 'files/images/poster3.jpg'),
    ('logo.png', 'files/images/logo.png');