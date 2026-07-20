package ru.nateemaru.polygon.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.nateemaru.polygon.entity.Book;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BookRepository {
    private final JdbcTemplate jdbcTemplate;

    public Book save(Book book) {
        String sql = """
                INSERT INTO books (title, author, publication_year)
                VALUES(?, ?, ?)
                RETURNING id, title, author, publication_year
                """;

        return jdbcTemplate.queryForObject(
                sql,
                this::mapRow,
                book.getTitle(),
                book.getAuthor(),
                book.getPublicationYear()
        );
    }

    public Optional<Book> findById(Long id) {
        String sql = """
                SELECT * FROM books WHERE id = ?
                """;

        return jdbcTemplate.query(sql, this::mapRow, id)
                .stream()
                .findFirst();
    }

    public Optional<Book> update(Book book) {
        String sql = """
                UPDATE books
                SET title = ?, author = ?, publication_year = ?
                WHERE id = ?
                RETURNING id, title, author, publication_year
                """;

        return jdbcTemplate.query(
                        sql,
                        this::mapRow,
                        book.getTitle(),
                        book.getAuthor(),
                        book.getPublicationYear(),
                        book.getId()
                )
                .stream()
                .findFirst();
    }

    public int delete(Long id) {
        String sql = """
                DELETE FROM books
                WHERE id = ?
                """;

        return jdbcTemplate.update(sql, id);
    }

    private Book mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Book(
                rs.getLong("id"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getInt("publication_year")
        );
    }
}
