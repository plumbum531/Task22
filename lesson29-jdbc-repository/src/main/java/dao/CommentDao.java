package dao;

import models.Comment;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class CommentDao {
    private final Connection connection;

    public CommentDao(Connection connection) {
        this.connection = connection;
    }

    public void delete(int id) {
        final String template = "DELETE FROM coments WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(template)) {
            statement.setInt(1, id);
            int affectedRows = statement.executeUpdate();
            if (affectedRows != 1) {
                throw new IllegalArgumentException(
                        "Affected rows on delete: " + affectedRows);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete comment", e);
        }
    }

    public void insertComment(Comment comment) {
        final String insertTemplate = "INSERT INTO comments(date, text, userId, bookId) VALUE(?,?,?,?)";
        try (PreparedStatement statement = connection.prepareStatement(insertTemplate)) {
            statement.setDate(1, (comment.date));
            statement.setString(2, comment.text);
            statement.setInt(3, comment.userId);
            statement.setInt(4, comment.bookId);
            int affectedRows = statement.executeUpdate();
            if (affectedRows != 1) {
                throw new IllegalArgumentException(
                        "Affected rows on insertComment: " + affectedRows);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert comment", e);
        }
    }

    public void updateComment(Comment comment) {
        final String updateTemplate =
                "UPDATE comments SET date = ?, text = ?, userId = ?, bookId =? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(updateTemplate)) {
            statement.setDate(1, (comment.date));
            statement.setString(2, comment.text);
            statement.setInt(3, comment.userId);
            statement.setInt(4, comment.bookId);
            statement.setInt(5, comment.id);
            int affectedRows = statement.executeUpdate();
            if (affectedRows != 1) {
                throw new IllegalArgumentException(
                        "Affected rows on update: " + affectedRows);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update user", e);
        }
    }

    public Collection<Comment> getAllComment() {
        final Collection<Comment> comments = new ArrayList<>();
        final String insertTemplate =
                "SELECT * FROM comments";

        try (Statement statement = connection.createStatement()) {
            ResultSet cursor = statement.executeQuery(insertTemplate);

            while (cursor.next()) {
                comments.add(createCommentsFromCursorIfPossible(cursor));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find comments", e);
        }
        return comments;
    }

    public Comment createCommentsFromCursorIfPossible(ResultSet cursor) throws SQLException {
        Comment comment = new Comment();
        comment.date = cursor.getDate(2);
        comment.text = cursor.getString(3);
        comment.userId = cursor.getInt(4);
        comment.bookId = cursor.getInt(5);

        return comment;
    }

    public Optional<Comment> getCommentById(int id) {
        final String template = "SELECT * FROM comments WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(template)) {
            statement.setInt(1, id);
            ResultSet cursor = statement.executeQuery();
            if (!cursor.next()) {
                return Optional.empty();
            } else {
                return Optional.of(createCommentsFromCursorIfPossible(cursor));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find comment", e);
        }
    }

}
