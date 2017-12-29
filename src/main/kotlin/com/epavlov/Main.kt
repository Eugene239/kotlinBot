package com.epavlov

import com.epavlov.bot.BotImpl
import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.TelegramBotsApi
import org.telegram.telegrambots.exceptions.TelegramApiException


fun main(args: Array<String>) {
    ApiContextInitializer.init()
    val telegramBotsApi = TelegramBotsApi()
    try {
        telegramBotsApi.registerBot(BotImpl())
    } catch (e: TelegramApiException) {
        e.printStackTrace()
    }
}



