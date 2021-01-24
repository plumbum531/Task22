import models.Author;
import models.Book;
import models.Comment;
import models.User;

import java.util.Collection;
import java.util.Optional;

public interface IBookRepository {
    /**
     * @return
     * @throws RuntimeException Если что-то пошло не так во время извлечения книг из хранилища.
     */
    Collection<Book> getAllBook();

    Optional<Book> getById(int id);

    /**
     * Сохраняет книгу с написавшим ее автором.
     * Если книга или автор не сохранены, то они добавляются в хранилище.
     * Если книга или автор сохранены, то они обновляются в хранилище.
     */
    void saveBook(Book book, Author author);

    void deleteBook(int id);

    void deleteBook(Book book);

    void saveAuthor(Author author);

    void deleteAuthor(int id);

    void deleteAuthor(Author author);

    Optional<Author> getAuthorByName(String name);

    void saveUser(User user);

    void deleteUser(int id);

    void deleteUser(User user);

    Collection<Comment> getAllComments();

    void saveComment(Comment comment, User user, Book book);

    void deleteComment(int id);

    void deleteComment(Comment comment);

    Collection <Comment> getAllCommentByUserId(User user);

    void createRelationshipTableBookAuthor();

    void printTable();

}
