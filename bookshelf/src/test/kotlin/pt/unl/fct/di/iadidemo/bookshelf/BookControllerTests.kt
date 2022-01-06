package pt.unl.fct.di.iadidemo.bookshelf

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import pt.unl.fct.di.iadidemo.bookshelf.domain.BookDAO
import pt.unl.fct.di.iadidemo.bookshelf.application.services.BookService
import org.springframework.security.test.context.support.WithMockUser


@RunWith(SpringRunner::class)
@SpringBootTest
@AutoConfigureMockMvc
class BookControllerTests {

    @Autowired
    lateinit var mvc:MockMvc

    @MockBean
    lateinit var books: BookService

    companion object {
        val b1 = BookDAO(1,"LOR", mutableListOf(), emptyList())
        val b2 = BookDAO(2, "Dune", mutableListOf(), emptyList())

        val l = listOf<BookDAO>(b1,b2)

        val mapper = ObjectMapper()

        val s1 = mapper.writeValueAsString(l)
    }

    @Test
    @WithMockUser(username = "user1", password = "password1", roles = ["USER"])
    fun `Test GET books`() {
        Mockito.`when`(books.getAll(0,3)).thenReturn(l)

        val s =
            mvc.perform(get("/user/books"))
                .andExpect(status().isOk())
                .andExpect(content().string(s1))
                .andReturn()
    }

    @Test
    @WithMockUser(username = "admin1", password = "password1", roles = ["ADMIN"])
    fun `Admin Test GET books`() {
        Mockito.`when`(books.getAll(0,3)).thenReturn(l)

        val s =
            mvc.perform(get("/admin/books"))
                .andExpect(status().isOk())
                .andExpect(content().string(s1))
                .andReturn()
    }
}