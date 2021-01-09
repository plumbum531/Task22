package dao;

import models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public class UserDao {
    private final Connection connection;

    public UserDao(Connection connection) {
        this.connection = connection;
    }

    public void delete(int id) {
        final String template = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(template)) {
            statement.setInt(1, id);
            int affectedRows = statement.executeUpdate();
            if (affectedRows != 1) {
                throw new IllegalArgumentException(
                        "Affected rows on delete: " + affectedRows);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete user", e);
        }
    }

    public Optional<User> getUserById(int id) {
        try (Statement statement = connection.createStatement()) {
            ResultSet cursor = statement.executeQuery(
                    "SELECT * FROM users WHERE id = " + id);
            if (!cursor.next()) {
                return Optional.empty();
            }

            return Optional.of(createUserFromCursorIfPossible(cursor));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch one book", e);
        }
    }

    public Optional<User> getUserByNickName(String nickName) {
        final String insertTemlate = "SELECT * FROM users WHERE nickName = ? LIMIT 1";
        try ( PreparedStatement statement = connection.prepareStatement(insertTemlate)) {
            statement.setString(1, nickName);
            ResultSet cursor = statement.executeQuery();
            if (!cursor.next()) {
                return Optional.empty();
            }
            return Optional.of(createUserFromCursorIfPossible(cursor));
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch one book", e);
        }
    }

    private User createUserFromCursorIfPossible(ResultSet cursor) throws SQLException {
        final User user = new User();
        user.idUser = cursor.getInt("id");
        user.registryDate = cursor.getString("registryDate");
        user.nickName = cursor.getString("nickName");
        return user;
    }

    public int insertUser(User user) {
        final String insertTemplate =
                "INSERT INTO users(registryDate,nickName) VALUES(?,?)";

        try (PreparedStatement statement = connection.prepareStatement(insertTemplate)) {
            statement.setString(1, user.registryDate);
            statement.setString(2, user.nickName);
            statement.executeUpdate();

            ResultSet cursor = statement.getGeneratedKeys();
            if (!cursor.next()) {
                throw new RuntimeException("Failed to insert user");
            }

            return cursor.getInt(1);//возвращает userId
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert user", e);
        }
    }

    public void updateUser(User user) {
        final String updateTemplate =
                "UPDATE users SET registryDate = ?, nickName = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(updateTemplate)) {
            statement.setString(1, user.registryDate);
            statement.setString(2, user.nickName);
            statement.setInt(3, user.idUser);
            int affectedRows = statement.executeUpdate();
            if (affectedRows != 1) {
                throw new IllegalArgumentException(
                        "Affected rows on update: " + affectedRows);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update user", e);
        }
    }

    public Collection<User> getAllUser() {
        final Collection<User> users = new ArrayList<>();
        final String insertTemplate =
                "SELECT * FROM users";

        try (Statement statement = connection.createStatement()) {
            ResultSet cursor = statement.executeQuery(insertTemplate);

            while (cursor.next()) {
                users.add(createUserFromCursorIfPossible(cursor));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find users", e);
        }
        return users;
    }
}
