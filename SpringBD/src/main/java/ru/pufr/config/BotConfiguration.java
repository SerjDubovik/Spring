package ru.pufr.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.pufr.bot.Bot;

@Configuration
@EnableCaching
@EnableScheduling
public class BotConfiguration {

    @Bean
    public TelegramBotsApi telegramBotsApi(Bot bot) throws TelegramApiException {

        var api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(bot);
        return api;
    }
}
