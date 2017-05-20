package org.maciejmarczak.ds.akka.server.db;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.maciejmarczak.ds.akka.model.Book;
import org.maciejmarczak.ds.akka.model.BookNotFoundException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class BookService {

    private static final Object LOCK = new Object();
    private static final BookService INSTANCE = new BookService();

    private BookService() {}

    public static BookService getInstance() {
        return INSTANCE;
    }

    private final ExecutorService executorService
            = Executors.newCachedThreadPool();

    public Book findBook(String title) {
        CompletionService<Book> completionService
                = new ExecutorCompletionService<>(executorService);

        for (String db : DbSource.BOOK_DBS) {
            completionService.submit(new BookFinder(db, title));
        }

        Book result = null;

        try {
            int submittedNo = DbSource.BOOK_DBS.size();
            int finishedNo = 0;
            boolean foundSolution = false;

            while (finishedNo < submittedNo && !foundSolution) {
                Future<Book> bookFuture = completionService.take();
                Book book = bookFuture.get();

                if (book != null) {
                    result = book;
                    foundSolution = true;
                }

                finishedNo++;
            }
        } catch (InterruptedException | ExecutionException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        }

        return result;
    }

    public void order(String title) {
        Book book = findBook(title);

        if (book == null) {
            throw new BookNotFoundException(title + " doesn't exist so it can't be ordered.");
        }

        try (CSVPrinter printer =
                     new CSVPrinter(new FileWriter(DbSource.ORDERS_DB), CSVFormat.EXCEL)) {

            synchronized (LOCK) {
                printer.printRecord(book.getTitle());
            }
        } catch (IOException e) {
            // should be logged
            e.printStackTrace();
        }
    }

    private class BookFinder implements Callable<Book> {

        final String db;
        final String title;

        BookFinder(String db, String title) {
            this.db = db;
            this.title = title;
        }

        @Override
        public Book call() throws Exception {
            Iterable<CSVRecord> records =
                    CSVFormat.EXCEL.withHeader("title", "price").parse(new FileReader(db));

            for (CSVRecord record : records) {
                if (title.equals(record.get("title"))) {
                    return new Book(record.get("title"), new BigDecimal(record.get("price")));
                }
            }

            return null;
        }
    }

    private static class DbSource {
        static final String ORDERS_DB = "orders.csv";
        static final List<String> BOOK_DBS
                = Arrays.asList("books_db_1.csv", "books_db_2.csv");
    }
}
