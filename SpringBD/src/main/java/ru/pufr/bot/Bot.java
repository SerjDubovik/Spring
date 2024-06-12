package ru.pufr.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import ru.pufr.models.BotTable;
import ru.pufr.repo.BotRepository;

import java.util.Optional;


@Component
public class Bot extends TelegramLongPollingBot {

    private static final Logger LOG = LoggerFactory.getLogger(Bot.class);

    @Autowired
    private BotRepository botRepository;

    private static final String START = "/start";
    private static final String HELP = "/help";
    private static final String LIST = "/list";


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

        var nik = update.getMessage().getChat().getUserName();  // ник
        var name = update.getMessage().getChat().getFirstName(); // имя
        var surname = update.getMessage().getChat().getLastName();  // фамилия

        // тут индификация пользователя в системе, поиск в таблице, если уже существует, то проверить на каком этапе
        // взаимодействия остановились. Если новый пользователь, то создать профиль и добавить в базу данных

        //BotTable mybotTable = botRepository.findByName(nik).<RuntimeException>orElseThrow(() -> {throw new AssertionError();});
        //if(!userRepository.existsById(id)){return "redirect:/users";}


        Optional<BotTable> botRepo = botRepository.findByName(nik);             // берём ник приславшего сообщение.

        Integer count;                                                          // тут переменная для определения позиции в боте
        if(botRepo.isPresent()) {
            count = Integer.parseInt(botRepo.get().getRhythm().trim());         // если пользователь уже обращался, тут берём номер его запроса, чтобы узать на чём он остановился
            //BotTable mybotTable = botRepository.findByName(nik).<RuntimeException>orElseThrow(() -> {throw new AssertionError();});


            switch (count) {
                case 0 -> {
                    String str = "Ты тут первый раз. Стартовая локация";
                    sendMessage(chatId, str);
                    botRepo.get().setRhythm(String.valueOf(1));
                    botRepository.save(botRepo.get());
                }
                case 1 -> {

                    String str = "Локация 1";
                    sendMessage(chatId, str);
                    botRepo.get().setRhythm(String.valueOf(2));
                    botRepository.save(botRepo.get());
                }
                case 2 -> {

                    String str = "Локация 2";
                    sendMessage(chatId, str);
                    botRepo.get().setRhythm(String.valueOf(3));
                    botRepository.save(botRepo.get());
                }
                case 3 -> {

                    String str = "Локация 3";
                    sendMessage(chatId, str);
                    botRepo.get().setRhythm(String.valueOf(0));
                    botRepository.save(botRepo.get());
                }
                default -> unknownCommand(chatId);
            }

        }else{

            String str = "тебя нет в списке, ща добавлю";
            sendMessage(chatId, str);

            BotTable botTable = new BotTable(update.getMessage().getChat().getUserName(),"0","0","0","0","0");
            botRepository.save(botTable);

            str = "Добавил, пиши что хотел...";
            sendMessage(chatId, str);
        }

    }


    /*
            switch (message) {
            case START -> {
                String userName = update.getMessage().getChat().getUserName();
                startCommand(chatId, userName);
            }
            case LIST -> {
                Iterable<BotTable> botTables = botRepository.findAll();
                //BotTable botTable = botRepository.findByName(name).<RuntimeException>orElseThrow(() -> {throw new AssertionError();});
                String str = "тут кака-то шлякшля))))) ";
                sendMessage(chatId, str);
            }
            case HELP -> helpCommand(chatId);
            default -> unknownCommand(chatId);
        }
    */


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

    private void helpCommand(Long chatId) {
        var text = """
                Справочная информация по боту                
                """;
        sendMessage(chatId, text);
    }

    private void unknownCommand(Long chatId) {
        var text = "Не удалось распознать команду!";
        sendMessage(chatId, text);
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
