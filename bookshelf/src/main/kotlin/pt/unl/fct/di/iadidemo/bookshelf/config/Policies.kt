package pt.unl.fct.di.iadidemo.bookshelf.config

import org.springframework.security.access.prepost.PreAuthorize

@PreAuthorize(CanSeeBook.condition)
annotation class CanSeeBook {
    companion object {
        const val condition:String = "true"
    }
}

@PreAuthorize(CanSeeBooks.condition)
annotation class CanSeeBooks {
    companion object {
        const val condition:String = "true"
    }
}

@PreAuthorize(CanSeeAuthors.condition)
annotation class CanSeeAuthors {
    companion object {
        const val condition:String = "true"
    }
}

@PreAuthorize(CanAddBook.condition)
annotation class CanAddBook {
    companion object {
        const val condition:String = "hasRole('USER') or " + CanUpdateBook.condition
    }
}

@PreAuthorize(CanUpdateBook.condition)
annotation class CanUpdateBook {
    companion object {
        const val condition:String = "hasRole('REVIEWER') or " + CanDeleteBook.condition
    }
}

@PreAuthorize(CanDeleteBook.condition)
annotation class CanDeleteBook {
    companion object {
        const val condition:String = "hasRole('ADMIN') or @securityService.isOwnerOfBook(principal, #id)"
    }
}

