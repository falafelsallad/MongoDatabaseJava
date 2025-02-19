package org.example;

import org.bson.Document;

import java.util.function.Predicate;
import java.util.stream.Stream;

public interface MovieFilter {

    Stream<Document> filters(Stream<Document> movies, Predicate<Document> predicate);


}
