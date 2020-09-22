package com.wlp.utubed

import javax.persistence.Entity
import javax.persistence.Id

class VideoInfo(var idv : String, var title : String = "", var thumbnail : String = ""  )
class AudioFile( var type : String , var payloadBase64 : String )
class finderVideo( var research : String , var listnumber : String = "" )
class SearchResult(var id :String = ""
                   ,var etag :String = ""
                   ,var kind :String = ""
                   ,var channelId :String = ""
                   ,var channelTitle :String = ""
                   ,var description :String = ""
                   ,var title :String = ""
                   ,var thumbnails :String = ""
                   ,var length : String = "")
data class UserSingIn(var nickname: String
                       , var email: String
                       , var password: String)



