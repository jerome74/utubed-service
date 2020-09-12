package com.wlp.utubed

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import java.io.File
import com.github.kiulian.downloader.YoutubeDownloader
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.youtube.YouTube
import it.sauronsoftware.jave.AudioAttributes;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.EncodingAttributes;
import it.sauronsoftware.jave.InputFormatException;
import java.text.SimpleDateFormat
import java.util.*


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

    /**
     * @Throws(Exception::class)
     * @return Unit
     */

    @Throws(Exception::class)
    fun getInfo(info: VideoInfo) {

        val video = downloader!!.getVideo(info.idv);

        val details = video.details();

        info.title      = details.title()
        info.thumbnail  = details.thumbnails()[0]
    }

    /**
     * @Throws(Exception::class, IllegalArgumentException::class, InputFormatException::class, EncoderException::class)
     * @return String
     */

    @Throws(Exception::class, IllegalArgumentException::class, InputFormatException::class, EncoderException::class)
    fun download(info: VideoInfo, type : String) : File{

        val video = downloader!!.getVideo(info.idv);

        val videoWithAudioFormats = video.videoWithAudioFormats();
        videoWithAudioFormats.forEach{println("${it.audioQuality()} = ${it.url()}")}

        val outputDir = kotlin.io.createTempDir("videos_");
        val format = videoWithAudioFormats.get(0);

        // sync downloading
        val source = video.download(format, outputDir);
        val target = kotlin.io.createTempFile("videos_",".mp3", outputDir);

        val audio	= AudioAttributes()

        if(type.equals("mp3"))
            audio.setCodec("libmp3lame")

        audio.setBitRate(128000)
        audio.setChannels(2)
        audio.setSamplingRate(44100)

        val attrs = EncodingAttributes();

        attrs.setFormat("mp3");
        attrs.setAudioAttributes(audio);

        val encoder = Encoder();

        encoder.encode(source, target, attrs)

        outputDir.deleteOnExit()

        return target
    }



    fun findVideo(research : String) : List<SearchResult>{

        var  youtube = YouTube.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory(), HttpRequestInitializer() {}).setApplicationName("youtube-cmdline-search-sample").build()

        // api key
        val apiKey = "AIzaSyARDAkWqWZZ4a8PPCn3SdojXqrDfwVXZ2g"

        // Define the API request for retrieving search results.
        val search = youtube.search().list("id,snippet");

        // Set your developer key from the Google Cloud Console for
        // non-authenticated requests. See:
        // https://cloud.google.com/console
        search.setKey(apiKey);
        search.setQ(research);

        // To increase efficiency, only retrieve the fields that the
        // application uses.
        search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
        search.setMaxResults(10);

        // Call the API and print results.
        val searchResponse = search.execute()
        val searchResultList = searchResponse.getItems();

        var itsearch = searchResultList.iterator();

        var result = mutableListOf<SearchResult>()

        var sdf = SimpleDateFormat("HH:mm:ss")
        sdf.timeZone = TimeZone.getTimeZone("GMT - 01:00")

        while (itsearch.hasNext()) {

            var searchResult = itsearch.next()
            var snippet = searchResult.snippet

            var singleSearchResult = SearchResult()

            try{

                if(!searchResult.id.videoId.isNullOrBlank()) {
                    singleSearchResult.id = searchResult.id.videoId
                    singleSearchResult.length = sdf.format(Date((downloader!!.getVideo(singleSearchResult.id).details().lengthSeconds() * 1000).toLong()))
                }

                if(!searchResult.etag.isNullOrBlank())          singleSearchResult.etag             = searchResult.etag
                if(!searchResult.kind.isNullOrBlank())          singleSearchResult.kind             = searchResult.kind
                if(!snippet.channelId.isNullOrBlank())          singleSearchResult.channelId        = snippet.channelId
                if(!snippet.channelTitle.isNullOrBlank())       singleSearchResult.channelTitle     = snippet.channelTitle
                if(!snippet.description.isNullOrBlank())        singleSearchResult.description      = snippet.description
                if(!snippet.title.isNullOrBlank())              singleSearchResult.title            = snippet.title
                if(!snippet.thumbnails.isEmpty())               singleSearchResult.thumbnails       = snippet.thumbnails.toString()


            }catch (e : Exception){continue}

            result.add(singleSearchResult)

        }

        return result

    }
}
