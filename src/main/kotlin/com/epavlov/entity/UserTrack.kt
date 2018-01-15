package com.epavlov.entity


class UserTrack {
    var createdTime: String? = null
    var name: String? = null
    var trackId: String? = null

    override fun toString(): String {
        return "UserTrack(createdTime=$createdTime, name=$name, trackId=$trackId)"
    }
    fun toKeyBoardText():String{
        if (name.isNullOrEmpty()){
            return trackId?:""
        }else{
            return "$name\n$trackId"
        }
    }

}