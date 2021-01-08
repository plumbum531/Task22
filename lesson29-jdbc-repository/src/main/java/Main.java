import models.Author;
import models.Book;
import models.Comment;
import models.User;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class Main {
    public static void main(String[] args) {
        try {
            new Main().run();
        } catch (SQLException e) {
            System.out.println("Failed to do something: " + e.getLocalizedMessage());
        }
    }

    private void run() throws SQLException {
        try (Connection connection =
                     DriverManager.getConnection("jdbc:sqlite:books.db")) {
            doWork(connection);
        }
    }

    private void doWork(Connection connection) throws SQLException {
        // TODO work with connection

        createTables(connection);
        IBookRepository repository = new JdbcBookRepository(connection);

        final Author author1 = new Author();
        author1.name = "Толкин";
        author1.birthYear = 1901;

        final Book book1 = new Book();
        book1.title = "Властелин колец";
        book1.publishYear = 1940;
        book1.price = new BigDecimal("3000.33");

        final Author author2 = new Author();
        author2.name = "Дейтел";
        author2.birthYear = 1960;

        final Book book2 = new Book();
        book2.title = "Java для начинающих";
        book2.publishYear = 2005;
        book2.price = new BigDecimal("100500700.255123");

        final Comment comment1 = new Comment();
        comment1.date =  Date.from(Instant.now());
        comment1.text = "Good book";

        Collection <Comment> commentsFirstbook = new ArrayList<>();
        commentsFirstbook.add(comment1);

        final Comment comment2 = new Comment();
        comment2.date = Date.from(Instant.now());
        comment2.text = "Bad book";

        final Comment comment3 = new Comment();
        comment3.date = Date.from(Instant.now());
        comment3.text = "Nice book";

        Collection<Comment> commentsSecondBooK = new ArrayList<>();
        commentsSecondBooK.add(comment2);
        commentsSecondBooK.add(comment3);

        final User user1 = new User();
        user1.nickName = "nickFirst";
        user1.registryDate = "12.06.2015";

        final User user2 = new User();
        user2.nickName = "nickSecond";
        user2.registryDate = "6.05.2001";

        //repository.save(book1, author1);
        //repository.save(book1, author1, comment1, user1); //надо понять как добавлять много коментов к одной книге
        //repository.save(book2, author2, comment2, user2);
        //reposotiry.saveUser
        Collection<Book> books = repository.getAll();
        System.out.println("Books count:" + books.size());
    }

    public final String CreateBooksTableQuery = "CREATE TABLE IF NOT EXISTS books (" +
            " id INTEGER PRIMARY KEY AUTOINCREMENT," +
            " title VARCHAR(100)," +
            " publish_year INTEGER," +
            " price DECIMAL(10,2)," +
            " author_id INTEGER" +
            ")";

    public final String CreateAuthorsTableQuery = "CREATE TABLE IF NOT EXISTS authors (" +
            " id INTEGER PRIMARY KEY AUTOINCREMENT," +
            " name VARCHAR(100)," +
            " birth_year INTEGER" +
            ")";

    public final String CreateCommentsTableQuery = "CREATE TABLE IF NOT EXISTS comments (" +
            " id INTEGER PRIMARY KEY AUTOINCREMENT," +
            " date DATETIME," +
            " text VARCHAR(100)," +
            " userId INTEGER"+
            " bookId INTEGER"+
            ")";

    public final String CreateUsersTableQuery = "CREATE TABLE IF NOT EXISTS users (" +
            " id INTEGER PRIMARY KEY AUTOINCREMENT," +
            " registryDate VARCHAR(100)," +
            " nickName VARCHAR(100)"+
            ")";

    private void createTables(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(CreateBooksTableQuery);
            statement.executeUpdate(CreateAuthorsTableQuery);
            statement.executeUpdate(CreateCommentsTableQuery);
            statement.executeUpdate(CreateUsersTableQuery);
        }
    }
}
