package telegram.bot.common.Parsing.JSONProcessing;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JSONParsingTomorrowWeather extends JSONParsing {
    List<Long> dtList;
    List<Double> tempList, speedList;
    List<Integer> pressList, humidList, cloudsList, degList;
    List<List<String>> weatherList;

    public JSONParsingTomorrowWeather() {
        dtList = new ArrayList<>();
        tempList = new ArrayList<>();
        speedList = new ArrayList<>();
        pressList = new ArrayList<>();
        humidList = new ArrayList<>();
        cloudsList = new ArrayList<>();
        degList = new ArrayList<>();
        weatherList = new ArrayList<>();
        log = LogManager.getLogger(JSONParsingWeekWeather.class);
    }

    @Override
    public void readJSON(HttpURLConnection urlConnection) {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonReader jsonReader = gson.newJsonReader(new InputStreamReader(urlConnection.getInputStream(), StandardCharsets.UTF_8));
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String name = jsonReader.nextName();
                if (name.equals("hourly")) readJSONSubParams(jsonReader);
                else jsonReader.skipValue();
            }
            jsonReader.endObject();
        } catch (IOException e) { log.error("Error processing JSON file", e); }
    }

    @Override
    public void readJSONSubParams(JsonReader jsonReader) {
        try {
            jsonReader.beginArray();
            while (jsonReader.hasNext()) {
                jsonReader.beginObject();
                while (jsonReader.hasNext()) {
                    String name = jsonReader.nextName();
                    switch (name) {
                        case "dt" -> dtList.add(jsonReader.nextLong());
                        case "temp" -> tempList.add(jsonReader.nextDouble() - 273.15);
                        case "pressure" -> pressList.add(jsonReader.nextInt());
                        case "humidity" -> humidList.add(jsonReader.nextInt());
                        case "clouds" -> cloudsList.add(jsonReader.nextInt());
                        case "wind_speed" -> speedList.add(jsonReader.nextDouble());
                        case "wind_deg" -> degList.add(jsonReader.nextInt());
                        case "weather" -> readJSONWeather(jsonReader);
                        default -> jsonReader.skipValue();
                    }
                }
                jsonReader.endObject();
            }
            jsonReader.endArray();
        } catch (IOException e) { log.error("Error processing JSON file", e); }
    }

    @Override
    public void readJSONWeather(JsonReader jsonReader) {
        try {
            weather = new ArrayList<>();
            jsonReader.beginArray();
            while (jsonReader.hasNext()) {
                jsonReader.beginObject();
                while (jsonReader.hasNext()) {
                    String name = jsonReader.nextName();
                    if (name.equals("description")) weather.add(jsonReader.nextString());
                    else jsonReader.skipValue();
                }
                jsonReader.endObject();
            }
            jsonReader.endArray();
            weatherList.add(weather);
        } catch (IOException e) { log.error("Error processing JSON file", e); }
    }

    @Override
    public String toString() {
        String s = "Город: " +
                city + "(" + id + ")\n" +
                "Координаты: " +
                "Широта: " + lat + "; " +
                "Долгота: " + lon + "\n" +
                "Страна: " + country + "\n\n";
        for (int i = 7; i <= 30; i += 6) {
            s = s + "Время: " + new Date(dtList.get(i) * 1000L).toString() + "\n" +
                    "Погода: ";
            for (String weath: weatherList.get(i))
                s = s + weath + ",";
            s = s.substring(0, s.length() - 1);
            s = s  + "\n" +
                    "Температура: " + String.format("%.2f", tempList.get(i)) + " °C\n" +
                    "Давление: " + pressList.get(i) + " hPa\n" +
                    "Влажность: " + humidList.get(i) + "%\n" +
                    "Облачность: " + cloudsList.get(i) + "%\n" +
                    "Ветер: " + speedList.get(i) + " м/с, " + degList.get(i) + " градусов" + "\n\n";
        }
        s = s.substring(0, s.length() - 2);
        return s;
    }
}
