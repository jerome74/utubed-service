package com.wlp.utubed

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.PropertySource
import org.springframework.core.env.Environment
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

object Util
{
    val crypto = BCryptPasswordEncoder(12)
}

@Component
@PropertySource("classpath:application.properties")
class ConfigProperties
{
    @Autowired
    lateinit var env : Environment

    fun getPropertes(prop : String) : String? = env.getProperty(prop)
}

/*
heroku create
heroku apps:rename --app still-bastion-07273 utubed
git add .
git commit -m "message"
git
git push heroku master
git remote add heroku https://git.heroku.com/utubed.git
*/