package pt.unl.fct.di.iadidemo.bookshelf.presentation.controllers

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import pt.unl.fct.di.iadidemo.bookshelf.application.services.AuthorService
import pt.unl.fct.di.iadidemo.bookshelf.config.*
import pt.unl.fct.di.iadidemo.bookshelf.domain.AuthorDAO
import pt.unl.fct.di.iadidemo.bookshelf.presentation.api.AuthorAPI
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
class AuthorController(val authors: AuthorService) : AuthorAPI {

    @CanSeeAuthors
    override fun getAll(page: Int, size: Int): List<AuthorDTO> =
        authors.getAll().map { AuthorDTO(
            it.id,
            it.name
        ) }

    @CanSeeAuthors
    override fun addOne(elem: AuthorsBookDTO): AuthorDTO =
        authors.addOne(AuthorDAO(0, elem.name))
            .let { AuthorDTO(
                it.id,
                it.name
            ) }

    @CanSeeAuthors
    override fun getOne(id: Long): AuthorDTO =
        authors
            .getOne(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found ${id}") }
            .let {
                AuthorDTO(
                    it.id,
                    it.name
                )
            }

    @CanSeeAuthors
    override fun updateOne(id: Long, elem: AuthorsBookDTO):AuthorDTO {
        TODO("Not yet implemented")
    }

    @CanSeeAuthors
    override fun deleteOne(id: Long) {
        authors.deleteOne(id)
    }

}