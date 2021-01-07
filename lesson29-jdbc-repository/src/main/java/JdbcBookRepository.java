import dao.AuthorDao;
import dao.BookDao;
import dao.CommentDao;
import dao.UserDao;
import models.Author;
import models.Book;
import models.User;

import java.sql.Connection;
import java.util.Collection;
import java.util.Optional;

public class JdbcBookRepository implements IBookRepository {
    private static final int INVALID_ID = 0;

    private final AuthorDao authorDao;
    private final BookDao bookDao;
    private final CommentDao commentDao;
    private final UserDao userDao;

    public JdbcBookRepository(Connection connection) {
        bookDao = new BookDao(connection);
        authorDao = new AuthorDao(connection);
        commentDao = new CommentDao(connection);
        userDao = new UserDao(connection);

        String expr = "SELECT books.title, authors.name" +
                " FROM books" +
                " JOIN authors_books ON books.id = authors_books.book_id" +
                " JOIN authors ON authors.id = authors_books.author_id" +
                " WHERE books.publish_year > 2000";

    }

    @Override
    public Collection<Book> getAll() {
        return bookDao.getAllBooks();
    }

    @Override
    public Optional<Book> getById(int id) {
        return bookDao.getBookById(id);
    }

    @Override
    public void save(Book book, Author author) {
        if (author.id == INVALID_ID) {
            author.id = authorDao.insertAuthor(author);
        } else {
            authorDao.updateAuthor(author);
        }

        book.authorId = author.id;

        if (book.id == INVALID_ID) {
            Optional<Book> matchingBook =
                    bookDao.getBookByTitleAndAuthor(book.title, author.name);
            if (matchingBook.isPresent()) {
                book.id = matchingBook.get().id;
                bookDao.updateBook(book);
            } else {
                book.id = bookDao.insertBook(book);
            }
        } else {
            bookDao.updateBook(book);
        }
    }

    @Override
    public void deleteBook(int id) {
        bookDao.delete(id);
    }

    @Override
    public void deleteBook(Book book) {
        bookDao.delete(book.id);
    }

    @Override
    public void deleteAuthor(int id) {
        authorDao.delete(id);
    }

    @Override
    public void deleteAuthor(Author author) {
        authorDao.delete(author.id);
    }
}
