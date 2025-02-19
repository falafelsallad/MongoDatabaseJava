package org.example;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.List;

import static org.example.Queries.fetchMovies;


public class MongoClientCon {
    public static void main(String[] args) {
        var connectionString =
                System.getenv("MONGODB");

        var serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();
        var settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .serverApi(serverApi)
                .build();
        var mongoClient = MongoClients.create(settings);
        MongoDatabase database = mongoClient.getDatabase("sample_mflix");
        MongoCollection<Document> collection = database.getCollection("movies");
        List<Document> movies = fetchMovies(collection);

        try {
            System.out.println("Q1: Number of movies released the year 1975: " + Queries.Q1countMoviesFrom1975(movies));
            System.out.println("Q2: Longest movie: " + Queries.Q2findLongestMovieRuntime(movies));
            System.out.println("Q3: Genres from 1975: " + Queries.Q3countUniqueGenres1975(movies));
            System.out.println("Q4: Highest rated movie actors: " + Queries.Q4findTopRatedMovieActors(movies));
            System.out.println("Q5: Movie with least actors: " + Queries.Q5findMovieWithLeastActors(movies));
            System.out.println("Q6: Actors in more than one movie: " + Queries.Q6actorsInMoreThanOneMovie(movies));
            System.out.println("Q7: Actor in most movies: " + Queries.Q7actorInMostMovies(movies));
            System.out.println("Q8: Unique languages: " + Queries.Q8countUniqueLanguages(movies));
            System.out.println("Q9: Duplicate titles: " + Queries.Q9hasDuplicateTitles(movies));
        } finally {
            mongoClient.close();
        }

    }


}
