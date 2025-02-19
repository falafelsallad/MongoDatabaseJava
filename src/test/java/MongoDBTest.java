import org.bson.Document;
import org.example.Queries;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MongoDBContainer;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MongoDBTest {

    private static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo");
    private MongoClient mongoClient;
    private MongoDatabase database;

    @BeforeEach
    void setUp() {
        mongoDBContainer.start();
        mongoClient = MongoClients.create(mongoDBContainer.getReplicaSetUrl());
        database = mongoClient.getDatabase("testdb");
    }

    @AfterEach
    void tearDown() {
        mongoClient.close();
        mongoDBContainer.stop();
    }

    @Test
    void testInsertDocument() {
        MongoCollection<Document> collection = database.getCollection("moviestest");

        Document movie = new Document("title", "Interstellar")
                .append("year", 2014)
                .append("imdb", new Document("rating", 8.6))
                .append("cast", List.of("Matthew McConaughey", "Anne Hathaway", "Jessica Chastain"));
        collection.insertOne(movie);

        Document found = collection.find(new Document("title", "Interstellar")).first();

        assertNotNull(found);
        assertEquals("Interstellar", found.getString("title"));
        assertEquals(2014, found.getInteger("year"));
        assertEquals(8.6, found.getEmbedded(List.of("imdb", "rating"), Double.class));
        assertEquals(1, collection.countDocuments());
    }

    @Test
    void testFetchMovies() {
        MongoCollection<Document> collection = database.getCollection("moviestest");
        collection.insertOne(new Document("year", 1975));
        collection.insertOne(new Document("year", 1975));
        collection.insertOne(new Document("year", 1976));

        List<Document> movies = Queries.fetchMovies(collection);

        assertNotNull(movies);
        assertEquals(2, movies.size());
    }
}
