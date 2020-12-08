package telegram.bot;

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
import java.util.logging.Level;
import java.util.logging.Logger;

public class Bot extends TelegramLongPollingBot {
    private static final Map<String, String> getenv = System.getenv();
    static Logger log;
    Map<Long, Boolean> subscribe;
    Map<Long, Boolean> start;
    Map<Long, String> city;
    OpenWeatherParsing openWeatherParsing;
    ReplyKeyboardMarkup replyKeyboardMarkup;
    List<KeyboardRow> keyboard;
    String subTime, botUsername, botToken;
    int subTimeH, subTimeM, subTimeS;

    public Bot(String botUsername, String botToken){
        super();
        this.botUsername = botUsername;
        this.botToken = botToken;
        subscribe = new HashMap<>();
        start = new HashMap<>();
        city = new HashMap<>();
        log = Logger.getLogger(Bot.class.getName());
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
    }

    public void getWeatherByString(String chatId, String city) throws Exception {
        sendMsg(chatId, openWeatherParsing.getCurWeatherByCity(city));
        sendMsg(chatId, "Погода на сутки:");
        sendMsg(chatId, openWeatherParsing.getTomWeatherByCity(city));
    }

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
            start.putIfAbsent(chatId, false);
            if ((getStr.equals("/start") | getStr.equals("start") | getStr.toLowerCase().equals("старт"))
                    & !start.get(chatId)) {
                sendMsg(String.valueOf(chatId), "Добро пожаловать в самый лучший погодный чат-бот в мире." +
                        "\nВведите название города для вывода текущей погоды и прогноза на сутки");
                start.put(chatId, true);
                city.put(chatId, "Москва");
                subscribe.put(chatId, false);
            }
            else if (getStr.toLowerCase().equals("текущая погода") | getStr.toLowerCase().equals("погода")) {
                try {
                    sendMsg(String.valueOf(chatId), openWeatherParsing.getCurWeatherByCity(city.get(chatId)));
                } catch (Exception e) { log.log(Level.SEVERE, "Exception: ", e.toString()); }
            }
            else if (getStr.toLowerCase().equals("погода на сутки")) {
                try {
                    sendMsg(String.valueOf(chatId), openWeatherParsing.getTomWeatherByCity(city.get(chatId)));
                } catch (Exception e) { log.log(Level.SEVERE, "Exception: ", e.toString()); }
            }
            else if (getStr.toLowerCase().equals("погода на неделю")) {
                try {
                    sendMsg(String.valueOf(chatId), openWeatherParsing.getWeekWeatherByCity(city.get(chatId)));
                } catch (Exception e) { log.log(Level.SEVERE, "Exception: ", e.toString()); }
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
            else if (start.get(chatId)){
                try {
                    if (!openWeatherParsing.getCurWeatherByCity(getStr).equals("Такого города не существует.\nПовторите попытку")) {
                        getWeatherByString(String.valueOf(chatId), getStr);
                        city.put(chatId, getStr);
                    }
                    else sendMsg(String.valueOf(chatId), "Такого города не существует.\nПовторите попытку");
                } catch (Exception e) { log.log(Level.SEVERE, "Exception: ", e.toString()); }
            }
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
        TelegramBotsApi botApi = new TelegramBotsApi();
        Bot bot = new Bot(getenv.get("BOT_USERNAME"), getenv.get("BOT_TOKEN"));
        try {
            botApi.registerBot(bot);
            SubscribeThread subThread = new SubscribeThread(bot);
            subThread.start();
        } catch (TelegramApiException e) {
            log.log(Level.SEVERE, "Exception: ", e.toString());
        }
    }
}
