package com.epavlov

import com.epavlov.bot.BotImpl
import com.epavlov.bot.BotImpl.safe
import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.TelegramBotsApi


fun main(args: Array<String>) {
    ApiContextInitializer.init()
    safe {
        val telegramBotsApi = TelegramBotsApi()
        telegramBotsApi.registerBot(BotImpl)
    }
}

