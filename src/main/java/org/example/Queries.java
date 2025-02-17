package org.example;

import com.mongodb.Function;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import org.bson.Document;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Queries {

    public static long countMoviesFrom1975(MongoCollection<Document> collection) {
        return collection.countDocuments(Filters.eq("year", 1975));
    }

    public static int findLongestMovieRuntime(MongoCollection<Document> collection) {
        var result=  collection.find(Filters.exists("runtime", true))
                .sort(Sorts.descending("runtime"))
                .limit(1)
//                .map(doc -> doc.getInteger("runtime", 0))
                .first();
        return result !=null ? result.getInteger("runtime") : 0;
    }

    public static List<String> countUniqueGenres1975(MongoCollection<Document> collection) {
        var result = collection.find(Filters.eq("year", 1975))
                .into(new ArrayList<>())
                .stream()
                .flatMap(doc -> doc.getList("genres", String.class).stream())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        return result !=null ? result : Collections.EMPTY_LIST;
    }

    public static List<String> findTopRatedMovieActors(MongoCollection<Document> collection) {
        var result = collection.find(Filters.exists("imdb", true))
                .sort(Sorts.descending(("imdb.rating")))
                .limit(1)
                .map(doc -> doc.getList("cast", String.class))
                .first();

        return result !=null ? List.copyOf(result) : Collections.EMPTY_LIST;
    }

    //TODO: SHOULD WRITE OUT LITTLE NEMO (1911) AS THE MOVIE WITH THE LEAST ACTORS
    public static String findMovieWithLeastActors(MongoCollection<Document> collection) {
        return collection.find(Filters.exists("cast", true))
                .into(new ArrayList<>())
                .stream()
                .map(doc -> new AbstractMap.SimpleEntry<>(doc.getString("title"), doc.getList("cast", String.class).size()))
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("No movie found");
    }

    public static <T> int actorsInMoreThanOneMovie(MongoCollection<Document> collection){
        var result = collection.find(Filters.exists("cast", true))
                .into(new ArrayList<>())
                .stream()
                .flatMap(doc -> doc.getList("cast", String.class).stream())
                .collect(Collectors.groupingBy(actor -> actor, Collectors.counting()))
                .values()
                .stream()
                .filter(count -> count > 1)
                .count();
        return (int) result; //turns long to int
    }

    public static <T> String actorInMostMovies(MongoCollection<Document> collection){
        var result = collection.find(Filters.exists("cast", true))
                .into(new ArrayList<>())
                .stream()
                .flatMap(doc -> doc.getList("cast", String.class).stream())
                .collect(Collectors.groupingBy(actor -> actor, Collectors.counting()))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);
        return  result.orElse("No actor found");
    }

    public static int countUniqueLanguages(MongoCollection<Document> collection){
        var result = collection.find(Filters.exists("languages", true))
                .into(new ArrayList<>())
                .stream()
                .flatMap(doc -> doc.getList("languages", String.class).stream())
                .distinct()
                .count();
        return (int) result;
    }

    public static boolean hasDuplicateTitles(MongoCollection<Document> collection){
        var result = collection.find(Filters.exists("title", true))
                .into(new ArrayList<>())
                .stream()
                .collect(Collectors.groupingBy(doc -> doc.getString("title"),Collectors.counting()))
                .values()
                .stream()
                .anyMatch(count -> count > 1);
        return result;
    }

}
