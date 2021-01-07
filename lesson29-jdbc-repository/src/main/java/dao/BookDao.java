package dao;

import models.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class BookDao {
    private final Connection connection;

    public BookDao(Connection connection) {
        this.connection = connection;
    }

    public void delete(int id) {
        final String template = "DELETE FROM books WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(template)) {
            statement.setInt(1, id);
            int affectedRows = statement.executeUpdate();
            if (affectedRows != 1) {
                throw new IllegalArgumentException(
                        "Affected rows on delete: " + affectedRows);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete book", e);
        }
    }

    public Optional<Book> getBookById(int id) {
        try (Statement statement = connection.createStatement()) {
            ResultSet cursor = statement.executeQuery(
                    "SELECT * FROM books WHERE id = " + id);
            if (!cursor.next()) {
                return Optional.empty();
            }

            return Optional.of(createBookFromCursorIfPossible(cursor));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch one book", e);
        }
    }

    public Optional<Book> getBookByName(String bookTitle) {
        final String template = "SELECT * FROM books WHERE title = ? LIMIT 1";
        try (PreparedStatement statement = connection.prepareStatement(template)) {
            statement.setString(1, bookTitle);
            ResultSet cursor = statement.executeQuery();
            if (!cursor.next()) {
                return Optional.empty();
            }

            return Optional.of(createBookFromCursorIfPossible(cursor));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch one book", e);
        }
    }

    public Optional<Book> getBookByTitleAndAuthor(String bookTitle, String authorName) {
        final String template = "SELECT books.* FROM books" +
                " JOIN authors ON authors.id = books.author_id" +
                " WHERE books.title = ? AND authors.name = ?" +
                " LIMIT 1";
        try (PreparedStatement statement = connection.prepareStatement(template)) {
            statement.setString(1, bookTitle);
            statement.setString(2, authorName);
            ResultSet cursor = statement.executeQuery();
            if (!cursor.next()) {
                return Optional.empty();
            }

            return Optional.of(createBookFromCursorIfPossible(cursor));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch one book", e);
        }
    }

    public Collection<Book> getAllBooks() {
        try (Statement statement = connection.createStatement()) {
            final Collection<Book> books = new ArrayList<>();
            ResultSet cursor = statement.executeQuery("SELECT * FROM books");
            while (cursor.next()) {
                books.add(createBookFromCursorIfPossible(cursor));
            }
            return books;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch books", e);
        }
    }


    public int insertBook(Book book) {
        final String insertTemplate =
                "INSERT INTO books(title,price,publish_year,author_id) VALUES(?,?,?,?)";

        try (PreparedStatement statement = connection.prepareStatement(insertTemplate)) {
            statement.setString(1, book.title);
            statement.setBigDecimal(2, book.price);
            statement.setInt(3, book.publishYear);
            statement.setInt(4, book.authorId);
            statement.executeUpdate();


            ResultSet cursor = statement.getGeneratedKeys();
            if (!cursor.next()) {
                throw new RuntimeException("Failed to insert author");
            }

            return cursor.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert book", e);
        }
    }

    public void updateBook(Book book) {
        final String updateTemplate =
                "UPDATE books" +
                        " SET title=?, price=?, publish_year=?, author_id=?" +
                        " WHERE id=?";

        try (PreparedStatement statement = connection.prepareStatement(updateTemplate)) {
            statement.setString(1, book.title);
            statement.setBigDecimal(2, book.price);
            statement.setInt(3, book.publishYear);
            statement.setInt(4, book.authorId);
            statement.setInt(5, book.id);
            int affectedRows = statement.executeUpdate();
            if (affectedRows != 1) {
                throw new IllegalArgumentException(
                        "Affected rows on update: " + affectedRows);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update book", e);
        }
    }


    private Book createBookFromCursorIfPossible(ResultSet cursor) throws SQLException {
        final Book book = new Book();
        book.id = cursor.getInt("id");
        book.title = cursor.getString("title");
        book.publishYear = cursor.getInt("publish_year");
        book.price = cursor.getBigDecimal("price");
        book.authorId = cursor.getInt("author_id");
//        TODO book.author = authorDao.getAuthorById(authorId).orElseThrow(
//                () -> new IllegalStateException("Failed to query author: " + authorId));
        return book;
    }

}
