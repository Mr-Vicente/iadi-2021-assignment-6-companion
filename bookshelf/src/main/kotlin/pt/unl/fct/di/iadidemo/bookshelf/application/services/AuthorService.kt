package pt.unl.fct.di.iadidemo.bookshelf.application.services

import org.springframework.stereotype.Service
import pt.unl.fct.di.iadidemo.bookshelf.application.services.exceptions.NoAuthorException
import pt.unl.fct.di.iadidemo.bookshelf.application.services.exceptions.NoBookException
import pt.unl.fct.di.iadidemo.bookshelf.domain.AuthorDAO
import pt.unl.fct.di.iadidemo.bookshelf.domain.AuthorRepository
import java.util.*

@Service
class AuthorService(val authors:AuthorRepository) {

    fun findByIds(ids:List<Long>): List<AuthorDAO> =
        ids.map { authors.findById(it) }.filter { it.isPresent }.map { it.get() }

    fun getAll(): List<AuthorDAO> = authors.findAll().toList()

    fun addOne(author: AuthorDAO):AuthorDAO = authors.save(author)

    fun getOne(id:Long):Optional<AuthorDAO> = authors.findById(id)

    fun deleteOne(id:Long) { authors.deleteById(id) }

}