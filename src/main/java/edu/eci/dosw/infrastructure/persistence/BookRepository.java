package edu.eci.dosw.persistence.repository;

import edu.eci.dosw.persistence.entity.Book;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByAvailableCopiesGreaterThan(Integer availableCopies);
}
