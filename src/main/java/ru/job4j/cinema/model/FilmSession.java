package ru.job4j.cinema.model;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

public class FilmSession {

    public static final Map<String, String> COLUMN_MAPPING = Map.of(
            "id", "id",
            "film_id", "filmId",
            "halls_id", "hallId",
            "start_time", "startTime",
            "end_time", "endTime",
            "price", "price"
    );

    private int id;
    private int filmId;
    private int hallId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer price;

    private Film film;

    public FilmSession() {
    }

    public FilmSession(int id, int filmId, int hallId, LocalDateTime startTime, LocalDateTime endTime, int price) {
        this.id = id;
        this.filmId = filmId;
        this.hallId = hallId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
    }

    public FilmSession(int id, int filmId, int hallId, LocalDateTime startTime, LocalDateTime endTime, int price, Film film) {
        this.id = id;
        this.filmId = filmId;
        this.hallId = hallId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
        this.film = film;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFilmId() {
        return filmId;
    }

    public void setFilmId(int filmId) {
        this.filmId = filmId;
    }

    public int getHallId() {
        return hallId;
    }

    public void setHallId(int hallId) {
        this.hallId = hallId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Film getFilm() {
        return film;
    }

    public void setFilm(Film film) {
        this.film = film;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FilmSession that = (FilmSession) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}