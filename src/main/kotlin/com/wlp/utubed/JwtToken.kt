package com.wlp.utubed

import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.filter.OncePerRequestFilter
import java.lang.IllegalStateException
import java.util.*
import java.util.stream.Collectors
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class UserAndPasswordAuthenticationRequest() {
    lateinit var username: String
    lateinit var password: String


}

class JwtUserAndPasswordAuthenticationFilter(val authent: AuthenticationManager) : UsernamePasswordAuthenticationFilter() {


    override fun attemptAuthentication(request: HttpServletRequest?, response: HttpServletResponse?): Authentication {

        val authenticationRequest = ObjectMapper().readValue<UserAndPasswordAuthenticationRequest>(request!!.inputStream, UserAndPasswordAuthenticationRequest::class.java)
        val authentication = UsernamePasswordAuthenticationToken(authenticationRequest.username, authenticationRequest.password)

        val auth = authent.authenticate(authentication)

        return auth
    }


    override fun successfulAuthentication(request: HttpServletRequest?, response: HttpServletResponse?, chain: FilterChain?, authResult: Authentication?) {


        val cal = Calendar.getInstance()
        cal.add(Calendar.MINUTE, 30)
        val to30min = cal.time

        val charset = Charsets.UTF_8
        val token = "pneumonoultramicroscopicsilicovolcanoconiosis".toByteArray(charset)

        val jwttoken = Jwts.builder()
                .setSubject(authResult!!.name)
                .claim("authorities", authResult!!.authorities)
                .setIssuedAt(Date())
                .setExpiration(to30min)
                .signWith(Keys.hmacShaKeyFor(token))
                .compact()

        response!!.addHeader("Authentication", "Bearer $jwttoken")

    }

}

class JwtTokenVerifier : OncePerRequestFilter()
{
    @Throws(IllegalStateException::class)
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {


        val authoritation = request.getHeader("Authentication")

        if(request.requestURI.equals("/api/utubed/signin") || request.requestURI.startsWith("/api/utubed/confirm")){

            val authenticationAuth = UsernamePasswordAuthenticationToken("guest",null,Config.UserRole.ADMIN.getGrantedAuthority())
            SecurityContextHolder.getContext().authentication = authenticationAuth
            filterChain.doFilter(request,response)
        }
        else if(authoritation.isNullOrBlank() || !authoritation.startsWith("Bearer")){
            filterChain.doFilter(request,response)
            return
        }
        else{
            val token = authoritation.replace("Bearer " , "")
            val charset = Charsets.UTF_8
            val claimJwts = Jwts.parser()
                    .setSigningKey(Keys.hmacShaKeyFor("pneumonoultramicroscopicsilicovolcanoconiosis".toByteArray(charset)))
                    .parseClaimsJws(token)

            val username = claimJwts.body.subject
            val authorities = claimJwts.body.get("authorities") as List<Map<String, String>>

            val simpleGrantedAuthority = authorities.stream().map { SimpleGrantedAuthority(it.get("authority")) }.collect(Collectors.toSet())
            val authenticationAuth = UsernamePasswordAuthenticationToken(username,null,simpleGrantedAuthority)

            SecurityContextHolder.getContext().authentication = authenticationAuth

            filterChain.doFilter(request,response)

        }
    }
}