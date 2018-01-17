package com.epavlov.entity

import com.google.firebase.database.Exclude
import org.telegram.telegrambots.api.objects.User
import java.time.LocalDateTime
import java.util.*


class UserBot {
    var id: Long? = null
    var user_name: String? = null
    var createdTime: String? = null
    var first_name: String? = null
    var last_name: String? = null
    var isActive: Boolean = false
    var lastMessageTime: String? = null


    val fio: String?
        @Exclude get() = "${first_name ?: ""} ${user_name ?: ""} ${last_name ?: ""}".trim().plus(" $id").replace("  ", " ")


    var trackList: HashMap<String, UserTrack> = HashMap()

    constructor()
    constructor(user: User) {
        this.id = user.id.toLong()
        this.isActive = true
        this.createdTime = LocalDateTime.now().toString()
        this.first_name = user.firstName
        this.last_name = user.lastName
        this.user_name = user.userName
        this.lastMessageTime = LocalDateTime.now().toString()
    }

    companion object {
        val PATH = "User"
    }

    override fun toString(): String {
        return "UserBot(id=$id, user_name=$user_name, createdTime=$createdTime, first_name=$first_name, last_name=$last_name, isActive=$isActive, lastMessageTime=$lastMessageTime, trackList=$trackList)"
    }
}
