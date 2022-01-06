package pt.unl.fct.di.iadidemo.bookshelf.application.services

import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import pt.unl.fct.di.iadidemo.bookshelf.application.services.exceptions.NoBookException
import pt.unl.fct.di.iadidemo.bookshelf.domain.BookDAO
import pt.unl.fct.di.iadidemo.bookshelf.domain.BookRepository
import java.util.*

/**
 * This is a sample class implementing the application logic layer, the service layer.
 *
 * Each service should implement logic that is closely related. It can use
 * other services to orchestrate its functionality.
 *
 * This service declares a dependency for the repository service in the data
 * layer that manipulates the information about books. Notice that the class
 * is annotated with @Service so that Spring can instantiate the corresponding
 * Bean and connect to other components.
 *
 * This implements two sample methods that use and orchestrate methods from the
 * database repository. It can also perform intermediate computations to prepare
 * data.
 */
@Service
class BookService(val books: BookRepository) {

    fun getAll(page: Int, size: Int): List<BookDAO> = books.findAll(PageRequest.of(page,size)).toList()

    fun addOne(book: BookDAO): BookDAO = books.save(book)

    fun getOne(id:Long): Optional<BookDAO> = books.findById(id)

    fun updateOne(id:Long, update: BookDAO): BookDAO {
        val maybeBook = books.findById(id)

        val book = maybeBook.orElseThrow { NoBookException("Book with ${id} was found") }
        book.authors = update.authors
        book.images = update.images
        book.title = update.title

        return books.save(book)
    }

    fun deleteOne(id:Long) { books.deleteById(id) }

}
