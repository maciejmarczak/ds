package org.maciejmarczak.ds.akka.model;

import de.svenjacobs.loremipsum.LoremIpsum;

import java.io.Serializable;
import java.math.BigDecimal;

public class Book implements Serializable {
    private final String title;
    private BigDecimal price;

    public Book(String title, BigDecimal price) {
        this.title = title;
        this.price = price;
    }

    public String getTitle() {
        return title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public byte[] getContent() {
        return ContentGenerator.getContent();
    }

    private static class ContentGenerator {
        static final LoremIpsum LOREM_IPSUM
                = new LoremIpsum();

        static byte[] getContent() {
            return LOREM_IPSUM.getParagraphs(1).getBytes();
        }
    }
}
