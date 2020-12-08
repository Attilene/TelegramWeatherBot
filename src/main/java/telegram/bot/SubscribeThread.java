package telegram.bot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class SubscribeThread {
    private final Logger log = LogManager.getLogger(SubscribeThread.class);
    Bot bot;
    Calendar c;
    Timer timer;
    long period;

    public SubscribeThread(Bot bot) {
        this.bot = bot;
        c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, this.bot.subTimeH);
        c.set(Calendar.MINUTE, this.bot.subTimeM);
        c.set(Calendar.SECOND, this.bot.subTimeS);
        timer = new Timer();
        period = 86400000;
    }

    public long getPeriod() { return period; }

    public void setPeriod(long period) { this.period = period; }

    public void start() {
        log.info("Subscription mailing stream started!");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                for (Map.Entry<Long, Boolean> ent: bot.subscribe.entrySet())
                    if (ent.getValue()) {
                        String user = bot.users.get(ent.getKey());
                        bot.getWeatherByString(user, String.valueOf(ent.getKey()), bot.city.get(ent.getKey()));
                        log.info("Subscription weather forecast sent to the " + user);
                    }
            }
        }, c.getTime(), period);
    }
}
