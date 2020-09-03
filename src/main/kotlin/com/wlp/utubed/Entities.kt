package com.wlp.utubed

import java.sql.Date
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.SequenceGenerator



@Entity
data class Users(@Id var id: Int, var username: String, var password: String, var active: Boolean)

@Entity
data class Userprofile(@Id var id: Int
                       , var nickname: String
                       , var email: String
                       , var avatarname: String
                       , var avatarcolor: String)