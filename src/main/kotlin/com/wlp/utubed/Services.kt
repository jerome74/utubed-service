package com.wlp.utubed

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import java.io.File
import com.github.kiulian.downloader.YoutubeDownloader
import javax.annotation.PostConstruct


@Service
class UsersDetailsService : UserDetailsService {

    @Autowired
    lateinit var repository : UsersRepository


    override fun loadUserByUsername(username: String?): UserDetails
    {
        var builder : User.UserBuilder? = null
        repository.findByUsername(username!!).ifPresent({

            builder = User.withUsername(it.username);
            builder!!.password(Util.crypto.encode(it.password));
            builder!!.authorities(Config.UserRole.ADMIN.getGrantedAuthority())

        })

        return builder!!.build()
        }

}

@Service
class UtubeD {

    var downloader : YoutubeDownloader? = null

    init {
        // init downloader
        downloader = YoutubeDownloader();


        // or just extend functionality via existing API
        // cipher features
        downloader!!.addCipherFunctionPattern(2, "\\b([a-zA-Z0-9$]{2})\\s*=\\s*function\\(\\s*a\\s*\\)\\s*\\{\\s*a\\s*=\\s*a\\.split\\(\\s*\"\"\\s*\\)")
        downloader!!.setParserRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36")
        downloader!!.setParserRetryOnFailure(1)
    }

    @Throws(Exception::class)
    fun getInfo(info: VideoInfo)
    {
        val video = downloader!!.getVideo(info.idv);

        val details = video.details();

        info.title      = details.title()
        info.thumbnail  = details.thumbnails()[0]
    }
}
