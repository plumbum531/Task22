import models.Author;
import models.Book;

import java.util.Collection;
import java.util.Optional;

public interface IBookRepository {
    /**
     * @return
     * @throws RuntimeException Если что-то пошло не так во время извлечения книг из хранилища.
     */
    Collection<Book> getAll();

    Optional<Book> getById(int id);

    /**
     * Сохраняет книгу с написавшим ее автором.
     * Если книга или автор не сохранены, то они добавляются в хранилище.
     * Если книга или автор сохранены, то они обновляются в хранилище.
     */
    void save(Book book, Author author);
//
//    void save(Author author);

    void deleteBook(int id);
    void deleteBook(Book book);
    void deleteAuthor(int id);
    void deleteAuthor(Author author);
}
