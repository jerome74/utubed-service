package com.wlp.utubed

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/utubed")
class RestLocationController{

    @Autowired
    lateinit var utubeD: UtubeD

    @PostMapping(path = ["/login"] , produces= [MediaType.APPLICATION_JSON_VALUE] )
    fun login(@RequestBody credential : UserAndPasswordAuthenticationRequest) {
    }

    @PostMapping(path = ["/info"] , produces= [MediaType.APPLICATION_JSON_VALUE] )
    fun login(@RequestBody info : VideoInfo) : VideoInfo {
        utubeD.getInfo(info)
        return info
    }
}