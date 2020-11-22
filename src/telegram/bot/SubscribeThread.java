package telegram.bot;

import java.util.*;

public class SubscribeThread {
    Bot bot;
    Calendar c;
    Timer timer;

    public SubscribeThread(Bot bot) {
        this.bot = bot;
        c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, this.bot.subTimeH);
        c.set(Calendar.MINUTE, this.bot.subTimeM);
        c.set(Calendar.SECOND, this.bot.subTimeS);
        timer = new Timer();
    }

    public void start() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                for (Map.Entry<Long, Boolean> ent: bot.subscribe.entrySet())
                    if (ent.getValue())
                        try {
                            bot.getWeather(String.valueOf(ent.getKey()), bot.city);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
            }
        }, c.getTime(), 86400000);
    }
}
