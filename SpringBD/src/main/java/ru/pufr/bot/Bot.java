package ru.pufr.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;




@Component
public class Bot extends TelegramLongPollingBot {

    private static final Logger LOG = LoggerFactory.getLogger(Bot.class);

    private static final String START = "/start";
    private static final String HELP = "/help";


    public Bot(@Value("${bot.token}") String botToken) {
        super(botToken);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }
        
        var message = update.getMessage().getText();
        var chatId = update.getMessage().getChatId();

        switch (message) {
            case START -> {
                String userName = update.getMessage().getChat().getUserName();
                startCommand(chatId, userName);
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "myBot_test_bfr12r3_bot";
    }

    private void startCommand(Long chatId, String userName) {
        var text = """
                Добро пожаловать в бот, %s!
                
                /help - для получения справки
                """;
        var formattedText = String.format(text, userName);
        sendMessage(chatId, formattedText);
    }


    private void sendMessage(Long chatId, String text) {
        var chatIdStr = String.valueOf(chatId);
        var sendMessage = new SendMessage(chatIdStr, text);
        try {
            execute(sendMessage);
        }catch (TelegramApiException e) {
            LOG.error("Ошибка отправки сообщения", e);
        }

    }

}
