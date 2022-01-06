package pt.unl.fct.di.iadidemo.bookshelf.application.services

import org.springframework.stereotype.Service
import pt.unl.fct.di.iadidemo.bookshelf.domain.SessionDAO
import pt.unl.fct.di.iadidemo.bookshelf.domain.SessionRepository
import java.util.*

@Service
class SessionService(val sessions: SessionRepository) {

    fun findSession(id: Long): Optional<SessionDAO> = sessions.findById(id)

    fun addSession(session: SessionDAO): SessionDAO = sessions.save(session)

    fun deleteSession(id: Long): Unit { sessions.deleteById(id) }

}