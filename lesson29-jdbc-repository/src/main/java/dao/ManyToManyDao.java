package dao;

import models.Book;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

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
        //связь многие ко многим
        String template = "SELECT books.id, authors.id" +
                " FROM books" +
                " JOIN authors ON authors.id = books.author_id";
        try (PreparedStatement preparedStatement = connection.prepareStatement(template)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                insertCursorSetInTable(resultSet);
            }

        } catch (SQLException throwables) {
            throw new RuntimeException("Failed to create combinated table", throwables);
        }
    }

    /**
     * не решена проблема вставки/пропуска повторяющихся наборов resultSet'a
     *
     */
    void insertCursorSetInTable(ResultSet resultSet) {
        String template = "INSERT INTO relationship_book_author(bookId, authorId) VALUES(?,?)";
        try (PreparedStatement statement = connection.prepareStatement(template)) {
            int bookId = resultSet.getInt(1);
            int authorId = resultSet.getInt(2);
            statement.setInt(1, bookId);
            statement.setInt(2, authorId);
            statement.executeUpdate();
        } catch (SQLException throwables) {
            throw new RuntimeException("Failed to add row into combinated table", throwables);
        }
    }

    public void getAutorBook(String title) {
        String template = "SELECT bookId, authorId FROM relationship_book_author WHERE bookID = ?";
        try (PreparedStatement statement = connection.prepareStatement(template)) {
            Optional<Book> bookTitle = bookDao.getBookByName(title);
            if (bookTitle.isPresent()) {
                int titlebook = bookTitle.get().id;
                statement.setInt(1, titlebook);
            } else {
                System.out.println("Book not found!");
            }
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                printResultSet(resultSet);
            }
        } catch (SQLException throwables) {
            throw new RuntimeException("Failed to add row into combinated table", throwables);
        }
    }

    void printResultSet(ResultSet set) {
        String template = "SELECT books.title, authors.name FROM books JOIN authors" +
                " ON authors.id = books.author_id WHERE books.id = ? AND  authors.id = ?";
        try (PreparedStatement statement = connection.prepareStatement(template)) {
            int bookId = set.getInt(1);
            int authorId = set.getInt(2);
            statement.setInt(1, bookId);
            statement.setInt(2, authorId);
            ResultSet resultSet = statement.executeQuery();
            System.out.println("Title is: " + resultSet.getString(1) +
                    ", author is: " + resultSet.getString(2));

        } catch (SQLException throwables) {
            throw new RuntimeException("Failed to add row into combinated table", throwables);
        }
    }


}
