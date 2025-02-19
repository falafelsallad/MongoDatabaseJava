
import com.mongodb.Function;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import net.bytebuddy.utility.dispatcher.JavaDispatcher;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.example.Queries;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.testcontainers.containers.MongoDBContainer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class QueriesTest {

    @Test
    void convertsCollectionToList(){

            MongoCollection<Document> collection = mock(MongoCollection.class);
            FindIterable<Document> findIterable = mock(FindIterable.class);

            when(collection.find((Bson) any())).thenReturn(findIterable);
            when(findIterable.into(anyList())).thenAnswer((Answer<List<Document>>) invocation -> {
                List<Document> list = invocation.getArgument(0);
                list.add(new Document("title", "Movie 1").append("year", 1975));
                list.add(new Document("title", "Movie 2").append("year", 1975));
                return list;
            });

            List<Document> movies = Queries.fetchMovies(collection);

            assertNotNull(movies);
            assertEquals(2, movies.size());
            assertEquals("Movie 1", movies.get(0).getString("title"));
            assertEquals("Movie 2", movies.get(1).getString("title"));

    }

    MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.6");

    @Test
    void testFetchMoviesFrom1975withContainer() {

        try (var mongoClient = MongoClients.create(mongoDBContainer.getReplicaSetUrl())) {
            MongoDatabase database = mongoClient.getDatabase("testdb");
            MongoCollection<Document> collection = database.getCollection("moviestest");


            collection.insertMany(List.of(
                    new Document("title", "Movie 1").append("year", 1975),
                    new Document("title", "Movie 2").append("year", 1975),
                    new Document("title", "Movie 3").append("year", 1976)
            ));

            List<Document> movies = Queries.fetchMovies(collection);

            assertEquals(2, movies.size());
            assertTrue(movies.stream().allMatch(doc -> doc.getInteger("year") == 1975));

        }
    }


    @Test
    void testQ1CountsAllMovies1975(){
        List<Document> movies = Arrays.asList(
                new Document("year", 1975),
                new Document("year", 1975),
                new Document("year", 1975)
        );
        assertEquals(3, Queries.countMoviesFrom1975(movies));
        assertEquals(0, Queries.countMoviesFrom1975(Collections.emptyList()));

    }



    @Test
    void testQ2FindLongestMovieRuntime(){
        List<Document> movies = Arrays.asList(
                new Document("runtime", 100),
                new Document("runtime", 120),
                new Document("runtime", 90)
        );
        assertEquals(120, Queries.findLongestMovieRuntime(movies));
        assertEquals(0, Queries.findLongestMovieRuntime(Collections.emptyList()));
    }


    @Test
    void testQ3CountUniqueGenres1975(){
        List<Document> movies = Arrays.asList(
                new Document("genres", Arrays.asList("Action", "Drama")),
                new Document("genres", Arrays.asList("Action", "Comedy")),
                new Document("genres", Arrays.asList("Drama", "Comedy"))
        );
        assertEquals(3, Queries.countUniqueGenres1975(movies));
        assertEquals(0, Queries.countUniqueGenres1975(Collections.emptyList()));
    }

    @Test
    void testQ4FindTopRatedMovieActors(){
        List<Document> movies = Arrays.asList(
                new Document("imdb", new Document("rating", 8.5)).append("cast", Arrays.asList("Actor 1", "Actor 2")),
                new Document("imdb", new Document("rating", 7.5)).append("cast", Arrays.asList("Actor 3", "Actor 4")),
                new Document("imdb", new Document("rating", 9.5)).append("cast", Arrays.asList("Actor 5", "Actor 6"))
        );
        assertEquals(Arrays.asList("Actor 5", "Actor 6"), Queries.findTopRatedMovieActors(movies));
        assertEquals(Collections.emptyList(), Queries.findTopRatedMovieActors(Collections.emptyList()));
    }

    @Test
    void testQ5FindMovieWithLeastActors(){
        List<Document> movies = Arrays.asList(
                new Document("title", "Movie 1").append("cast", Arrays.asList("Actor 1", "Actor 2", "Actor 3", "Actor 4")),
                new Document("title", "Movie 2").append("cast", Arrays.asList("Actor 3", "Actor 4", "Actor 5")),
                new Document("title", "Movie 3").append("cast", List.of("Actor 5"))
        );
        assertEquals("Movie 3", Queries.findMovieWithLeastActors(movies));
        assertEquals("No movie found", Queries.findMovieWithLeastActors(Collections.emptyList()));
    }

    @Test
    void testQ6ActorsInMoreThanOneMovie(){
        List<Document> movies = Arrays.asList(
                new Document("cast", Arrays.asList("2", "3", "ACTOR A", "5")),
                new Document("cast", Arrays.asList("ACTOR A", "ACTOR B", "7")),
                new Document("cast", List.of("ACTOR B", "13", "17"))
        );
        assertEquals(2, Queries.actorsInMoreThanOneMovie(movies));
        assertEquals(0, Queries.actorsInMoreThanOneMovie(Collections.emptyList()));
    }

    @Test
    void testQ7ActorInMostMovies(){
        List<Document> movies = Arrays.asList(
                new Document("cast", Arrays.asList("2", "3", "ACTOR B", "5")),    //B CUS IT APPEARS FIRST!
                new Document("cast", Arrays.asList("ACTOR B", "ACTOR A", "7")),
                new Document("cast", Arrays.asList("ACTOR A", "13", "17")),
                new Document("cast", Arrays.asList("19", "23", "ACTOR B", "ACTOR  A"))
        );
        assertEquals("ACTOR B", Queries.actorInMostMovies(movies));
        assertEquals("No actor found", Queries.actorInMostMovies(Collections.emptyList()));
    }

    @Test
    void testQ8CountUniqueLanguages(){
        List<Document> movies = Arrays.asList(
                new Document("languages", Arrays.asList("English", "Spanish")),
                new Document("languages", Arrays.asList("English", "French")),
                new Document("languages", Arrays.asList("Spanish", "French"))
        );
        assertEquals(3, Queries.countUniqueLanguages(movies));
        assertNotEquals(2, Queries.countUniqueLanguages(movies));
        assertEquals(0, Queries.countUniqueLanguages(Collections.emptyList()));
    }

    @Test
    void testQ9HasDuplicateTitles(){
        List<Document> movies = Arrays.asList(
                new Document("title", "Movie 1"),
                new Document("title", "Movie 2"),
                new Document("title", "Movie 1")
        );
        assertTrue(Queries.hasDuplicateTitles(movies));
        assertNotEquals(false, Queries.hasDuplicateTitles(movies));
        assertFalse(Queries.hasDuplicateTitles(Collections.emptyList()));
    }





}
