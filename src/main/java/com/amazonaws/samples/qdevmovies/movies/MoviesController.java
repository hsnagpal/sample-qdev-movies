package com.amazonaws.samples.qdevmovies.movies;

import com.amazonaws.samples.qdevmovies.utils.MovieIconUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class MoviesController {
    private static final Logger logger = LogManager.getLogger(MoviesController.class);

    @Autowired
    private MovieService movieService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/movies")
    public String getMovies(org.springframework.ui.Model model) {
        logger.info("Fetching movies");
        model.addAttribute("movies", movieService.getAllMovies());
        model.addAttribute("allGenres", movieService.getAllGenres());
        model.addAttribute("searchName", "");
        model.addAttribute("searchId", "");
        model.addAttribute("searchGenre", "");
        model.addAttribute("isSearchResult", false);
        return "movies";
    }

    @GetMapping("/movies/{id}/details")
    public String getMovieDetails(@PathVariable("id") Long movieId, org.springframework.ui.Model model) {
        logger.info("Fetching details for movie ID: {}", movieId);
        
        Optional<Movie> movieOpt = movieService.getMovieById(movieId);
        if (!movieOpt.isPresent()) {
            logger.warn("Movie with ID {} not found", movieId);
            model.addAttribute("title", "Movie Not Found");
            model.addAttribute("message", "Movie with ID " + movieId + " was not found.");
            return "error";
        }
        
        Movie movie = movieOpt.get();
        model.addAttribute("movie", movie);
        model.addAttribute("movieIcon", MovieIconUtils.getMovieIcon(movie.getMovieName()));
        model.addAttribute("allReviews", reviewService.getReviewsForMovie(movie.getId()));
        
        return "movie-details";
    }

    /**
     * Ahoy matey! Search fer movies like a true pirate huntin' fer treasure!
     * This endpoint accepts query parameters and returns HTML with search results.
     * 
     * @param name Movie name to search fer (optional)
     * @param id Specific movie ID to find (optional)
     * @param genre Genre to filter by (optional)
     * @param model Spring model fer template rendering
     * @return Template name with search results
     */
    @GetMapping("/movies/search")
    public String searchMovies(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "genre", required = false) String genre,
            org.springframework.ui.Model model) {
        
        logger.info("Arrr! Searchin' fer movies with name: {}, id: {}, genre: {}", name, id, genre);
        
        // Perform the search
        List<Movie> searchResults = movieService.searchMovies(name, id, genre);
        
        // Add search results and parameters to model
        model.addAttribute("movies", searchResults);
        model.addAttribute("searchName", name != null ? name : "");
        model.addAttribute("searchId", id != null ? id.toString() : "");
        model.addAttribute("searchGenre", genre != null ? genre : "");
        model.addAttribute("allGenres", movieService.getAllGenres());
        model.addAttribute("isSearchResult", true);
        
        // Add pirate messages based on results
        if (searchResults.isEmpty()) {
            if (name == null && id == null && genre == null) {
                model.addAttribute("searchMessage", "Ahoy! Use the search form below to find yer treasure movies, matey!");
            } else {
                model.addAttribute("searchMessage", "Blimey! No movies found matchin' yer search criteria. Try different terms, ye scallywag!");
            }
        } else {
            model.addAttribute("searchMessage", 
                String.format("Shiver me timbers! Found %d movie%s matchin' yer search!", 
                    searchResults.size(), searchResults.size() == 1 ? "" : "s"));
        }
        
        logger.info("Search completed. Found {} movies", searchResults.size());
        return "movies";
    }

    /**
     * Arrr! JSON API endpoint fer searchin' movies - perfect fer other pirates' applications!
     * 
     * @param name Movie name to search fer (optional)
     * @param id Specific movie ID to find (optional)
     * @param genre Genre to filter by (optional)
     * @return JSON response with search results and pirate messages
     */
    @GetMapping(value = "/movies/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> searchMoviesJson(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "genre", required = false) String genre) {
        
        logger.info("Arrr! JSON search request with name: {}, id: {}, genre: {}", name, id, genre);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Movie> searchResults = movieService.searchMovies(name, id, genre);
            
            response.put("movies", searchResults);
            response.put("count", searchResults.size());
            response.put("searchCriteria", Map.of(
                "name", name != null ? name : "",
                "id", id != null ? id.toString() : "",
                "genre", genre != null ? genre : ""
            ));
            
            // Add pirate messages
            if (searchResults.isEmpty()) {
                if (name == null && id == null && genre == null) {
                    response.put("message", "Ahoy! Provide search criteria to find yer treasure movies!");
                } else {
                    response.put("message", "Blimey! No movies found matchin' yer search criteria, matey!");
                }
            } else {
                response.put("message", 
                    String.format("Shiver me timbers! Found %d movie%s in yer search!", 
                        searchResults.size(), searchResults.size() == 1 ? "" : "s"));
            }
            
            response.put("success", true);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Arrr! Error during movie search: {}", e.getMessage());
            response.put("success", false);
            response.put("message", "Batten down the hatches! Something went wrong with yer search: " + e.getMessage());
            response.put("movies", List.of());
            response.put("count", 0);
            return ResponseEntity.internalServerError().body(response);
        }
    }
}