package com.epavlov.entity

import java.util.*


class UserBot {

    var id: Long? = null
    var user_name: String? = null
    var createdTime: String? = null
    var first_name: String? = null
    var last_name: String? = null
    var isActive: Boolean = false
    var lastMessageTime: String? = null

    val fio: String? by lazy{
        return@lazy "${first_name!!} ${user_name!!} ${last_name!!}"
    }
    var trackList: HashMap<String, UserTrack> = HashMap()


    companion object {
        val PATH = "User"
    }

    override fun toString(): String {
        return "UserBot(id=$id, user_name=$user_name, createdTime=$createdTime, first_name=$first_name, last_name=$last_name, isActive=$isActive, lastMessageTime=$lastMessageTime, trackList=$trackList)"
    }


}