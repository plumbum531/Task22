package dao;

import models.Author;

import java.sql.*;
import java.util.Optional;

public class AuthorDao {
    private final Connection connection;

    public AuthorDao(Connection connection) {
        this.connection = connection;
    }

    public void delete(int id) {
        final String template = "DELETE FROM authors WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(template)) {
            statement.setInt(1, id);
            int affectedRows = statement.executeUpdate();
            if (affectedRows != 1) {
                throw new IllegalArgumentException(
                        "Affected rows on delete: " + affectedRows);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete author", e);
        }
    }

    public Optional<Author> getAuthorById(int id) {
        try (Statement statement = connection.createStatement()) {
            ResultSet cursor = statement.executeQuery(
                    "SELECT * FROM authors WHERE id = " + id);
            if (!cursor.next()) {
                return Optional.empty();
            }

            return Optional.of(createAuthorFromCursorIfPossible(cursor));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch one book", e);
        }
    }

    private Author createAuthorFromCursorIfPossible(ResultSet cursor) throws SQLException {
        final Author author = new Author();
        author.id = cursor.getInt("id");
        author.name = cursor.getString("name");
        author.birthYear = cursor.getInt("birth_year");
        return author;
    }

    public int insertAuthor(Author author) {
        final String insertTemplate =
                "INSERT INTO authors(name,birth_year) VALUES(?,?)";

        try (PreparedStatement statement = connection.prepareStatement(insertTemplate)) {
            statement.setString(1, author.name);
            statement.setInt(2, author.birthYear);
            statement.executeUpdate();

            ResultSet cursor = statement.getGeneratedKeys();
            if (!cursor.next()) {
                throw new RuntimeException("Failed to insert author");
            }

            return cursor.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert author", e);
        }
    }

    public void updateAuthor(Author author) {
        final String insertTemplate =
                "UPDATE authors SET name = ?, birth_year = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(insertTemplate)) {
            statement.setString(1, author.name);
            statement.setInt(2, author.birthYear);
            statement.setInt(3, author.id);
            int affectedRows = statement.executeUpdate();
            if (affectedRows != 1) {
                throw new IllegalArgumentException(
                        "Affected rows on update: " + affectedRows);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update author", e);
        }
    }

}
