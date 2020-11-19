package telegram.bot;

import java.util.Date;
import java.util.Map;

public class SubscribeThread extends Thread {
    Bot bot;
    String time;
    Date datetime;

    public SubscribeThread(Bot bot) {
        this.bot = bot;
        datetime = new Date();
        time = datetime.toString().split(" ")[3];
    }

    @Override
    public void run() {
        while(true) {
            try {
                datetime = new Date();
                for (Map.Entry<Long, Boolean> ent: bot.subscribe.entrySet()) {
                    if (ent.getValue() & time.equals(bot.subTime)) bot.getWeather(String.valueOf(ent.getKey()), bot.city);
                }
                time = datetime.toString().split(" ")[3];
                sleep(1000);
            } catch (InterruptedException e) {
                interrupt();
            }
        }
    }
}
