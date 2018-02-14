package com.epavlov.repository

import com.epavlov.entity.ErrorMessage

object ErrorMessageRepository {

    fun save(message: ErrorMessage){
        Repository.db.getReference("ERROR/${message.userId}").push().setValue(message)
    }
}