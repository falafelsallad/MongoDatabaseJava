package org.example;
import static com.mongodb.client.model.Filters.eq;

import com.mongodb.client.model.Sorts;
import org.bson.Document;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.conversions.Bson;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Main {
    public static void main( String[] args ) {

        String uri = "mongodb+srv://trash:passwords@cluster.awrv3.mongodb.net/?retryWrites=true&w=majority&appName=Cluster";
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase("sample_mflix");
            MongoCollection<Document> collection = database.getCollection("movies");
            Document doc = collection.find(eq("title", "Back to the Future")).first();
            if (doc != null) {
                doc.toJson();
                System.out.println(doc.toJson());
                System.out.println("Connected  WELCOME TO THE FUTURE");
            } else {
                System.out.println("No matching documents found.");
            }

            //TODO: THIS IS THE CODE THAT NEEDS TO BE IMPLEMENTED
            /// Q1
            var count = mongoClient.getDatabase("sample_mflix").getCollection("movies")
                    .countDocuments(eq("year", 1975)
                    );
            System.out.println("Number of movies released the year 1975: " + count);
            /// Q2
            var length = mongoClient.getDatabase("sample_mflix").getCollection("movies")
                    .find()
                    .sort(eq("runtime", -1))
                    .first();
            System.out.println("Longest movie: " + length.getString("title"));
            /// Q3
            var genres = mongoClient.getDatabase("sample_mflix").getCollection("movies")
                    .distinct("genres", eq("year", 1975), String.class)
                    .into(new ArrayList<>()
                    );
            System.out.println("Genres from 1975: " + genres);
            /// Q4
            var highestRatedMovie = mongoClient.getDatabase("sample_mflix").getCollection("movies")
                    .find()
                    .sort(Sorts.descending("imdb"))
                    .first();
            System.out.println("Highest rated movie: " + highestRatedMovie.getString("title"));


            //            handleCollection(mongoClient.getDatabase("sample_mflix").getCollection("movies"),
//                    Filters.lte("year", 1975),
//                    "movies_1975.json"
//            );

//            System.out.println("Data successfully written to file");

// ------------------------------------------------------------------------------------------------

//            ///* Higher order function.
//            ///* 1. Generalise the handling of collections and filters.
//            ///* 2.Allows reuse of the entire pipeline for other collections or queries without modifying internal logic.
//
//            private static void handleCollection (MongoCollection <Document>  collection, Object filter, String outputFile) {
//                fetchDocumentsAsJson(collection, filter)
//                        .ifPresent(jsonArray -> writeJsonToFile(jsonArray, outputFile));
//            }
//            /// FetchDocumentsAsJson pipepline is only executed if the collection is not empty.
//            /// Empty collections willnot trigger JSon writing, reducing null pointer exceptions.
//            private static java.util.Optional<String> fetchDocumentsAsJson
//            (MongoCollection <Document> collection, Object filter){
//                var jsonArray = StreamSupport.stream(collection.find((Bson) filter).spliterator(), false)
//                        .map(Document::toJson)
//                        .collect(Collectors.joining(",\n", "[\n", "\n]"));
//                return jsonArray.isEmpty() ? java.util.Optional.empty() : java.util.Optional.of(jsonArray);
//            }
//
//            private static void writeJsonToFile (String jsonData, String fileName) {
//                try (var fileWriter = new FileWriter(fileName)) {
//                    fileWriter.write(jsonData);
//                } catch (Exception e) {
//                    throw new RuntimeException("Error writing JSON to file", e);
//
//                }
//            }

        }


    }
}