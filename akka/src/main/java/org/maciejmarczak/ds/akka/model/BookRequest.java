package org.maciejmarczak.ds.akka.model;

import java.io.Serializable;

public class BookRequest implements Serializable {
    private final String bookTitle;
    private final Type type;

    public BookRequest(String bookTitle, Type type) {
        this.bookTitle = bookTitle;
        this.type = type;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        SEARCH, ORDER, DOWNLOAD
    }
}
