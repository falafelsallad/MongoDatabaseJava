package org.example;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import org.bson.Document;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Queries {

    public static List<Document> fetchMovies(MongoCollection<Document> collection) {
        return collection.find(Filters.eq("year", 1975))
                .projection(Projections.include("title", "runtime", "genres", "cast", "languages", "imdb"))
                .into(new ArrayList<>());
    }

    //This method is used to process the stream of movies with the help of the MoviesStreamer interface
    public static <T> T process(List<Document> movies, MoviesStreamer<T> streamer) {
        return streamer.process(movies.stream());
    }

    //This method is used to filter the stream of movies with the help of the MovieFilter interface and MoviesStreamer interface
    public static <T> T processAndFilter(List<Document> movies, MovieFilter filter, Predicate<Document> predicate, MoviesStreamer<T> streamer) {
        return streamer.process(filter.filters(movies.stream(), predicate));
    }

    //Only the docs that pass the filter are sorted and limited, become null, and reversed to the end of the stream
    public static <T> T processAndSort(List<Document> movies, MovieFilter filter, Comparator<Document> comparator, int limit, MoviesStreamer<T> streamer) {
        return streamer.process(filter.filters(movies.stream(), doc -> true).sorted(comparator).limit(limit));
    }


    public static long Q1countMoviesFrom1975(List<Document> movies) {
        return process(movies, Stream::count);
    }

    public static int Q2findLongestMovieRuntime(List<Document> movies) {
        return processAndFilter(movies, Stream::filter, _ -> true, stream -> stream
                .map(doc -> doc.getInteger("runtime", 0))
                .max(Integer::compareTo)
                .orElse(0));
    }

    public static Long Q3countUniqueGenres1975(List<Document> movies) {
        return process(movies, stream -> stream
                .flatMap(doc -> doc.getList("genres", String.class).stream())
                .distinct()
                .count());
    }

    public static List<String> Q4findTopRatedMovieActors(List<Document> movies) {
        return processAndSort(movies, Stream::filter, Comparator.comparing
                        (doc -> doc.getEmbedded(List.of("imdb", "rating"), Double.class), Comparator.reverseOrder()),
                1, stream -> stream
                        .map(doc -> doc.getList("cast", String.class))
                        .findFirst()
                        .orElse(Collections.emptyList()));
    }

    public static String Q5findMovieWithLeastActors(List<Document> movies) {
        return processAndSort(movies, Stream::filter, Comparator.comparing(
                        doc -> doc.getList("cast", String.class).size()),
                1, stream -> stream
                        .map(doc -> doc.getString("title"))
                        .findFirst()
                        .orElse("No movie found"));
    }

    public static int Q6actorsInMoreThanOneMovie(List<Document> movies) {
        return processAndFilter(movies, Stream::filter, doc -> doc.containsKey("cast"), stream -> (int) stream
                .flatMap(doc -> doc.getList("cast", String.class).stream())
                .collect(Collectors.groupingBy(actor -> actor, Collectors.counting()))
                .values()
                .stream()
                .filter(count -> count > 1)
                .count());
    }

    public static <T> String Q7actorInMostMovies(List<Document> movies) {
        return processAndFilter(movies, Stream::filter, doc -> doc.containsKey("cast"), stream -> stream)
                .flatMap(doc -> doc.getList("cast", String.class).stream())
                .collect(Collectors.groupingBy(actor -> actor, Collectors.counting()))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("No actor found");
    }

    public static Long Q8countUniqueLanguages(List<Document> movies) {
        return processAndFilter(movies, Stream::filter, doc -> doc.containsKey("languages"), stream -> stream)
                .flatMap(doc -> doc.getList("languages", String.class).stream())
                .distinct()
                .count();
    }

    public static boolean Q9hasDuplicateTitles(List<Document> movies) {
        return processAndFilter(movies, Stream::filter, doc -> doc.containsKey("title"), stream -> stream)
                .collect(Collectors.groupingBy(doc -> doc.getString("title"), Collectors.counting()))
                .values()
                .stream()
                .anyMatch(count -> count > 1);
    }

}
