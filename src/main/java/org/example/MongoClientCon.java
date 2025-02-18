package org.example;

import static com.mongodb.client.model.Filters.eq;
import static org.example.Queries.fetchMovies;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.*;
import org.bson.Document;

import java.util.List;


public class MongoClientCon {
    public static void main(String[] args) {
        var connectionString=  //TODO: Replace this with a more secure way of storing the connection string
                "mongodb+srv://trash:passwords@cluster.awrv3.mongodb.net/?retryWrites=true&w=majority&appName=Cluster";

        var serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();
        var settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .serverApi(serverApi)
                .build();
        var mongoClient = MongoClients.create(settings);
         MongoDatabase database=  mongoClient.getDatabase("sample_mflix");
         MongoCollection<Document> collection = database.getCollection("movies");
        List <Document> movies = fetchMovies(collection);

         try {

             System.out.println("Q1: Number of movies released the year 1975: " + Queries.countMoviesFrom1975(movies));
             System.out.println("Q2: Longest movie: " + Queries.findLongestMovieRuntime(movies));
             System.out.println("Q3: Genres from 1975: " + Queries.countUniqueGenres1975(movies));
             System.out.println("Q4: Highest rated movie actors: " + Queries.findTopRatedMovieActors(movies));
             System.out.println("Q5: Movie with least actors: " + Queries.findMovieWithLeastActors(movies));
             System.out.println("Q6: Actors in more than one movie: " + Queries.actorsInMoreThanOneMovie(movies));
             System.out.println("Q7: Actor in most movies: " + Queries.actorInMostMovies(movies));
             System.out.println("Q8: Unique languages: " + Queries.countUniqueLanguages(movies));
             System.out.println("Q9: Duplicate titles: " + Queries.hasDuplicateTitles(movies));
         } finally {
             mongoClient.close();
         }

    }


}
