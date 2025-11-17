package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.ui.ExtendedModelMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class MoviesControllerTest {

    private MoviesController moviesController;
    private Model model;
    private MovieService mockMovieService;
    private ReviewService mockReviewService;

    @BeforeEach
    public void setUp() {
        moviesController = new MoviesController();
        model = new ExtendedModelMap();
        
        // Create mock services with pirate-themed test data
        mockMovieService = new MovieService() {
            private final List<Movie> testMovies = Arrays.asList(
                new Movie(1L, "The Pirate's Treasure", "Captain Hook", 2023, "Adventure", "A swashbuckling adventure", 120, 4.5),
                new Movie(2L, "Ocean's Mystery", "Blackbeard", 2022, "Drama", "A mysterious tale of the seas", 110, 4.0),
                new Movie(3L, "Space Pirates", "Captain Nebula", 2021, "Sci-Fi", "Pirates in space!", 130, 3.5)
            );
            
            @Override
            public List<Movie> getAllMovies() {
                return testMovies;
            }
            
            @Override
            public Optional<Movie> getMovieById(Long id) {
                return testMovies.stream().filter(m -> m.getId() == id).findFirst();
            }
            
            @Override
            public List<Movie> searchMovies(String name, Long id, String genre) {
                List<Movie> results = new ArrayList<>(testMovies);
                
                if (id != null && id > 0) {
                    Optional<Movie> movieById = getMovieById(id);
                    return movieById.map(List::of).orElse(new ArrayList<>());
                }
                
                if (name != null && !name.trim().isEmpty()) {
                    String searchName = name.trim().toLowerCase();
                    results = results.stream()
                            .filter(movie -> movie.getMovieName().toLowerCase().contains(searchName))
                            .collect(ArrayList::new, (list, movie) -> list.add(movie), ArrayList::addAll);
                }
                
                if (genre != null && !genre.trim().isEmpty()) {
                    String searchGenre = genre.trim().toLowerCase();
                    results = results.stream()
                            .filter(movie -> movie.getGenre().toLowerCase().contains(searchGenre))
                            .collect(ArrayList::new, (list, movie) -> list.add(movie), ArrayList::addAll);
                }
                
                return results;
            }
            
            @Override
            public List<Movie> searchMoviesByName(String name) {
                return searchMovies(name, null, null);
            }
            
            @Override
            public List<Movie> searchMoviesByGenre(String genre) {
                return searchMovies(null, null, genre);
            }
            
            @Override
            public List<String> getAllGenres() {
                return Arrays.asList("Adventure", "Drama", "Sci-Fi");
            }
        };
        
        mockReviewService = new ReviewService() {
            @Override
            public List<Review> getReviewsForMovie(long movieId) {
                return new ArrayList<>();
            }
        };
        
        // Inject mocks using reflection
        try {
            java.lang.reflect.Field movieServiceField = MoviesController.class.getDeclaredField("movieService");
            movieServiceField.setAccessible(true);
            movieServiceField.set(moviesController, mockMovieService);
            
            java.lang.reflect.Field reviewServiceField = MoviesController.class.getDeclaredField("reviewService");
            reviewServiceField.setAccessible(true);
            reviewServiceField.set(moviesController, mockReviewService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock services", e);
        }
    }

    @Test
    public void testGetMovies() {
        String result = moviesController.getMovies(model);
        assertNotNull(result);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertNotNull(movies);
        assertEquals(3, movies.size());
    }

    @Test
    public void testGetMovieDetails() {
        String result = moviesController.getMovieDetails(1L, model);
        assertNotNull(result);
        assertEquals("movie-details", result);
        
        Movie movie = (Movie) model.getAttribute("movie");
        assertNotNull(movie);
        assertEquals("The Pirate's Treasure", movie.getMovieName());
    }

    @Test
    public void testGetMovieDetailsNotFound() {
        String result = moviesController.getMovieDetails(999L, model);
        assertNotNull(result);
        assertEquals("error", result);
        
        String title = (String) model.getAttribute("title");
        assertEquals("Movie Not Found", title);
    }

    // Arrr! New search functionality tests, matey!
    
    @Test
    public void testSearchMoviesWithNoParameters() {
        String result = moviesController.searchMovies(null, null, null, model);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(3, movies.size()); // Should return all movies
        
        String searchMessage = (String) model.getAttribute("searchMessage");
        assertTrue(searchMessage.contains("Ahoy! Use the search form"));
        
        Boolean isSearchResult = (Boolean) model.getAttribute("isSearchResult");
        assertTrue(isSearchResult);
    }
    
    @Test
    public void testSearchMoviesByName() {
        String result = moviesController.searchMovies("pirate", null, null, model);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(2, movies.size()); // "The Pirate's Treasure" and "Space Pirates"
        
        String searchMessage = (String) model.getAttribute("searchMessage");
        assertTrue(searchMessage.contains("Shiver me timbers! Found 2 movies"));
        
        String searchName = (String) model.getAttribute("searchName");
        assertEquals("pirate", searchName);
    }
    
    @Test
    public void testSearchMoviesById() {
        String result = moviesController.searchMovies(null, 2L, null, model);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals("Ocean's Mystery", movies.get(0).getMovieName());
        
        String searchMessage = (String) model.getAttribute("searchMessage");
        assertTrue(searchMessage.contains("Shiver me timbers! Found 1 movie"));
    }
    
    @Test
    public void testSearchMoviesByGenre() {
        String result = moviesController.searchMovies(null, null, "drama", model);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals("Ocean's Mystery", movies.get(0).getMovieName());
        
        String searchGenre = (String) model.getAttribute("searchGenre");
        assertEquals("drama", searchGenre);
    }
    
    @Test
    public void testSearchMoviesWithInvalidId() {
        String result = moviesController.searchMovies(null, 999L, null, model);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(0, movies.size());
        
        String searchMessage = (String) model.getAttribute("searchMessage");
        assertTrue(searchMessage.contains("Blimey! No movies found"));
    }
    
    @Test
    public void testSearchMoviesWithNoResults() {
        String result = moviesController.searchMovies("nonexistent", null, null, model);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(0, movies.size());
        
        String searchMessage = (String) model.getAttribute("searchMessage");
        assertTrue(searchMessage.contains("Blimey! No movies found"));
    }
    
    @Test
    public void testSearchMoviesCombinedCriteria() {
        String result = moviesController.searchMovies("pirate", null, "adventure", model);
        assertEquals("movies", result);
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertEquals(1, movies.size());
        assertEquals("The Pirate's Treasure", movies.get(0).getMovieName());
    }
    
    // Arrr! JSON API tests, ye scallywag!
    
    @Test
    public void testSearchMoviesJsonSuccess() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMoviesJson("pirate", null, null);
        
        assertEquals(200, response.getStatusCodeValue());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertTrue((Boolean) body.get("success"));
        assertEquals(2, body.get("count"));
        
        String message = (String) body.get("message");
        assertTrue(message.contains("Shiver me timbers! Found 2 movies"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) body.get("movies");
        assertEquals(2, movies.size());
    }
    
    @Test
    public void testSearchMoviesJsonNoResults() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMoviesJson("nonexistent", null, null);
        
        assertEquals(200, response.getStatusCodeValue());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertTrue((Boolean) body.get("success"));
        assertEquals(0, body.get("count"));
        
        String message = (String) body.get("message");
        assertTrue(message.contains("Blimey! No movies found"));
    }
    
    @Test
    public void testSearchMoviesJsonById() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMoviesJson(null, 1L, null);
        
        assertEquals(200, response.getStatusCodeValue());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertTrue((Boolean) body.get("success"));
        assertEquals(1, body.get("count"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) body.get("movies");
        assertEquals("The Pirate's Treasure", movies.get(0).getMovieName());
    }
    
    @Test
    public void testSearchMoviesJsonNoParameters() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMoviesJson(null, null, null);
        
        assertEquals(200, response.getStatusCodeValue());
        
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertTrue((Boolean) body.get("success"));
        assertEquals(3, body.get("count")); // All movies
        
        String message = (String) body.get("message");
        assertTrue(message.contains("Shiver me timbers! Found 3 movies"));
    }

    @Test
    public void testMovieServiceIntegration() {
        List<Movie> movies = mockMovieService.getAllMovies();
        assertEquals(3, movies.size());
        assertEquals("The Pirate's Treasure", movies.get(0).getMovieName());
        
        // Test search functionality
        List<Movie> searchResults = mockMovieService.searchMoviesByName("pirate");
        assertEquals(2, searchResults.size());
        
        List<Movie> genreResults = mockMovieService.searchMoviesByGenre("Drama");
        assertEquals(1, genreResults.size());
        assertEquals("Ocean's Mystery", genreResults.get(0).getMovieName());
        
        List<String> genres = mockMovieService.getAllGenres();
        assertEquals(3, genres.size());
        assertTrue(genres.contains("Adventure"));
    }
}