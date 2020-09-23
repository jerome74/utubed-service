package com.wlp.utubed

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.io.File
import java.io.FileInputStream

@RestController
@RequestMapping("/api/utubed")
class RestLocationController{

    @Autowired
    lateinit var utubeD: UtubeD

    @Autowired
    lateinit var register: Register

    @Autowired
    lateinit var userprofileRepository: UserprofileRepository

    @Autowired
    lateinit var config: ConfigProperties

    @Autowired
    lateinit var usersRepository: UsersRepository

    @PostMapping(path = ["/login"] , produces= [MediaType.APPLICATION_JSON_VALUE] )
    fun login(@RequestBody credential : UserAndPasswordAuthenticationRequest) {
    }

    @PostMapping(path = ["/signin"] , produces= [MediaType.APPLICATION_JSON_VALUE] )
    fun signin(@RequestBody credential : UserSingIn) : ResponseEntity<String> {
        if(!register.registerUser(credential))
            return ResponseEntity(HttpStatus.UNAUTHORIZED);
        else
            return ResponseEntity(HttpStatus.OK);
    }

    @GetMapping(path = ["/confirm/{email}"] , produces= [MediaType.APPLICATION_JSON_VALUE] )
    fun confirm(@PathVariable email : String) : String? {
        val user = usersRepository.findByUsername(email).orElseThrow { RuntimeException("Any User Found!") }
        user.active = true
        usersRepository.save(user)

        return config.getPropertes("mail.ok")

    }

    @GetMapping(path=["/profile/{email}"], produces=[MediaType.APPLICATION_JSON_VALUE] )
    fun getProfileByEmail(@PathVariable email : String) : Userprofile {
        return userprofileRepository.findByEmail(email).orElseThrow { RuntimeException("Any Profile Found!") }
    }

    @PostMapping(path = ["/info"] , produces= [MediaType.APPLICATION_JSON_VALUE] )
    fun info(@RequestBody info : VideoInfo) : VideoInfo {
        utubeD.getInfo(info)
        return info
    }

    @PostMapping(path = ["/download/{type}"] , produces= [MediaType.APPLICATION_JSON_VALUE] )
    fun download(@PathVariable type : String, @RequestBody info : VideoInfo) : ResponseEntity<Resource> {

        val header =  HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=download.mp3");
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");

        var fileAudio : File? = null

        when(type)
        {
            "mp3" -> fileAudio = utubeD.downloadMp3(info)
            "mp4" -> fileAudio = utubeD.downloadMp4(info)
        }

        val resource = InputStreamResource(FileInputStream(fileAudio!!));

        return ResponseEntity.ok()
                .contentLength(fileAudio!!.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @PostMapping(path = ["/find"] , produces= [MediaType.APPLICATION_JSON_VALUE] )
    fun find(@RequestBody finder : finderVideo) : List<SearchResult> {

        return utubeD.findVideo(finder.research)
    }

}