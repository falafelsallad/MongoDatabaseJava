
import com.mongodb.Function;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.example.Queries;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

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

    @Test
    void testConvertsOnlyAndAllMovies1975() {
        MongoCollection<Document> collection = mock(MongoCollection.class);
        FindIterable<Document> findIterable = mock(FindIterable.class);

        when(collection.find(Filters.eq("year", 1975))).thenReturn(findIterable);
        when(findIterable.into(anyList())).thenAnswer((Answer<List<Document>>) invocation -> {
            List<Document> list = invocation.getArgument(0);
            list.add(new Document("title", "Movie 1").append("year", 1975));
            list.add(new Document("title", "Movie 2").append("year", 1975));
//            list.add(new Document("title", "Movie 3").append("year", 1976));
            return list;
        });


        List<Document> movies = Queries.fetchMovies(collection);

        assertEquals(2, movies.size());
        assertTrue(movies.stream().allMatch(doc -> doc.getInteger("year") == 1975));


    }

    @Test
    void testCountsAllMovies1975(){
        List<Document> movies = Arrays.asList(
                new Document("year", 1975),
                new Document("year", 1975),
                new Document("year", 1975)
        );
        assertEquals(3, Queries.countMoviesFrom1975(movies));
    }







}
