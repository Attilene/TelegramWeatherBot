package telegram.bot;

import com.github.prominence.openweathermap.api.enums.Accuracy;
import com.github.prominence.openweathermap.api.enums.Language;
import com.github.prominence.openweathermap.api.enums.UnitSystem;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Bot extends TelegramLongPollingBot{
    static Logger log;
    Map<Long, Boolean> subscribe;
    Map<Long, Boolean> start;

    public Bot(){
        subscribe = new HashMap<>();
        start = new HashMap<>();
        log = Logger.getLogger(Bot.class.getName());
    }

    @Override
    public String getBotUsername() { return "Bakanchik_Weather_bot"; }

    @Override
    public String getBotToken() { return "1449620104:AAEf-XIegq8h6P0JqzqnyzuutZuAAlav3ko"; }

    @Override
    public void onUpdateReceived(Update upd) {
        if (upd.hasMessage() && upd.getMessage().hasText()) {
            Message inMsg = upd.getMessage();
            Long chatId = inMsg.getChatId();
            String getStr = inMsg.getText();
            start.putIfAbsent(chatId, false);
            if (getStr.equals("/start") & !start.get(chatId)) {
                sendMsg(String.valueOf(inMsg.getChatId()), "Добро пожаловать в самый лучший погодный чат-бот в мире.\nВведите название города для вывода текущей погоды и прогноза на сутки");
                start.put(chatId, true);
                subscribe.put(chatId, false);
            }
            else if (getStr.equals("/subscribe") & !subscribe.get(chatId)) {
                sendMsg(String.valueOf(inMsg.getChatId()), "Вы подписались на ежедневную рассылку прогноза погоды за сутки");
                subscribe.put(chatId, true);
            }
            else if (getStr.equals("/subscribe") & subscribe.get(chatId)) {
                sendMsg(String.valueOf(inMsg.getChatId()), "Вы уже подписались на ежедневную рассылку");
            }
            else if (getStr.equals("/unsubscribe") & subscribe.get(chatId)) {
                sendMsg(String.valueOf(inMsg.getChatId()), "Вы отписались от ежедневной рассылки прогноза погоды за сутки");
                subscribe.put(chatId, false);
            }
            else if (getStr.equals("/unsubscribe") & !subscribe.get(chatId)) {
                sendMsg(String.valueOf(inMsg.getChatId()), "Вы уже отписались от ежедневной рассылки");
            }
        }
        System.gc();
    }

    public synchronized void sendMsg(String chatId, String msg) {
        SendMessage sMsg = new SendMessage();
        sMsg.setChatId(chatId);
        sMsg.setText(msg);
        try {
            execute(sMsg);
        } catch (TelegramApiException e) {
            log.log(Level.SEVERE, "Exception: ", e.toString());
        }
        System.gc();
    }

    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi botapi = new TelegramBotsApi();
        try {
            botapi.registerBot(new Bot());
        } catch (TelegramApiException e) {
            log.log(Level.SEVERE, "Exception: ", e.toString());
        }
        OpenWeatherParsing openWeatherParsing = new OpenWeatherParsing();
        System.out.println(openWeatherParsing.getCurWeatherByCity("London"));
    }
}
