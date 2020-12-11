package telegram.bot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import telegram.bot.common.Bot;
import telegram.bot.common.SubscribeThread;

public class InitializeBot {
    private static final Logger log = LogManager.getLogger(InitializeBot.class);

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
