package com.wlp.utubed

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/utubed")
class RestLocationController{

    @Autowired
    lateinit var utubeD: UtubeD

    @PostMapping(path = ["/login"] , produces= [MediaType.APPLICATION_JSON_VALUE] )
    fun login(@RequestBody credential : UserAndPasswordAuthenticationRequest) {
    }

    @PostMapping(path = ["/info"] , produces= [MediaType.APPLICATION_JSON_VALUE] )
    fun info(@RequestBody info : VideoInfo) : VideoInfo {
        utubeD.getInfo(info)
        return info
    }

    @PostMapping(path = ["/download/{type}"] , produces= [MediaType.APPLICATION_JSON_VALUE] )
    fun download(@PathVariable type : String, @RequestBody info : VideoInfo) : AudioFile {

        return AudioFile(type,utubeD.download(info, type))
    }

    @PostMapping(path = ["/find"] , produces= [MediaType.APPLICATION_JSON_VALUE] )
    fun find(@RequestBody finder : finderVideo) : List<SearchResult> {

        return utubeD.findVideo(finder.research)
    }

}