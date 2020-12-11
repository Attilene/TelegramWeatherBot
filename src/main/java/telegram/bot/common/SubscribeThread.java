package telegram.bot.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import telegram.bot.common.DBMS.models.User;
import telegram.bot.common.DBMS.services.UserService;

import java.util.*;

public class SubscribeThread {
    private final Logger log = LogManager.getLogger(SubscribeThread.class);
    List<User> users;
    UserService userService;
    Bot bot;
    Calendar c;
    Timer timer;
    long period;

    public SubscribeThread(Bot bot) {
        this.bot = bot;
        userService = new UserService();
        c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, this.bot.subTimeH);
        c.set(Calendar.MINUTE, this.bot.subTimeM);
        c.set(Calendar.SECOND, this.bot.subTimeS);
        timer = new Timer();
        period = 86400000;
    }

    public long getPeriod() { return period; }

    public Bot getBot() { return bot; }

    public void setPeriod(long period) { this.period = period; }

    public void setBot(Bot bot) { this.bot = bot; }

    public void start() {
        log.info("Subscription mailing stream started!");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                users = userService.findAllUsers();
                if (users != null)
                    for (User user: users)
                        if (user.getSubscribe()) {
                            bot.getWeatherByString(user.getId(), user.getUser_name(), user.getCity());
                            System.out.println(c.getTime());
                            log.info("| " + user.getUser_name() + " | Subscription weather forecast sent");
                        }
            }
        }, c.getTime(), period);
    }
}
