package pt.unl.fct.di.iadidemo.bookshelf.presentation.controllers

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import pt.unl.fct.di.iadidemo.bookshelf.application.services.AuthorService
import pt.unl.fct.di.iadidemo.bookshelf.domain.BookDAO
import pt.unl.fct.di.iadidemo.bookshelf.application.services.BookService
import pt.unl.fct.di.iadidemo.bookshelf.config.*
import pt.unl.fct.di.iadidemo.bookshelf.domain.ImageDAO
import pt.unl.fct.di.iadidemo.bookshelf.presentation.api.BooksAPI
import pt.unl.fct.di.iadidemo.bookshelf.presentation.api.dto.*

/**
 * This is a sample class implementing the presentation logic layer for REST services,
 * the controller layer.
 *
 * Each controller implements a set of endpoints declared in a API interface. It performs
 * data format transformation and prepares answers to the REST clients.
 *
 * This controller implements two sample endpoints that use and orchestrate methods
 * from one or more components from the service layer. Notice the use of DTO classes
 * to define the types of the enpoint parameters and results. Data transformations are
 * necessary in all cases.
 */

@RestController
class BookController(val books: BookService, val authors: AuthorService) : BooksAPI {

    @CanSeeBooks
    override fun getAll(page: Int, size: Int): List<BookListDTO> =
        books.getAll(page, size).map {
            BookListDTO(
                it.id,
                it.title,
                it.authors.map { AuthorsBookDTO(it.name) },
                it.images.map { ImageDTO(it.url) }
            )
        }

        @CanAddBook
        override fun addOne(elem: BookDTO): BookListDTO {
            val authors = authors.findByIds(elem.authors) // May return 400 (invalid request) if they do not exist

            return books.addOne(BookDAO(0, elem.title, authors.toMutableList(),
                elem.images.map { ImageDAO(0, it ) })).let {
                    BookListDTO(
                        it.id,
                        it.title,
                        it.authors.map { AuthorsBookDTO(it.name) },
                        it.images.map { ImageDTO(it.url) }
                    )
                }
        }

        @CanSeeBook
        override fun getOne(id:Long): BookListDTO =
            books
                .getOne(id)
                .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found ${id}") }
                .let {
                    BookListDTO(
                        it.id,
                        it.title,
                        it.authors.map { AuthorsBookDTO(it.name) },
                        it.images.map { ImageDTO(it.url) }
                    )
                }

        @CanUpdateBook
        override fun updateOne(id: Long, elem: BookDTO): BookListDTO {
            val authors = authors.findByIds(elem.authors) // May return 400 (invalid request) if they do not exist

            return books.updateOne(id, BookDAO(0, elem.title, authors.toMutableList(),
                elem.images.map { ImageDAO(0, it ) })).let {
                    BookListDTO(
                        it.id,
                        it.title,
                        it.authors.map { AuthorsBookDTO(it.name) },
                        it.images.map { ImageDTO(it.url) }
                    )
                }
        }

        @CanDeleteBook
        override fun deleteOne(id: Long) { books.deleteOne(id) }
}