package com.epavlov.entity

import java.time.LocalDateTime

class ErrorMessage{
    var text=""
    var time=""
    var userId:Long=0

    constructor()

    constructor(userId:Long,text:String){
        this.text = text
        this.userId=userId
        this.time= LocalDateTime.now().toString()
    }
}