package telegram.bot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import telegram.bot.Parsing.OpenWeatherParsing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bot extends TelegramLongPollingBot {
    private static final Logger log = LogManager.getLogger(Bot.class);
    Map<Long, Boolean> subscribe;
    Map<Long, Boolean> start;
    Map<Long, String> city;
    Map<Long, String> users;
    OpenWeatherParsing openWeatherParsing;
    ReplyKeyboardMarkup replyKeyboardMarkup;
    List<KeyboardRow> keyboard;
    String subTime, botUsername, botToken;
    int subTimeH, subTimeM, subTimeS;

    public Bot(){
        super();
        botUsername = "Bakanchik_Weather_bot";
        botToken = "1449620104:AAEf-XIegq8h6P0JqzqnyzuutZuAAlav3ko";
        subscribe = new HashMap<>();
        users = new HashMap<>();
        start = new HashMap<>();
        city = new HashMap<>();
        openWeatherParsing = new OpenWeatherParsing();
        subTime = "09:00:00";
        subTimeH = 9;
        subTimeM = 0;
        subTimeS = 0;
    }

    public void setBotToken(String botToken) { this.botToken = botToken; }

    public void setBotUsername(String botUsername) { this.botUsername = botUsername; }

    public void setSubTime(String subTime) {
        String[] subT = subTime.split(":");
        this.subTime = subTime;
        this.subTimeH = Integer.parseInt(subT[0]);
        this.subTimeM = Integer.parseInt(subT[1]);
        this.subTimeS = Integer.parseInt(subT[2]);
        log.info("A new mailing time has been set");
    }

    public void getWeatherByString(String username, String chatId, String city) {
        sendMsg(username, chatId, openWeatherParsing.getCurWeatherByCity(city));
        log.info("Current weather for the " + city + " was sent to the " + username);
        sendMsg(username, chatId, "Погода на сутки:");
        sendMsg(username, chatId, openWeatherParsing.getTomWeatherByCity(city));
        log.info("Weather forecast for the day for the " + city + " was sent to the " + username);
    }

    public String getSubTime() { return subTime; }

    @Override
    public String getBotUsername() { return botUsername; }

    @Override
    public String getBotToken() { return botToken; }

    @Override
    public void onUpdateReceived(Update upd) {
        if (upd.hasMessage() && upd.getMessage().hasText()) {
            Message inMsg = upd.getMessage();
            Long chatId = inMsg.getChatId();
            String getStr = inMsg.getText();
            users.putIfAbsent(chatId, inMsg.getFrom().getUserName());
            start.putIfAbsent(chatId, false);
            if ((getStr.equals("/start") | getStr.equals("start") | getStr.equalsIgnoreCase("старт"))
                    & !start.get(chatId)) {
                sendMsg(users.get(chatId), String.valueOf(chatId), "Добро пожаловать в самый лучший погодный чат-бот в мире." +
                        "\nВведите название города для вывода текущей погоды и прогноза на сутки. " +
                        "По умолчанию информация о погоде выводится по городу Москва");
                start.put(chatId, true);
                city.put(chatId, "Москва");
                subscribe.put(chatId, false);
                log.info("{} has connected to the bot", users.get(chatId));
            }
            else if ((getStr.equalsIgnoreCase("текущая погода") | getStr.equalsIgnoreCase("погода"))
                    && start.get(chatId)) {
                sendMsg(users.get(chatId), String.valueOf(chatId), openWeatherParsing.getCurWeatherByCity(city.get(chatId)));
                log.info("Current weather for the " + city.get(chatId) + " was sent to the " + users.get(chatId));
            }
            else if (getStr.equalsIgnoreCase("погода на сутки") && start.get(chatId)) {
                sendMsg(users.get(chatId), String.valueOf(chatId), openWeatherParsing.getTomWeatherByCity(city.get(chatId)));
                log.info("Weather forecast for the day for the " + city.get(chatId) + " was sent to the " + users.get(chatId));
            }
            else if (getStr.equalsIgnoreCase("погода на неделю") && start.get(chatId)) {
                sendMsg(users.get(chatId), String.valueOf(chatId), openWeatherParsing.getWeekWeatherByCity(city.get(chatId)));
                log.info("Weather forecast for the week for the " + city.get(chatId) + " was sent to the " + users.get(chatId));
            }
            else if ((getStr.equals("/subscribe") | getStr.equals("subscribe")
                    | getStr.equalsIgnoreCase("подписаться на рассылку")) & !subscribe.get(chatId)) {
                sendMsg(users.get(chatId), String.valueOf(chatId),
                        "Вы подписались на ежедневную рассылку прогноза погоды на сутки по городу: " + city.get(chatId));
                sendMsg(users.get(chatId), String.valueOf(chatId),
                        "Для изменения города, по которому будет производится рассылка, просто напишите новый город в чат");
                sendMsg(users.get(chatId), String.valueOf(chatId), "Рассылка проходит в " + subTime);
                subscribe.put(chatId, true);
                log.info("Subscription issued to the " + users.get(chatId));
            }
            else if ((getStr.equals("/subscribe") || getStr.equals("subscribe")
                    || getStr.equalsIgnoreCase("подписаться на рассылку")) & subscribe.get(chatId)) {
                sendMsg(users.get(chatId), String.valueOf(chatId), "Вы уже подписались на ежедневную рассылку");
                log.info(users.get(chatId) + " has already subscribed");
            }
            else if ((getStr.equals("/unsubscribe") | getStr.equals("unsubscribe")
                    | getStr.equalsIgnoreCase("отписаться от рассылки")) & subscribe.get(chatId)) {
                sendMsg(users.get(chatId), String.valueOf(chatId), "Вы отписались от ежедневной рассылки прогноза погоды за сутки");
                subscribe.put(chatId, false);
                log.info(users.get(chatId) + " canceled the subscription");
            }
            else if ((getStr.equals("/unsubscribe") || getStr.equals("unsubscribe")
                    || getStr.equalsIgnoreCase("отписаться от рассылки")) & !subscribe.get(chatId)) {
                sendMsg(users.get(chatId), String.valueOf(chatId), "Вы уже отписались от ежедневной рассылки");
                log.info("Subscription has already been canceled for the " + users.get(chatId));
            }
            else if (start.get(chatId)) {
                if (getStr.length() <= 80) {
                    if (!openWeatherParsing.getCurWeatherByCity(getStr).equals("Такого города не существует.\nПовторите попытку")) {
                        log.info("{} changed their current location to {}", users.get(chatId), getStr);
                        getWeatherByString(users.get(chatId), String.valueOf(chatId), getStr);
                        city.put(chatId, getStr);
                    } else {
                        sendMsg(users.get(chatId), String.valueOf(chatId), "Такого города не существует\nПовторите попытку");
                        log.warn(users.get(chatId) + " is trying to get the weather for an unknown location '" + getStr + "'");
                    }
                } else {
                    sendMsg(users.get(chatId), String.valueOf(chatId), "Слишком длинное название города\nПовторите попытку");
                    log.warn(users.get(chatId) + " entered the city name too long: '" + getStr + "'");
                }
            }
        }
        else {
            log.error("Update doesn't have a message!");
            throw new IllegalStateException("Update doesn't have a message!");
        }
        System.gc();
    }

    private synchronized void setConstantButtons(SendMessage sendMessage) {
        replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        keyboard = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        KeyboardRow keyboardSecondRow = new KeyboardRow();
        KeyboardRow keyboardThirdRow = new KeyboardRow();
        KeyboardRow keyboardFourthRow = new KeyboardRow();
        keyboardFirstRow.add(new KeyboardButton("Текущая погода"));
        keyboardSecondRow.add(new KeyboardButton("Погода на сутки"));
        keyboardThirdRow.add(new KeyboardButton("Погода на неделю"));
        keyboardFourthRow.add(new KeyboardButton("Подписаться на рассылку"));
        keyboardFourthRow.add(new KeyboardButton("Отписаться от рассылки"));
        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);
        keyboard.add(keyboardThirdRow);
        keyboard.add(keyboardFourthRow);
        replyKeyboardMarkup.setKeyboard(keyboard);
    }

    public synchronized void sendMsg(String username, String chatId, String msg) {
        SendMessage sMsg = new SendMessage();
        sMsg.setChatId(chatId);
        sMsg.setText(msg);
        try {
            setConstantButtons(sMsg);
            execute(sMsg);
        } catch (TelegramApiException e) { log.error("Error while sending message to " + username + "!", e); }
        System.gc();
    }

    public static void main(String[] args) {
        try {
            log.info("Initializing API context...");
            ApiContextInitializer.init();
            TelegramBotsApi botApi = new TelegramBotsApi();
            log.info("Registering Bot...");
            Bot bot = new Bot();
            botApi.registerBot(bot);
            log.info("Bot is ready for work!");
            log.info("Starting the subscription mailing stream...");
            SubscribeThread subThread = new SubscribeThread(bot);
            subThread.start();
            log.info("The subscription mailing stream is work!");
        } catch (TelegramApiException e) { log.error("Error while initializing bot!", e); }
    }
}
