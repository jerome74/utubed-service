package com.wlp.utubed

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import java.util.*
import java.util.stream.Collectors
import java.util.stream.Collectors.toMap as collectorsToMap

@Configuration
@EnableWebSecurity
class Config : WebSecurityConfigurerAdapter() {

    @Autowired
    lateinit var service: UsersDetailsService

    override fun configure(http : HttpSecurity){

        http
                .csrf()
                .disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(JwtUserAndPasswordAuthenticationFilter(authenticationManager()))
                .addFilterAfter(JwtTokenVerifier(),JwtUserAndPasswordAuthenticationFilter::class.java)
                .authorizeRequests()
                .antMatchers( "/utubed/**").permitAll()
                .antMatchers( "/utubed/**").hasRole(UserRole.ADMIN.name)
                .anyRequest()
                .authenticated()
    }


    override fun configure(auth: AuthenticationManagerBuilder?) {
        auth!!.userDetailsService(service).passwordEncoder(Util.crypto)
    }


    enum class UserRole(val permission : Set<UserPermissions>)
    {
        ADMIN(setOf(UserPermissions.ADMIN_READ,UserPermissions.ADMIN_WRITE,UserPermissions.ADMIN_DELETE));


         fun getGrantedAuthority(): Set<SimpleGrantedAuthority> {
            var permissions = permission.stream().map { SimpleGrantedAuthority(it.permission) }.collect(Collectors.toSet())
            permissions.add(SimpleGrantedAuthority("ROLE_" + this.name))
            return permissions
        }
    }

    enum class UserPermissions(val permission : String)
    {
        ADMIN("admin"),
        ADMIN_READ("admin:read"),
        ADMIN_WRITE("admin:write"),
        ADMIN_DELETE("admin:delete");
    }

}