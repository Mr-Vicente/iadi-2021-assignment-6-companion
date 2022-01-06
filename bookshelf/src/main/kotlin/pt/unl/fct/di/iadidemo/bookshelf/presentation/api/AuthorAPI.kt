package pt.unl.fct.di.iadidemo.bookshelf.presentation.api

import org.springframework.web.bind.annotation.*
import pt.unl.fct.di.iadidemo.bookshelf.presentation.api.dto.*

@RequestMapping("authors")
interface AuthorAPI : GenAPI<AuthorsBookDTO, AuthorDTO, AuthorDTO>