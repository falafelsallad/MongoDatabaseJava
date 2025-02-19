import org.bson.Document;
import org.example.Queries;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class QueriesTest {

    @Test
    void testQ1countMoviesFrom1975() {
        List<Document> movies = Arrays.asList(
                new Document("year", 1975),
                new Document("year", 1975),
                new Document("year", 1975)
        );
        assertEquals(3, Queries.Q1countMoviesFrom1975(movies));
        assertEquals(0, Queries.Q1countMoviesFrom1975(Collections.emptyList()));

    }

    @Test
    void testQ2FindLongestMovieRuntime() {
        List<Document> movies = Arrays.asList(
                new Document("runtime", 100),
                new Document("runtime", 120),
                new Document("runtime", 90)
        );
        assertEquals(120, Queries.Q2findLongestMovieRuntime(movies));
        assertEquals(0, Queries.Q2findLongestMovieRuntime(Collections.emptyList()));
    }

    @Test
    void testQ3CountUniqueGenres1975() {
        List<Document> movies = Arrays.asList(
                new Document("genres", Arrays.asList("Action", "Drama")),
                new Document("genres", Arrays.asList("Action", "Comedy")),
                new Document("genres", Arrays.asList("Drama", "Comedy"))
        );
        assertEquals(3, Queries.Q3countUniqueGenres1975(movies));
        assertEquals(0, Queries.Q3countUniqueGenres1975(Collections.emptyList()));
    }

    @Test
    void testQ4FindTopRatedMovieActors() {
        List<Document> movies = Arrays.asList(
                new Document("imdb", new Document("rating", 8.5)).append("cast", Arrays.asList("Actor 1", "Actor 2")),
                new Document("imdb", new Document("rating", 7.5)).append("cast", Arrays.asList("Actor 3", "Actor 4")),
                new Document("imdb", new Document("rating", 9.5)).append("cast", Arrays.asList("Actor 5", "Actor 6"))
        );
        assertEquals(Arrays.asList("Actor 5", "Actor 6"), Queries.Q4findTopRatedMovieActors(movies));
        assertEquals(Collections.emptyList(), Queries.Q4findTopRatedMovieActors(Collections.emptyList()));
    }

    @Test
    void testQ5FindMovieWithLeastActors() {
        List<Document> movies = Arrays.asList(
                new Document("title", "Movie 1").append("cast", Arrays.asList("Actor 1", "Actor 2", "Actor 3", "Actor 4")),
                new Document("title", "Movie 2").append("cast", Arrays.asList("Actor 3", "Actor 4", "Actor 5")),
                new Document("title", "Movie 3").append("cast", List.of("Actor 5"))
        );
        assertEquals("Movie 3", Queries.Q5findMovieWithLeastActors(movies));
        assertEquals("No movie found", Queries.Q5findMovieWithLeastActors(Collections.emptyList()));
    }

    @Test
    void testQ6ActorsInMoreThanOneMovie() {
        List<Document> movies = Arrays.asList(
                new Document("cast", Arrays.asList("2", "3", "ACTOR A", "5")),
                new Document("cast", Arrays.asList("ACTOR A", "ACTOR B", "7")),
                new Document("cast", List.of("ACTOR B", "13", "17"))
        );
        assertEquals(2, Queries.Q6actorsInMoreThanOneMovie(movies));
        assertEquals(0, Queries.Q6actorsInMoreThanOneMovie(Collections.emptyList()));
    }

    @Test
    void testQ7ActorInMostMovies() {
        List<Document> movies = Arrays.asList(
                new Document("cast", Arrays.asList("2", "3", "ACTOR B", "5")),    //B CUS IT APPEARS FIRST!
                new Document("cast", Arrays.asList("ACTOR B", "ACTOR A", "7")),
                new Document("cast", Arrays.asList("ACTOR A", "13", "17")),
                new Document("cast", Arrays.asList("19", "23", "ACTOR B", "ACTOR  A"))
        );
        assertEquals("ACTOR B", Queries.Q7actorInMostMovies(movies));
        assertEquals("No actor found", Queries.Q7actorInMostMovies(Collections.emptyList()));
    }

    @Test
    void testQ8CountUniqueLanguages() {
        List<Document> movies = Arrays.asList(
                new Document("languages", Arrays.asList("English", "Spanish")),
                new Document("languages", Arrays.asList("English", "French")),
                new Document("languages", Arrays.asList("Spanish", "French"))
        );
        assertEquals(3, Queries.Q8countUniqueLanguages(movies));
        assertNotEquals(2, Queries.Q8countUniqueLanguages(movies));
        assertEquals(0, Queries.Q8countUniqueLanguages(Collections.emptyList()));
    }

    @Test
    void testQ9HasDuplicateTitles() {
        List<Document> movies = Arrays.asList(
                new Document("title", "Movie 1"),
                new Document("title", "Movie 2"),
                new Document("title", "Movie 1")
        );
        assertTrue(Queries.Q9hasDuplicateTitles(movies));
        assertNotEquals(false, Queries.Q9hasDuplicateTitles(movies));
        assertFalse(Queries.Q9hasDuplicateTitles(Collections.emptyList()));
    }


}
