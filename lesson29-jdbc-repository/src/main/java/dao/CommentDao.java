package dao;

import java.sql.Connection;

public class CommentDao {
    private final Connection connection;

    public CommentDao(Connection connection) {
        this.connection = connection;
    }


}
