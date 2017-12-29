package com.epavlov.repository

import com.epavlov.PropReader
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseCredentials
import com.google.firebase.database.FirebaseDatabase
import java.io.IOException

object Repository {
    init {
        val serviceAccount = javaClass.classLoader.getResourceAsStream("google-services.json")
        val databaseUrl: String = PropReader.getProperty("database").toString()
        try {
            val options = FirebaseOptions.Builder()
                    .setCredential(FirebaseCredentials.fromCertificate(serviceAccount))
                    .setDatabaseUrl(databaseUrl)
                    .build()
            FirebaseApp.initializeApp(options)
        } catch (e: IOException) {
            e.printStackTrace()
            Runtime.getRuntime().exit(-1)
        }
    }

    val db: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }
}