# 🏴‍☠️ Pirate Cinema - Movie Service Spring Boot Demo Application

Ahoy matey! Welcome to the Pirate Cinema - a swashbuckling movie catalog web application built with Spring Boot, demonstrating Java application development best practices with a pirate's flair!

## ⚓ Features

- **🎬 Movie Treasure Chest**: Browse 12 classic movies with detailed information
- **🔍 Movie Search & Filtering**: Hunt fer yer favorite movies by name, ID, or genre like a true pirate!
- **📋 Movie Details**: View comprehensive information including captain (director), year of discovery, genre, duration, and description
- **⭐ Customer Reviews**: Each movie includes authentic customer reviews with ratings and avatars
- **📱 Responsive Design**: Mobile-first design that works on all devices, from ship to shore
- **🎨 Modern Pirate UI**: Dark theme with gradient backgrounds, smooth animations, and pirate language throughout
- **🗺️ Search Form**: Interactive search interface with pirate-themed labels and messages

## 🛠️ Technology Stack

- **☕ Java 8**
- **🚀 Spring Boot 2.0.5**
- **📦 Maven** for dependency management
- **📝 Log4j 2.20.0**
- **🧪 JUnit 5.8.2**
- **🎭 Thymeleaf** for server-side templating
- **🎨 CSS3** with pirate-themed styling

## 🚀 Quick Start

### Prerequisites

- Java 8 or higher
- Maven 3.6+

### Run the Application

```bash
git clone https://github.com/<youruser>/sample-qdev-movies.git
cd sample-qdev-movies
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Access the Pirate Cinema

- **🏴‍☠️ Movie Treasure Chest**: http://localhost:8080/movies
- **🔍 Movie Search**: http://localhost:8080/movies/search
- **📋 Movie Details**: http://localhost:8080/movies/{id}/details (where {id} is 1-12)

## 🏗️ Building for Production

```bash
mvn clean package
java -jar target/sample-qdev-movies-0.1.0.jar
```

## 📁 Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/amazonaws/samples/qdevmovies/
│   │       ├── movies/
│   │       │   ├── MoviesApplication.java    # Main Spring Boot application
│   │       │   ├── MoviesController.java     # REST controller with search endpoints
│   │       │   ├── MovieService.java         # Service layer with search functionality
│   │       │   ├── Movie.java                # Movie data model
│   │       │   ├── Review.java               # Review data model
│   │       │   └── ReviewService.java        # Review service
│   │       └── utils/
│   │           ├── MovieIconUtils.java       # Movie icon utilities
│   │           └── MovieUtils.java           # Movie validation utilities
│   └── resources/
│       ├── application.yml                   # Application configuration
│       ├── movies.json                       # Movie treasure data
│       ├── mock-reviews.json                 # Mock review data
│       ├── log4j2.xml                        # Logging configuration
│       ├── templates/
│       │   ├── movies.html                   # Main movie listing with search form
│       │   └── movie-details.html            # Movie details page
│       └── static/css/
│           └── movies.css                    # Pirate-themed styling
└── test/                                     # Comprehensive unit tests
    └── java/
        └── com/amazonaws/samples/qdevmovies/movies/
            ├── MoviesControllerTest.java     # Controller tests with search functionality
            ├── MovieServiceTest.java         # Service layer tests
            └── MovieTest.java                # Model tests
```

## 🗺️ API Endpoints

### Get All Movies (Treasure Chest)
```
GET /movies
```
Returns an HTML page displaying all movies with ratings, basic information, and a search form.

### 🔍 Search Movies (NEW! Arrr!)
```
GET /movies/search
```
**Ahoy! This be the new treasure hunting endpoint, matey!**

Search fer movies using various criteria. Supports both HTML and JSON responses.

**Query Parameters:**
- `name` (optional): Movie name to search fer (case-insensitive partial match)
- `id` (optional): Specific movie ID to find (exact match, 1-12)
- `genre` (optional): Genre to filter by (case-insensitive partial match)

**HTML Response Examples:**
```bash
# Search by movie name (partial match)
http://localhost:8080/movies/search?name=pirate

# Search by specific ID
http://localhost:8080/movies/search?id=1

# Search by genre
http://localhost:8080/movies/search?genre=drama

# Combined search (name + genre)
http://localhost:8080/movies/search?name=family&genre=crime

# Show search form (no parameters)
http://localhost:8080/movies/search
```

**JSON API Response:**
Add `Accept: application/json` header or access with JSON client:
```bash
curl -H "Accept: application/json" "http://localhost:8080/movies/search?name=pirate"
```

**JSON Response Format:**
```json
{
  "success": true,
  "count": 2,
  "message": "Shiver me timbers! Found 2 movies matchin' yer search!",
  "movies": [...],
  "searchCriteria": {
    "name": "pirate",
    "id": "",
    "genre": ""
  }
}
```

### Get Movie Details
```
GET /movies/{id}/details
```
Returns an HTML page with detailed movie information and customer reviews.

**Parameters:**
- `id` (path parameter): Movie ID (1-12)

**Example:**
```
http://localhost:8080/movies/1/details
```

## 🎯 Search Features

### 🔍 Search Capabilities
- **Name Search**: Case-insensitive partial matching (e.g., "pirate" finds "The Pirate's Treasure")
- **ID Search**: Exact match by movie ID (overrides other criteria when provided)
- **Genre Search**: Case-insensitive partial matching (e.g., "drama" finds "Crime/Drama")
- **Combined Search**: Mix name and genre criteria (ID takes precedence)
- **Empty Search**: Shows all movies with helpful message

### 🏴‍☠️ Pirate Language Features
- **Search Messages**: "Shiver me timbers! Found X movies matchin' yer search!"
- **No Results**: "Blimey! No movies found matchin' yer search criteria, ye scallywag!"
- **Form Labels**: Pirate-themed input labels and placeholders
- **Error Handling**: "Batten down the hatches!" error messages
- **UI Text**: Consistent pirate language throughout the interface

### 🎨 Available Genres in Our Treasure Chest
- Action/Crime
- Action/Sci-Fi  
- Adventure/Fantasy
- Adventure/Sci-Fi
- Crime/Drama
- Drama
- Drama/History
- Drama/Romance
- Drama/Thriller

## 🧪 Testing

Run the comprehensive test suite:
```bash
mvn test
```

**Test Coverage:**
- **MovieServiceTest**: 20+ tests covering all search functionality
- **MoviesControllerTest**: 15+ tests for both HTML and JSON endpoints
- **Edge Cases**: Invalid parameters, empty results, performance tests
- **Pirate Language**: Validation of pirate-themed messages and responses

## 🔧 Troubleshooting

### Port 8080 already in use

Run on a different port:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

### Build failures

Clean and rebuild:
```bash
mvn clean compile
```

### Search not working

1. Verify the application started successfully
2. Check that movies.json is loaded (should see 12 movies)
3. Try different search terms (case-insensitive)
4. Check browser console for any JavaScript errors

## 🤝 Contributing

Ahoy! This project welcomes contributions from fellow pirates! Feel free to:
- Add more movies to the treasure chest
- Enhance the pirate UI/UX with more nautical themes
- Improve search functionality (fuzzy matching, advanced filters)
- Add more pirate language and personality
- Enhance the responsive design for mobile devices
- Add new features like movie ratings or favorites

## 📜 License

This sample code is licensed under the MIT-0 License. See the LICENSE file.

---

**🏴‍☠️ Arrr! Welcome to the Pirate Cinema - where every movie be a treasure worth discoverin'! ⚓**
