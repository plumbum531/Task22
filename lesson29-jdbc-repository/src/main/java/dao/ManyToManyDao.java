package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ManyToManyDao {
    Connection connection;
    BookDao bookDao;
    AuthorDao authorDao;

    public ManyToManyDao(Connection connection, BookDao bookDao, AuthorDao authorDao) {
        this.connection = connection;
        this.bookDao = bookDao;
        this.authorDao = authorDao;
    }

    public void createSelectFromCombinatedTable() {
        String template = "SELECT books.id, authors.id FROM books" +
                " JOIN authors ON authors.id = books.author_id";
        try (PreparedStatement preparedStatement = connection.prepareStatement(template)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                insertCursorSetInTable(resultSet);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to create combined table", e);
        }
    }

    /**
     * не решена проблема вставки/пропуска повторяющихся наборов resultSet'a
     */
    void insertCursorSetInTable(ResultSet resultSet) {
        String template = "INSERT INTO relationship_book_author(bookId, authorId) VALUES(?,?)";
        try (PreparedStatement statement = connection.prepareStatement(template)) {
            int bookId = resultSet.getInt(1);
            int authorId = resultSet.getInt(2);
            statement.setInt(1, bookId);
            statement.setInt(2, authorId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add row into combined table", e);
        }
    }

    public void printTable() {
        String template = "SELECT * FROM relationship_book_author JOIN books ON bookId = books.id" +
                " JOIN authors ON authorId = authors.id";
        try (PreparedStatement statement = connection.prepareStatement(template)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                System.out.println(resultSet.getString(1) + " " + resultSet.getString(2) + " " +
                        resultSet.getString(3) + " " + resultSet.getString(4) + " " +
                        resultSet.getString(5) + " " + resultSet.getString(6) + " " +
                        resultSet.getString(7) + " " + resultSet.getString(8) + " " +
                        resultSet.getString(9) + " " + resultSet.getString(10));
            }
        } catch (SQLException throwables) {
            throw new RuntimeException("Failed to add row into combinated table", throwables);
        }
    }
}
