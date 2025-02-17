
import com.mongodb.Function;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.example.Queries;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class QueriesTest {
    private MongoCollection<Document> collection;
    private FindIterable<Document> iterable;


    @BeforeEach
    void setUp() {
        collection = Mockito.mock(MongoCollection.class);
        iterable = mock(FindIterable.class);
    }

    @Test
    void testCountMoviesFrom1975() {
        when(collection.countDocuments(Filters.eq("year", 1975))).thenReturn(5L);
        assertEquals(5L, Queries.countMoviesFrom1975(collection));
    }

    @Test
    void testFindLongestMovieRuntime() {
        Document doc1 = new Document("runtime", 120);
        Document doc2 = new Document("runtime", 90);
        when(collection.find(Filters.exists("runtime", true))).thenReturn(iterable);
        when(iterable.sort(Sorts.descending("runtime"))).thenReturn(iterable);
        when(iterable.limit(1)).thenReturn(iterable);
        when(iterable.first()).thenReturn(doc1);

        assertEquals(120, Queries.findLongestMovieRuntime(collection));
    }

    @Test
    void testCountUniqueGenres1975() {
      Document doc1 = new Document("genres", Arrays.asList("Action", "Drama"));
      Document doc2 = new Document("genres", Arrays.asList("Drama", "Comedy"));
      when(collection.find(Filters.eq("year", 1975))).thenReturn((FindIterable<Document>) Arrays.asList(doc1, doc2));
        List<String> expected = Arrays.asList("Action", "Comedy", "Drama");
        assertEquals(expected, Queries.countUniqueGenres1975(collection));
    }

    @Test
    void testCountUnique2() {


    }





}
