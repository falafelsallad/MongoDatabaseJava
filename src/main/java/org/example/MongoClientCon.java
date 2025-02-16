package org.example;

import static com.mongodb.client.model.Filters.eq;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Collections;
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

         try {
             System.out.println("Number of movies released the year 1975: " + countMoviesFrom1975(collection));
             System.out.println("Longest movie: " + findLongestMovieRuntime(collection));
             System.out.println("Genres from 1975: " + countUniqueGenres1975(collection));
             System.out.println("Highest rated movie actors: " + findTopRatedMovieActors(collection));
             System.out.println("Movie with least actors: " + findMovieWithLeastActors(collection));
             System.out.println("Actors in more than one movie: " + actorsInMoreThanOneMovie(collection));
             System.out.println("Actor in most movies: " + actorInMostMovies(collection));
             System.out.println("Unique languages: " + countUniqueLanguages(collection));
             System.out.println("Duplicate titles: " + hasDuplicateTitles(collection));
         } finally {
             mongoClient.close();
         }

    }
    private static long countMoviesFrom1975(MongoCollection<Document> collection) {
        return collection.countDocuments(Filters.eq("year", 1975));
    }

    private static int findLongestMovieRuntime(MongoCollection<Document> collection) {
        Document result = collection.aggregate(List.of(Aggregates.match(Filters.exists("runtime", true)),
                Aggregates.group(null, Accumulators.max("runtime", "$runtime"))
                )).first();
        return result !=null ? result.getInteger("runtime") : 0;
    }

    private static List<String> countUniqueGenres1975(MongoCollection<Document> collection) {
        var result = collection.aggregate(List.of(Aggregates.match(Filters.eq("year", 1975)),
                Aggregates.unwind("$genres"),
                Aggregates.group("$genres"),
                Aggregates.sort(Sorts.ascending("_id"))
                )).map(doc -> doc.getString("_id")).into(new ArrayList<>());
        return result !=null ? result : Collections.EMPTY_LIST;
    }

    public static List<String> findTopRatedMovieActors(MongoCollection<Document> collection) {
        var result = collection.aggregate(List.of(
                Aggregates.match(Filters.exists("imdb", true)),
                Aggregates.sort(Sorts.descending("imdb.rating")),
                Aggregates.limit(1)
        )).first();
        return result !=null ? List.copyOf(result.getList("cast", String.class)) : Collections.EMPTY_LIST;
    }

    //TODO: SHOULD WRITE OUT LITTLE NEMO (1911) AS THE MOVIE WITH THE LEAST ACTORS
    public static String findMovieWithLeastActors(MongoCollection<Document> collection) {
        var result = collection.aggregate(List.of(
                Aggregates.match(Filters.exists("cast", true)),
                Aggregates.project(Projections.fields(Projections.include("title"),
                        Projections.computed("castSize", new Document("$size", "$cast"))
                )),
                Aggregates.sort(Sorts.ascending("castSize")),
                Aggregates.limit(1)
        )).first();
        return result !=null ? result.getString("title") : "No movie found";
    }

    public static int actorsInMoreThanOneMovie(MongoCollection<Document> collection){
        var result = collection.aggregate(List.of(
               Aggregates.unwind("$cast"),
               Aggregates.group("$cast", Accumulators.sum(
                       "movieCount", 1)),
                Aggregates.match(Filters.gt("movieCount", 1)),
                Aggregates.count()
        )).first();
        return result !=null ? result.getInteger("count") : 0;
    }

    public static String actorInMostMovies(MongoCollection<Document> collection){
        var result = collection.aggregate(List.of(
                Aggregates.unwind("$cast"),
                Aggregates.group("$cast", Accumulators.sum("movieCount", 1)),
                Aggregates.sort(Sorts.descending("movieCount")),
                Aggregates.limit(1)
        )).first();
        return result !=null ? result.getString("_id") : "No actor found";
    }

    public static int countUniqueLanguages(MongoCollection<Document> collection){
        var result = collection.aggregate(List.of(
                Aggregates.unwind("$languages"),
                Aggregates.group("$language"),
                Aggregates.count()
        )).first();
        return result !=null ? result.getInteger("count") : 0;
    }

    public static boolean hasDuplicateTitles(MongoCollection<Document> collection){
        var result = collection.aggregate(List.of(
                Aggregates.group("$title", Accumulators.sum("count", 1)),
                Aggregates.match(Filters.gt("count", 1)),
                Aggregates.limit(1)
        )).first();
        return result !=null;
    }


}
