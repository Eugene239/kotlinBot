package com.epavlov.entity

import java.util.*


class Track {

    var id: String? = null
    var status: String? = null
    var parserCode: Int = 0
    var text: String? = null
    var time: String? = null
    var users = HashMap<String, Long>()
    var last_modify :String?=null
    var notFound:Int=0


    companion object {
        val PATH = Track::class.java.simpleName
    }

    override fun toString(): String {
        return "Track(id=$id, status=$status, parserCode=$parserCode, text=$text, time=$time, users=$users, last_modify=$last_modify, notFound=$notFound)"
    }


}