package telegram.bot;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Bot extends TelegramLongPollingBot{
    static Logger log;
    Map<Long, Boolean> subscribe;
    Map<Long, Boolean> start;
    OpenWeatherParsing openWeatherParsing;
    ReplyKeyboardMarkup replyKeyboardMarkup;
    List<KeyboardRow> keyboard;
    String city, subTime;

    public Bot(){
        subscribe = new HashMap<>();
        start = new HashMap<>();
        log = Logger.getLogger(Bot.class.getName());
        openWeatherParsing = new OpenWeatherParsing();
        city = "Moscow";
        subTime = "09:00:00";
    }

    public void setSubTime(String subTime) {
        this.subTime = subTime;
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
            if ((getStr.equals("/start") | getStr.equals("start") | getStr.toLowerCase().equals("старт"))
                    & !start.get(chatId)) {
                sendMsg(String.valueOf(chatId), "Добро пожаловать в самый лучший погодный чат-бот в мире." +
                        "\nВведите название города для вывода текущей погоды и прогноза на сутки");
                start.put(chatId, true);
                subscribe.put(chatId, false);
            }
            else if ((getStr.equals("/subscribe") | getStr.equals("subscribe")
                    | getStr.toLowerCase().equals("подписаться на рассылку")) & !subscribe.get(chatId)) {
                sendMsg(String.valueOf(chatId), "Вы подписались на ежедневную рассылку прогноза погоды за сутки");
                sendMsg(String.valueOf(chatId), "Рассылка проходит в " + subTime);
                subscribe.put(chatId, true);
            }
            else if ((getStr.equals("/subscribe") || getStr.equals("subscribe")
                    || getStr.toLowerCase().equals("подписаться на рассылку")) & subscribe.get(chatId)) {
                sendMsg(String.valueOf(chatId), "Вы уже подписались на ежедневную рассылку");
            }
            else if ((getStr.equals("/unsubscribe") | getStr.equals("unsubscribe")
                    | getStr.toLowerCase().equals("отписаться от рассылки")) & subscribe.get(chatId)) {
                sendMsg(String.valueOf(chatId), "Вы отписались от ежедневной рассылки прогноза погоды за сутки");
                subscribe.put(chatId, false);
            }
            else if ((getStr.equals("/unsubscribe") || getStr.equals("unsubscribe")
                    || getStr.toLowerCase().equals("отписаться от рассылки")) & !subscribe.get(chatId)) {
                sendMsg(String.valueOf(chatId), "Вы уже отписались от ежедневной рассылки");
            }
            else if (start.get(chatId) & !getStr.equals("/start") & !getStr.equals("/subscribe") &
                    !getStr.equals("/unsubscribe") & !getStr.equals("Подписаться на рассылку") &
                    !getStr.equals("Отписаться от рассылки")) {
                try {
                    getWeather(String.valueOf(chatId), getStr);
                    this.city = getStr;
                } catch (Exception e) {
                    sendMsg(String.valueOf(chatId), "Такого города не существует.\nПовторите попытку");
                }
            }
        }
        System.gc();

    }

    public void getWeather(String chatId, String city) {
        sendMsg(chatId, openWeatherParsing.getCurWeatherByCity(city));
        sendMsg(chatId, "Погода на следующий день:");
        sendMsg(chatId, openWeatherParsing.getTomWeatherByCity(city));
    }

    public synchronized void setConstantButtons(SendMessage sendMessage) {
        replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        keyboard = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.add(new KeyboardButton("Подписаться на рассылку"));
        KeyboardRow keyboardSecondRow = new KeyboardRow();
        keyboardSecondRow.add(new KeyboardButton("Отписаться от рассылки"));
        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);
        replyKeyboardMarkup.setKeyboard(keyboard);
    }

    public synchronized void sendMsg(String chatId, String msg) {
        SendMessage sMsg = new SendMessage();
        sMsg.setChatId(chatId);
        sMsg.setText(msg);
        try {
            setConstantButtons(sMsg);
            execute(sMsg);
        } catch (TelegramApiException e) {
            log.log(Level.SEVERE, "Exception: ", e.toString());
        }
        System.gc();
    }

    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi botapi = new TelegramBotsApi();
        Bot bot = new Bot();
//        bot.setSubTime("17:15:00");
        try {
            botapi.registerBot(bot);
            SubscribeThread subThread = new SubscribeThread(bot);
            subThread.start();
        } catch (TelegramApiException e) {
            log.log(Level.SEVERE, "Exception: ", e.toString());
        }
    }
}
