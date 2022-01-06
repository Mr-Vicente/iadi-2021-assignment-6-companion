package pt.unl.fct.di.iadidemo.bookshelf.presentation.api.dto



data class BookDTO(val title:String, val authors:List<Long>, val images:List<String>)

data class ImageDTO(val url:String)

data class BookListDTO(val id:Long, val title:String, val authors:List<AuthorsBookDTO>, val images:List<ImageDTO>)

data class AuthorsBookDTO(val name:String)

data class AuthorDTO(val id:Long, val name:String)



