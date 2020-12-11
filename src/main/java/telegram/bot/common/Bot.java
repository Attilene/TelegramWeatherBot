package telegram.bot.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import telegram.bot.common.DBMS.models.User;
import telegram.bot.common.DBMS.services.UserService;
import telegram.bot.common.Parsing.OpenWeatherParsing;

import java.util.ArrayList;
import java.util.List;

public class Bot extends TelegramLongPollingBot {
    private static final Logger log = LogManager.getLogger(Bot.class);
    UserService userService;
    User user;
    OpenWeatherParsing openWeatherParsing;
    ReplyKeyboardMarkup replyKeyboardMarkup;
    List<KeyboardRow> keyboard;
    String subTime, botUsername, botToken;
    int subTimeH, subTimeM, subTimeS;

    public Bot(){
        super();
        botUsername = "Bakanchik_Weather_bot";
        botToken = "1449620104:AAEf-XIegq8h6P0JqzqnyzuutZuAAlav3ko";
        userService = new UserService();
        openWeatherParsing = new OpenWeatherParsing();
        subTime = "09:00:00";
        subTimeH = 9;
        subTimeM = 0;
        subTimeS = 0;
    }

    public void setBotToken(String botToken) { this.botToken = botToken; }

    public void setBotUsername(String botUsername) { this.botUsername = botUsername; }

    public void setSubTime(Long chatId, String subTime) {
        User user = userService.findUser(chatId);
        String[] subT = subTime.split(":");
        this.subTime = subTime;
        this.subTimeH = Integer.parseInt(subT[0]);
        this.subTimeM = Integer.parseInt(subT[1]);
        this.subTimeS = Integer.parseInt(subT[2]);
        user.setSubTime(this.subTime);
        userService.updateUser(user);
        log.info("A new mailing time has been set");
    }

    public void getWeatherByString(Long chatId, String user_name, String city) {
        sendMsg(chatId, user_name, openWeatherParsing.getCurWeatherByCity(city));
        log.info("| " + user_name + " | Current weather for the " + city + " was sent");
        sendMsg(chatId, user_name, "Погода на сутки:");
        sendMsg(chatId, user_name, openWeatherParsing.getTomWeatherByCity(city));
        log.info("| " + user_name + " | Weather forecast for the day for the " + city + " was sent");
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
            user = userService.findUser(chatId);
            if (user == null) {
                user = new User();
                user.setId(chatId);
                user.setFirst_name(inMsg.getFrom().getFirstName());
                user.setLast_name(inMsg.getFrom().getLastName());
                user.setUser_name(inMsg.getFrom().getUserName());
                user.setCity("Москва");
                user.setStarting(false);
                user.setSubscribe(false);
                user.setSubTime(subTime);
                userService.saveUser(user);
            }
            if ((getStr.equals("/start") | getStr.equals("start") | getStr.equalsIgnoreCase("старт"))
                    & !user.getStarting()) {
                sendMsg(chatId, user.getUser_name(), "Добро пожаловать в самый лучший погодный чат-бот в мире." +
                        "\nВведите название города для вывода текущей погоды и прогноза на сутки. " +
                        "По умолчанию информация о погоде выводится по городу Москва");
                user.setStarting(true);
                userService.updateUser(user);
                log.info("| {} | connected to the bot", user.getUser_name());
            }
            else if ((getStr.equalsIgnoreCase("текущая погода") | getStr.equalsIgnoreCase("погода"))
                    && user.getStarting()) {
                sendMsg(chatId, user.getUser_name(), openWeatherParsing.getCurWeatherByCity(user.getCity()));
                log.info("| " + user.getUser_name() + " | Current weather for the " + user.getCity() + " was sent");
            }
            else if (getStr.equalsIgnoreCase("погода на сутки") && user.getStarting()) {
                sendMsg(chatId, user.getUser_name(), openWeatherParsing.getTomWeatherByCity(user.getCity()));
                log.info("| " + user.getUser_name() + " | Weather forecast for the day for the " + user.getCity() + " was sent");
            }
            else if (getStr.equalsIgnoreCase("погода на неделю") && user.getStarting()) {
                sendMsg(chatId, user.getUser_name(), openWeatherParsing.getWeekWeatherByCity(user.getCity()));
                log.info("| " + user.getUser_name() + " | Weather forecast for the week for the " + user.getCity() + " was sent");
            }
            else if ((getStr.equals("/subscribe") | getStr.equals("subscribe")
                    | getStr.equalsIgnoreCase("подписаться на рассылку")) & !user.getSubscribe() & user.getStarting()) {
                sendMsg(chatId, user.getUser_name(), "Вы подписались на ежедневную рассылку прогноза погоды на сутки по городу: " + user.getCity());
                sendMsg(chatId, user.getUser_name(), "Для изменения города, по которому будет производится рассылка, просто напишите новый город в чат");
                sendMsg(chatId, user.getUser_name(), "Рассылка проходит в " + subTime);
                user.setSubscribe(true);
                userService.updateUser(user);
                log.info("| " + user.getUser_name() + " | Subscription issued");
            }
            else if ((getStr.equals("/subscribe") || getStr.equals("subscribe")
                    || getStr.equalsIgnoreCase("подписаться на рассылку")) & user.getSubscribe() & user.getStarting()) {
                sendMsg(chatId, user.getUser_name(), "Вы уже подписались на ежедневную рассылку");
                log.info("| " + user.getUser_name() + " | Already subscribed");
            }
            else if ((getStr.equals("/unsubscribe") | getStr.equals("unsubscribe")
                    | getStr.equalsIgnoreCase("отписаться от рассылки")) & user.getSubscribe() & user.getStarting()) {
                sendMsg(chatId, user.getUser_name(), "Вы отписались от ежедневной рассылки прогноза погоды за сутки");
                user.setSubscribe(false);
                userService.updateUser(user);
                log.info("| " + user.getUser_name() + " | Canceled the subscription");
            }
            else if ((getStr.equals("/unsubscribe") || getStr.equals("unsubscribe")
                    || getStr.equalsIgnoreCase("отписаться от рассылки")) & !user.getSubscribe() & user.getStarting()) {
                sendMsg(chatId, user.getUser_name(), "Вы уже отписались от ежедневной рассылки");
                log.info("| " + user.getUser_name() + " | Already canceled subscription");
            }
            else if (user.getStarting()) {
                if (getStr.length() <= 80) {
                    if (!openWeatherParsing.getCurWeatherByCity(getStr).equals("Такого города не существует.\nПовторите попытку")) {
                        log.info("| {} | Changed their current location to {}", user.getUser_name(), getStr);
                        getWeatherByString(chatId, user.getUser_name(), getStr);
                        user.setCity(getStr);
                        userService.updateUser(user);
                    } else {
                        sendMsg(chatId, user.getUser_name(), "Такого города не существует\nПовторите попытку");
                        log.warn("| " + user.getUser_name() + " | Try to get the weather for an unknown location '" + getStr + "'");
                    }
                } else {
                    sendMsg(chatId, user.getUser_name(), "Слишком длинное название города\nПовторите попытку");
                    log.warn("| " + user.getUser_name() + " | Entered the city name too long: '" + getStr + "'");
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

    public synchronized void sendMsg(Long chatId, String user_name, String msg) {
        SendMessage sMsg = new SendMessage();
        sMsg.setChatId(chatId);
        sMsg.setText(msg);
        try {
            setConstantButtons(sMsg);
            execute(sMsg);
        } catch (TelegramApiException e) { log.error("| " + user_name + " | Error while sending message!", e); }
        System.gc();
    }
}
