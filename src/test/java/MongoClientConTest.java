import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.example.Queries;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;


import static org.junit.jupiter.api.Assertions.*;

class MongoClientConTest {


    @Test
    void testCountMoviesFrom1975() {

        MongoCollection<Document> mockCollection =
                mock(MongoCollection.class);

        when(mockCollection.countDocuments(Filters.eq("year", 1975))).thenReturn(5L);
        assertEquals(5L, Queries.countMoviesFrom1975(mockCollection));
    }


}
