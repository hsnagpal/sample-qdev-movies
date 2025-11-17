package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Arrr! Test class fer the MovieService search functionality, matey!
 * These tests ensure our treasure hunting methods work like a true pirate's compass!
 */
public class MovieServiceTest {

    private MovieService movieService;

    @BeforeEach
    public void setUp() {
        // Use the real MovieService which loads from movies.json
        movieService = new MovieService();
    }

    @Test
    public void testGetAllMovies() {
        List<Movie> movies = movieService.getAllMovies();
        assertNotNull(movies);
        assertFalse(movies.isEmpty());
        // Should have 12 movies from the JSON file
        assertEquals(12, movies.size());
    }

    @Test
    public void testGetMovieById() {
        Optional<Movie> movie = movieService.getMovieById(1L);
        assertTrue(movie.isPresent());
        assertEquals("The Prison Escape", movie.get().getMovieName());
        assertEquals("John Director", movie.get().getDirector());
    }

    @Test
    public void testGetMovieByIdNotFound() {
        Optional<Movie> movie = movieService.getMovieById(999L);
        assertFalse(movie.isPresent());
    }

    @Test
    public void testGetMovieByIdInvalid() {
        Optional<Movie> movie1 = movieService.getMovieById(null);
        assertFalse(movie1.isPresent());
        
        Optional<Movie> movie2 = movieService.getMovieById(0L);
        assertFalse(movie2.isPresent());
        
        Optional<Movie> movie3 = movieService.getMovieById(-1L);
        assertFalse(movie3.isPresent());
    }

    // Arrr! Search functionality tests, ye scallywag!

    @Test
    public void testSearchMoviesWithNoParameters() {
        List<Movie> results = movieService.searchMovies(null, null, null);
        assertEquals(12, results.size()); // Should return all movies
    }

    @Test
    public void testSearchMoviesByNameExactMatch() {
        List<Movie> results = movieService.searchMovies("The Prison Escape", null, null);
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesByNamePartialMatch() {
        List<Movie> results = movieService.searchMovies("the", null, null);
        // Should find movies with "the" in the name (case-insensitive)
        assertTrue(results.size() >= 6); // At least "The Prison Escape", "The Family Boss", etc.
        
        // Verify all results contain "the" in the name
        for (Movie movie : results) {
            assertTrue(movie.getMovieName().toLowerCase().contains("the"));
        }
    }

    @Test
    public void testSearchMoviesByNameCaseInsensitive() {
        List<Movie> results1 = movieService.searchMovies("PRISON", null, null);
        List<Movie> results2 = movieService.searchMovies("prison", null, null);
        List<Movie> results3 = movieService.searchMovies("Prison", null, null);
        
        assertEquals(results1.size(), results2.size());
        assertEquals(results2.size(), results3.size());
        assertEquals(1, results1.size());
        assertEquals("The Prison Escape", results1.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesByNameNoResults() {
        List<Movie> results = movieService.searchMovies("nonexistentmovie", null, null);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchMoviesByNameEmptyString() {
        List<Movie> results1 = movieService.searchMovies("", null, null);
        List<Movie> results2 = movieService.searchMovies("   ", null, null);
        
        assertEquals(12, results1.size()); // Should return all movies
        assertEquals(12, results2.size()); // Should return all movies
    }

    @Test
    public void testSearchMoviesById() {
        List<Movie> results = movieService.searchMovies(null, 1L, null);
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesByIdNotFound() {
        List<Movie> results = movieService.searchMovies(null, 999L, null);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchMoviesByIdInvalid() {
        List<Movie> results1 = movieService.searchMovies(null, 0L, null);
        assertTrue(results1.isEmpty());
        
        List<Movie> results2 = movieService.searchMovies(null, -1L, null);
        assertTrue(results2.isEmpty());
    }

    @Test
    public void testSearchMoviesByGenreExactMatch() {
        List<Movie> results = movieService.searchMovies(null, null, "Drama");
        assertTrue(results.size() >= 1);
        
        // Verify all results have Drama in genre
        for (Movie movie : results) {
            assertTrue(movie.getGenre().toLowerCase().contains("drama"));
        }
    }

    @Test
    public void testSearchMoviesByGenrePartialMatch() {
        List<Movie> results = movieService.searchMovies(null, null, "crime");
        assertTrue(results.size() >= 2); // Should find Crime/Drama movies
        
        // Verify all results contain "crime" in genre
        for (Movie movie : results) {
            assertTrue(movie.getGenre().toLowerCase().contains("crime"));
        }
    }

    @Test
    public void testSearchMoviesByGenreCaseInsensitive() {
        List<Movie> results1 = movieService.searchMovies(null, null, "DRAMA");
        List<Movie> results2 = movieService.searchMovies(null, null, "drama");
        List<Movie> results3 = movieService.searchMovies(null, null, "Drama");
        
        assertEquals(results1.size(), results2.size());
        assertEquals(results2.size(), results3.size());
        assertTrue(results1.size() >= 1);
    }

    @Test
    public void testSearchMoviesByGenreNoResults() {
        List<Movie> results = movieService.searchMovies(null, null, "NonexistentGenre");
        assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchMoviesCombinedCriteria() {
        // Search by name and genre
        List<Movie> results = movieService.searchMovies("family", null, "crime");
        
        // Should find movies that contain "family" in name AND "crime" in genre
        for (Movie movie : results) {
            assertTrue(movie.getMovieName().toLowerCase().contains("family"));
            assertTrue(movie.getGenre().toLowerCase().contains("crime"));
        }
    }

    @Test
    public void testSearchMoviesIdOverridesOtherCriteria() {
        // When ID is provided, it should override name and genre
        List<Movie> results = movieService.searchMovies("SomeOtherName", 1L, "SomeOtherGenre");
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesByNameOnly() {
        List<Movie> results = movieService.searchMoviesByName("hero");
        assertTrue(results.size() >= 1);
        
        // Should find "The Masked Hero"
        boolean foundMaskedHero = results.stream()
                .anyMatch(movie -> movie.getMovieName().equals("The Masked Hero"));
        assertTrue(foundMaskedHero);
    }

    @Test
    public void testSearchMoviesByGenreOnly() {
        List<Movie> results = movieService.searchMoviesByGenre("Action");
        assertTrue(results.size() >= 1);
        
        // Verify all results contain "action" in genre
        for (Movie movie : results) {
            assertTrue(movie.getGenre().toLowerCase().contains("action"));
        }
    }

    @Test
    public void testGetAllGenres() {
        List<String> genres = movieService.getAllGenres();
        assertNotNull(genres);
        assertFalse(genres.isEmpty());
        
        // Should be sorted and contain unique genres
        assertTrue(genres.contains("Action/Crime"));
        assertTrue(genres.contains("Drama"));
        assertTrue(genres.contains("Crime/Drama"));
        
        // Verify sorted order
        for (int i = 1; i < genres.size(); i++) {
            assertTrue(genres.get(i - 1).compareTo(genres.get(i)) <= 0);
        }
        
        // Verify uniqueness
        long uniqueCount = genres.stream().distinct().count();
        assertEquals(genres.size(), uniqueCount);
    }

    @Test
    public void testSearchMoviesWithWhitespaceHandling() {
        // Test that leading/trailing whitespace is handled properly
        List<Movie> results1 = movieService.searchMovies("  prison  ", null, null);
        List<Movie> results2 = movieService.searchMovies("prison", null, null);
        
        assertEquals(results1.size(), results2.size());
        assertEquals(1, results1.size());
        
        List<Movie> results3 = movieService.searchMovies(null, null, "  drama  ");
        List<Movie> results4 = movieService.searchMovies(null, null, "drama");
        
        assertEquals(results3.size(), results4.size());
    }

    @Test
    public void testSearchMoviesPerformance() {
        // Simple performance test - search should complete quickly
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < 100; i++) {
            movieService.searchMovies("the", null, null);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // Should complete 100 searches in less than 1 second
        assertTrue(duration < 1000, "Search operations took too long: " + duration + "ms");
    }
}