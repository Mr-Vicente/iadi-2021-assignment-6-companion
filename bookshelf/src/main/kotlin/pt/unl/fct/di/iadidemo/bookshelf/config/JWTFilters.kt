package pt.unl.fct.di.iadidemo.bookshelf.config

import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.authentication.logout.LogoutFilter
import org.springframework.security.web.authentication.logout.LogoutHandler
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler
import org.springframework.web.filter.GenericFilterBean
import pt.unl.fct.di.iadidemo.bookshelf.application.services.SessionService
import pt.unl.fct.di.iadidemo.bookshelf.application.services.UserService
import pt.unl.fct.di.iadidemo.bookshelf.domain.SessionDAO
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.ServletResponseWrapper
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/*
* Secret information
* */

object JWTSecret {
    private const val passphrase = "este é um grande segredo que tem que ser mantido escondido"
    val KEY: String = Base64.getEncoder().encodeToString(passphrase.toByteArray())
    const val SUBJECT = "JSON Web Token for CIAI 2019/20"
    const val VALIDITY = 1000 * 60 * 10 // 10 minutes in milliseconds
}

/*
* Server session information
* */

private class UserAuthToken(
    private var login:String,
    private var authorities: List<SimpleGrantedAuthority>,
    private var session: Long) : Authentication {

    override fun getAuthorities() = authorities

    override fun setAuthenticated(isAuthenticated: Boolean) {}

    override fun getName() = login

    override fun getCredentials() = null

    override fun getPrincipal() = this

    override fun isAuthenticated() = true

    override fun getDetails() = login

    fun getSession() = session
}

/*
* Add session info to response
* */

private fun addResponseToken(authentication: Authentication, response: HttpServletResponse, includeCredentials: Boolean) {

    val auth = authentication as UserAuthToken
    val claims = HashMap<String, Any?>()
    claims["username"] = auth.name
    claims["session"] = auth.getSession()

    val token = Jwts
        .builder()
        .setClaims(claims)
        .setSubject(JWTSecret.SUBJECT)
        .setIssuedAt(Date(System.currentTimeMillis()))
        .setExpiration(Date(System.currentTimeMillis() + JWTSecret.VALIDITY))
        .signWith(SignatureAlgorithm.HS256, JWTSecret.KEY)
        .compact()

    response.addHeader("Authorization", "Bearer $token")
    response.addHeader("Content-Type", "applications/json")
    if (includeCredentials) {
        val responseWrapper = response as ServletResponseWrapper
        responseWrapper.response.resetBuffer()
        var authoritiesList = ""
        auth.authorities.map { authoritiesList += "\"$it\"," }
        authoritiesList = authoritiesList.subSequence(0, authoritiesList.length - 1) as String
        responseWrapper.response.outputStream.write(("{\"username\": \"" + auth.name + "\", \"roles\": [" + authoritiesList + "], \"jwt\": \"" + token + "\"}").toByteArray())
    }
}

/*
* Login
* */

// Information

class LoginDTO(val username: String, val password: String) {
    constructor() : this("", "")
}

// Filter

class UserPasswordAuthenticationFilterToJWT (
    defaultFilterProcessesUrl: String?,
    private val sessions: SessionService,
    private val anAuthenticationManager: AuthenticationManager
) : AbstractAuthenticationProcessingFilter(defaultFilterProcessesUrl) {

    override fun attemptAuthentication(request: HttpServletRequest?,
                                       response: HttpServletResponse?): Authentication? {
        //getting user from request body
        val user = ObjectMapper().readValue(request!!.inputStream, LoginDTO::class.java)

        // perform the "normal" authentication
        val auth = anAuthenticationManager.authenticate(UsernamePasswordAuthenticationToken(user.username, user.password))

        return if (auth.isAuthenticated) {
            val authToken = UserAuthToken(
                auth.name,
                auth.authorities.map { SimpleGrantedAuthority(it.authority) },
                sessions.addSession(SessionDAO(0)).id
            )
            // Proceed with an authenticated user
            SecurityContextHolder.getContext().authentication = authToken
            authToken
        } else
            null
    }

    override fun successfulAuthentication(request: HttpServletRequest,
                                          response: HttpServletResponse,
                                          filterChain: FilterChain?,
                                          auth: Authentication) {

        // When returning from the Filter loop, add the token to the response
        addResponseToken(auth, response, true)
    }
}

/*
* Logout
* */

class JWTLogoutFilter(
    defaultFilterProcessesUrl: String?,
    sessions: SessionService
): LogoutFilter(defaultFilterProcessesUrl, Logout(sessions))

class Logout(private val sessions: SessionService): LogoutHandler {
    override fun logout(request: HttpServletRequest, response: HttpServletResponse, auth: Authentication) {
        val authHeader = request.getHeader("Authorization")

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            val token = authHeader.substring(7) // Skip 7 characters for "Bearer "
            val claims = Jwts.parser().setSigningKey(JWTSecret.KEY).parseClaimsJws(token).body

            // disable session
            sessions.deleteSession((claims["session"] as Int).toLong())
        }
    }
}

class LogoutSuccess(): LogoutSuccessHandler {
    override fun onLogoutSuccess(request: HttpServletRequest, response: HttpServletResponse, auth: Authentication) {
        response.status = HttpServletResponse.SC_OK
    }
}

/*
* Authentication
* */

class JWTAuthenticationFilter(private val users: UserService,
                              private val sessions: SessionService): GenericFilterBean() {

    // To try it out, go to https://jwt.io to generate custom tokens, in this case we only need a name...

    override fun doFilter(request: ServletRequest?,
                          response: ServletResponse?,
                          chain: FilterChain?) {

        val authHeader = (request as HttpServletRequest).getHeader("Authorization")

        if( authHeader != null && authHeader.startsWith("Bearer ") ) {
            val token = authHeader.substring(7) // Skip 7 characters for "Bearer "
            try {
                val claims = Jwts.parser().setSigningKey(JWTSecret.KEY).parseClaimsJws(token).body

                // should check for token validity here (e.g. expiration date, session in db, etc.)
                val exp = (claims["exp"] as Int).toLong()
                val sessionInfo = sessions
                    .findSession((claims["session"] as Int).toLong())
                    .orElse(null)
                if (exp < System.currentTimeMillis()/1000 || sessionInfo == null) // in seconds
                    (response as HttpServletResponse).sendError(HttpServletResponse.SC_UNAUTHORIZED) // RFC 6750 3.1
                else {
                    val session = sessionInfo.id
                    val log = claims["username"] as String
                    val authorities = users.findUser(log)
                        .orElse(null)
                        .roles.map { SimpleGrantedAuthority("ROLE_${it.tag}") }
                    val authentication = UserAuthToken(log, authorities, session)
                    // Can go to the database to get the actual user information (e.g. authorities)

                    SecurityContextHolder.getContext().authentication = authentication

                    // Renew token with extended time here. (before doFilter)
                    addResponseToken(authentication, response as HttpServletResponse, false)

                    chain!!.doFilter(request, response)
                }
            } catch (e: ExpiredJwtException) {
                (response as HttpServletResponse).sendError(HttpServletResponse.SC_UNAUTHORIZED) // RFC 6750 3.1
            }
        } else {
            chain!!.doFilter(request, response)
        }
    }
}
