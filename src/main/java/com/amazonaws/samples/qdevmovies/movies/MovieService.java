package com.amazonaws.samples.qdevmovies.movies;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

@Service
public class MovieService {
    private static final Logger logger = LogManager.getLogger(MovieService.class);
    private final List<Movie> movies;
    private final Map<Long, Movie> movieMap;

    public MovieService() {
        this.movies = loadMoviesFromJson();
        this.movieMap = new HashMap<>();
        for (Movie movie : movies) {
            movieMap.put(movie.getId(), movie);
        }
    }

    private List<Movie> loadMoviesFromJson() {
        List<Movie> movieList = new ArrayList<>();
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("movies.json");
            if (inputStream != null) {
                Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name());
                String jsonContent = scanner.useDelimiter("\\A").next();
                scanner.close();
                
                JSONArray moviesArray = new JSONArray(jsonContent);
                for (int i = 0; i < moviesArray.length(); i++) {
                    JSONObject movieObj = moviesArray.getJSONObject(i);
                    movieList.add(new Movie(
                        movieObj.getLong("id"),
                        movieObj.getString("movieName"),
                        movieObj.getString("director"),
                        movieObj.getInt("year"),
                        movieObj.getString("genre"),
                        movieObj.getString("description"),
                        movieObj.getInt("duration"),
                        movieObj.getDouble("imdbRating")
                    ));
                }
            }
        } catch (Exception e) {
            logger.error("Failed to load movies from JSON: {}", e.getMessage());
        }
        return movieList;
    }

    public List<Movie> getAllMovies() {
        return movies;
    }

    public Optional<Movie> getMovieById(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return Optional.ofNullable(movieMap.get(id));
    }

    /**
     * Ahoy matey! Search fer movies by various criteria like a true pirate huntin' fer treasure!
     * 
     * @param name The movie name to search fer (case-insensitive partial match)
     * @param id The specific movie ID to find
     * @param genre The genre to filter by (case-insensitive partial match)
     * @return List of movies matchin' yer search criteria, or empty list if no treasure be found
     */
    public List<Movie> searchMovies(String name, Long id, String genre) {
        logger.info("Arrr! Searchin' fer movies with name: {}, id: {}, genre: {}", name, id, genre);
        
        List<Movie> results = new ArrayList<>(movies);
        
        // Filter by ID first - if ID is provided, return only that movie (or empty list)
        if (id != null && id > 0) {
            Optional<Movie> movieById = getMovieById(id);
            if (movieById.isPresent()) {
                results = List.of(movieById.get());
                logger.info("Shiver me timbers! Found movie by ID: {}", id);
            } else {
                logger.warn("Blimey! No movie found with ID: {}", id);
                return new ArrayList<>();
            }
        }
        
        // Filter by name if provided
        if (name != null && !name.trim().isEmpty()) {
            String searchName = name.trim().toLowerCase();
            results = results.stream()
                    .filter(movie -> movie.getMovieName().toLowerCase().contains(searchName))
                    .collect(ArrayList::new, (list, movie) -> list.add(movie), ArrayList::addAll);
            logger.info("Filtered by name '{}', found {} movies", name, results.size());
        }
        
        // Filter by genre if provided
        if (genre != null && !genre.trim().isEmpty()) {
            String searchGenre = genre.trim().toLowerCase();
            results = results.stream()
                    .filter(movie -> movie.getGenre().toLowerCase().contains(searchGenre))
                    .collect(ArrayList::new, (list, movie) -> list.add(movie), ArrayList::addAll);
            logger.info("Filtered by genre '{}', found {} movies", genre, results.size());
        }
        
        logger.info("Arrr! Search complete! Found {} movies in total", results.size());
        return results;
    }

    /**
     * Search fer movies by name only, ye scallywag!
     * 
     * @param name The movie name to search fer
     * @return List of movies with matchin' names
     */
    public List<Movie> searchMoviesByName(String name) {
        return searchMovies(name, null, null);
    }

    /**
     * Search fer movies by genre only, me hearty!
     * 
     * @param genre The genre to filter by
     * @return List of movies in the specified genre
     */
    public List<Movie> searchMoviesByGenre(String genre) {
        return searchMovies(null, null, genre);
    }

    /**
     * Get all available genres from our treasure chest of movies!
     * 
     * @return List of unique genres found in the movie collection
     */
    public List<String> getAllGenres() {
        return movies.stream()
                .map(Movie::getGenre)
                .distinct()
                .sorted()
                .collect(ArrayList::new, (list, genre) -> list.add(genre), ArrayList::addAll);
    }
}
