package com.wlp.utubed

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

object Util
{
    val crypto = BCryptPasswordEncoder(12)
}

/*
heroku create
heroku apps:rename --app still-bastion-07273 utubed
git add .
git commit -m "message"
git push heroku master

 */