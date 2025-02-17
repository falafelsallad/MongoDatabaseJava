package org.example;

import static com.mongodb.client.model.Filters.eq;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import org.bson.Document;
import org.bson.internal.BsonUtil;

import java.util.*;
import java.util.stream.Collectors;

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

         try {
             System.out.println("Q1: Number of movies released the year 1975: " + Queries.countMoviesFrom1975(collection));
             System.out.println("Q2: Longest movie: " + Queries.findLongestMovieRuntime(collection));
             System.out.println("Q3: Genres from 1975: " + Queries.countUniqueGenres1975(collection));
             System.out.println("Q4: Highest rated movie actors: " + Queries.findTopRatedMovieActors(collection));
             System.out.println("Q5: Movie with least actors: " + Queries.findMovieWithLeastActors(collection));
             System.out.println("Q6: Actors in more than one movie: " + Queries.actorsInMoreThanOneMovie(collection));
             System.out.println("Q7: Actor in most movies: " + Queries.actorInMostMovies(collection));
             System.out.println("Q8: Unique languages: " + Queries.countUniqueLanguages(collection));
             System.out.println("Q9: Duplicate titles: " + Queries.hasDuplicateTitles(collection));
         } finally {
             mongoClient.close();
         }

    }


}
