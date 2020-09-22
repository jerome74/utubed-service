package com.wlp.utubed

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*


@Repository
interface UserprofileRepository : JpaRepository<Userprofile, Int> {
    fun findByEmail(email: String): Optional<Userprofile>
    fun findTopByOrderByIdDesc() : Optional<Userprofile>
}

@Repository
interface UsersRepository : JpaRepository<Users, Int> {

    fun findByUsername(username: String): Optional<Users>
    fun findTopByOrderByIdDesc() : Optional<Users>
}