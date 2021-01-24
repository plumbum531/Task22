import dao.*;
import models.Author;
import models.Book;
import models.Comment;
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
    private final ManyToManyDao manyToManyDao;

    public JdbcBookRepository(Connection connection) {
        bookDao = new BookDao(connection);
        authorDao = new AuthorDao(connection);
        commentDao = new CommentDao(connection);
        userDao = new UserDao(connection);
        manyToManyDao = new ManyToManyDao(connection, bookDao, authorDao);

    }

    @Override
    public Collection<Book> getAllBook() {
        return bookDao.getAllBooks();
    }

    @Override
    public Optional<Book> getById(int id) {
        return bookDao.getBookById(id);
    }

    @Override
    public void saveBook(Book book, Author author) {
        if (author.id == INVALID_ID) {
            Optional<Author> authorPresent = authorDao.getAuthorByName(author.name);
            if(!authorPresent.isPresent()){
                author.id = authorDao.insertAuthor(author);
                book.authorId = author.id;
            }else {
                book.authorId = authorDao.updateAuthor(author);
            }
        } else {
            book.authorId = authorDao.updateAuthor(author);
        }

        if (book.id == INVALID_ID) {
            Optional<Book> matchingBook = bookDao.getBookByTitleAndAuthor(book.title, author.name);
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
    public void saveAuthor(Author author) {
        authorDao.insertAuthor(author);
    }

    @Override
    public Optional<Author> getAuthorByName(String name) {
        return authorDao.getAuthorByName(name);
    }

    @Override
    public void deleteAuthor(int id) {
        authorDao.delete(id);
    }

    @Override
    public void deleteAuthor(Author author) {
        authorDao.delete(author.id);
    }

    @Override
    public void saveUser(User user) {
        userDao.insertUser(user);
    }

    @Override
    public void deleteUser(int id) {
        userDao.delete(id);
    }

    @Override
    public void deleteUser(User user) {
        userDao.delete(user.idUser);
    }

    @Override
    public Collection<Comment> getAllComments() {
        return commentDao.getAllComment();
    }

    /**
     * подоразумевается, что "commet" не может быть без книги.
     */
    @Override
    public void saveComment(Comment comment, User user, Book book) {
        comment.bookId = book.id;

        if (user.idUser == INVALID_ID) {//есть ли у usera id
            Optional<User> findUser = userDao.getUserByNickName(user.nickName);
            if (!findUser.isPresent()) {
                comment.userId = userDao.insertUser(user);
            } else {
                comment.userId = findUser.get().idUser;
            }
        } else {
            Optional<User> findUser = userDao.getUserByNickName(user.nickName);
            if (findUser.isPresent()) {
                comment.userId = findUser.get().idUser;
            }
        }

        if (comment.text != null) {// порверка на нулевой коммит
            if (comment.id == INVALID_ID) {//есть ли у коммита id
                Optional<Comment> findCommet = commentDao.getCommentByText(comment);
                if (findCommet.isPresent()) {
                    comment.id = findCommet.get().id;
                    commentDao.updateComment(comment);
                } else {
                    commentDao.insertComment(comment);
                }
            } else {
                commentDao.updateComment(comment);
            }
        } else {
            System.out.println("Can not add empty comment");
        }
    }

    @Override
    public void deleteComment(int id) {
        commentDao.delete(id);
    }

    @Override
    public void deleteComment(Comment comment) {
        commentDao.delete(comment.id);
    }

    @Override
    public Collection<Comment> getAllCommentByUserId(User user) {

        Optional<User> userFromDataDase = userDao.getUserByNickName(user.nickName);
        if (userFromDataDase.isPresent()) {
            user.idUser = userFromDataDase.get().idUser;
        } else {
            System.out.println("Can not find user!");
        }
        return commentDao.getAllCommentByUserId(user);
    }

    @Override
    public void createRelationshipTableBookAuthor() {
        manyToManyDao.createSelectFromCombinatedTable();
    }
    @Override
    public void printTable(){
        manyToManyDao.printTable();
    }


}
