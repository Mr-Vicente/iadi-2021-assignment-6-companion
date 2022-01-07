package pt.unl.fct.di.iadidemo.bookshelf.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import pt.unl.fct.di.iadidemo.bookshelf.application.services.SessionService
import pt.unl.fct.di.iadidemo.bookshelf.application.services.UserService


@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
class SecurityConfig(
    val customUserDetails:CustomUserDetailsService,
    val users: UserService,
    val sessions: SessionService
) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http
            .cors().and().csrf().disable() // This allows applications to access endpoints from any source location
            .authorizeRequests()
            .antMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
            .antMatchers("/user/books").permitAll()
            .anyRequest().authenticated()
            .and().httpBasic()
            // Missing the sign-up, sign-in and sign-out endpoints
            // Missing the configuration for filters
            .and().logout { logout -> logout
                .permitAll()
                .addLogoutHandler(Logout(sessions))
                .logoutSuccessHandler(LogoutSuccess())
            }
            .addFilterBefore(UserPasswordAuthenticationFilterToJWT ("/login", sessions,
                super.authenticationManagerBean()),
                BasicAuthenticationFilter::class.java)
            .addFilterBefore(JWTAuthenticationFilter(users, sessions),
                BasicAuthenticationFilter::class.java)

    }

    override fun configure(auth: AuthenticationManagerBuilder) {
        auth
            // To declare two users with
            .inMemoryAuthentication()
            .withUser("user")
            .password(BCryptPasswordEncoder().encode("password"))
            .roles("USER")

            .and()
            .withUser("admin")
            .password(BCryptPasswordEncoder().encode("password"))
            .roles("ADMIN")

            // Set the way passwords are encoded in the system
            .and()
            .passwordEncoder(BCryptPasswordEncoder())

            // Connect spring security to the domain/data model
            .and()
            .userDetailsService(customUserDetails)
            .passwordEncoder(BCryptPasswordEncoder())
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource? {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.allowCredentials = true
        config.addAllowedOrigin("http://localhost:3000")
        config.addAllowedHeader("*")
        config.addAllowedMethod("*")
        source.registerCorsConfiguration("/**", config)
        return source
    }
}