package pt.unl.fct.di.iadidemo.bookshelf

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import pt.unl.fct.di.iadidemo.bookshelf.domain.*
import javax.transaction.Transactional

@SpringBootApplication
class SecurityApplication(
    val books:BookRepository,
    val users:UserRepository,
    val roles:RoleRepository,
    val authors:AuthorRepository
) : CommandLineRunner {

    @Transactional
    override fun run(vararg args: String?) {

        val r1 = RoleDAO("ADMIN")
        val r2 = RoleDAO("REVIEWER")
        val r3 = RoleDAO("USER")
        roles.saveAll(listOf(r1, r2, r3))

        // Dummy users
        users.save(UserDAO("user",BCryptPasswordEncoder().encode("password"), listOf(r3),"User"))
        users.save(UserDAO("admin",BCryptPasswordEncoder().encode("password"), listOf(r1),"Admin"))

        val u1 = UserDAO("user1",BCryptPasswordEncoder().encode("password1"),listOf(r3,r2),"User 1")
        users.save(u1)

        val u2 = UserDAO("admin1",BCryptPasswordEncoder().encode("password1"), listOf(r1),"Admin 1")
        users.save(u2)

        val a1 = AuthorDAO(0,"Philip K. Dick")
        authors.save(a1)

        val b1 = BookDAO(0,"Ubik", mutableListOf(a1), listOf(ImageDAO(0, "https://covers.openlibrary.org/b/id/9251896-L.jpg")))
        val b2 = BookDAO(0,"Do Androids Dream of Electric Sheep?", mutableListOf(a1), listOf(ImageDAO(0, "https://covers.openlibrary.org/b/id/11153217-L.jpg")))
        val b3 = BookDAO(0,"The Man in the High Castle", mutableListOf(a1), listOf(ImageDAO(0, "https://covers.openlibrary.org/b/id/10045188-L.jpg")))

        val b4 = BookDAO(0,"Ubik", mutableListOf(a1), listOf(ImageDAO(0, "https://sm.ign.com/ign_br/screenshot/default/harry-potter-hbo-max_q8yn.jpg")))
        val b5 = BookDAO(0,"Do Androids Dream of Electric Sheep?", mutableListOf(a1), listOf(ImageDAO(0, "https://exame.com/wp-content/uploads/2019/07/untitled-5-2.png")))
        val b6 = BookDAO(0,"The Man in the High Castle", mutableListOf(a1), listOf(ImageDAO(0, "https://gkpb.com.br/wp-content/uploads/2021/12/gkpb-cinemark-reexibe-harry-potter.jpg")))

        val b7 = BookDAO(0,"Ubik", mutableListOf(a1), listOf(ImageDAO(0, "https://static.globalnoticias.pt/dn/image.jpg?brand=DN&type=generate&guid=454e5789-8c37-4e1e-95b3-39529dad3740&w=800&h=450&t=20211117100639")))
        val b8 = BookDAO(0,"Do Androids Dream of Electric Sheep?", mutableListOf(a1), listOf(ImageDAO(0, "https://t.ctcdn.com.br/w648g5hnt0u3-OvYjGE2l3OT0Fs=/512x288/smart/i525540.jpeg")))
        val b9 = BookDAO(0,"The Man in the High Castle", mutableListOf(a1), listOf(ImageDAO(0, "https://sm.ign.com/t/ign_br/news/h/heres-our-/heres-our-first-look-at-harry-potter-20th-anniversary-return_8y39.620.jpg")))

        val b10 = BookDAO(0,"The Man in the High Castle", mutableListOf(a1), listOf(ImageDAO(0, "https://sm.ign.com/t/ign_br/news/h/heres-our-/heres-our-first-look-at-harry-potter-20th-anniversary-return_8y39.620.jpg")))

        books.saveAll(listOf(b1,b2,b3,b4,b5,b6,b7,b8,b9,b10))
    }

}

fun main(args: Array<String>) {
    runApplication<SecurityApplication>(*args)
}
