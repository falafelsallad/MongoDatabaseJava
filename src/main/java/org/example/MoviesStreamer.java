package org.example;

import org.bson.Document;

import java.util.stream.Stream;

public interface MoviesStreamer<T> {

    T process(Stream<Document> movies);
}
