package telegram.bot.Parsing.JSONProcessing;

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

public class JSONParsingCurrentWeather extends JSONParsing {

    public JSONParsingCurrentWeather() { log = LogManager.getLogger(JSONParsingWeekWeather.class); }

    @Override
    public void readJSON(HttpURLConnection urlConnection) {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonReader jsonReader = gson.newJsonReader(new InputStreamReader(urlConnection.getInputStream(), StandardCharsets.UTF_8));
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String name = jsonReader.nextName();
                switch (name) {
                    case "name" -> city = jsonReader.nextString();
                    case "id" -> id = jsonReader.nextInt();
                    case "coord", "main", "clouds", "sys", "wind" -> readJSONSubParams(jsonReader);
                    case "weather" -> readJSONWeather(jsonReader);
                    default -> jsonReader.skipValue();
                }
            }
            jsonReader.endObject();
        } catch (IOException e) { log.error("Error processing JSON file", e); }
    }

    @Override
    public void readJSONSubParams(JsonReader jsonReader) {
        try {
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String name = jsonReader.nextName();
                switch (name) {
                    case "lon" -> lon = jsonReader.nextDouble();
                    case "lat" -> lat = jsonReader.nextDouble();
                    case "temp" -> temp = jsonReader.nextDouble() - 273.15;
                    case "temp_min" -> min_temp = jsonReader.nextDouble() - 273.15;
                    case "temp_max" -> max_temp = jsonReader.nextDouble() - 273.15;
                    case "pressure" -> press = jsonReader.nextInt();
                    case "humidity" -> humid = jsonReader.nextInt();
                    case "all" -> clouds = jsonReader.nextInt();
                    case "country" -> country = jsonReader.nextString();
                    case "speed" -> speed = jsonReader.nextDouble();
                    case "deg" -> deg = jsonReader.nextInt();
                    default -> jsonReader.skipValue();
                }
            }
            jsonReader.endObject();
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
        } catch (IOException e) { log.error("Error processing JSON file", e); }
    }

    @Override
    public String toString() {
        String s = "Город: " +
                city + "(" + id + ")\n" +
                "Координаты: " +
                "Широта: " + lat + "; " +
                "Долгота: " + lon + "\n" +
                "Страна: " + country + "\n" +
                "Погода: ";
        for (String weath: weather)
            s = s + weath + ",";
        s = s.substring(0, s.length() - 1);
        s = s  + "\n" +
                "Температура: " + String.format("%.2f", temp) + " °C\n" +
                "Минимальная температура: " + String.format("%.2f", min_temp) + " °C\n" +
                "Максимальная температура: " + String.format("%.2f", max_temp) + " °C\n" +
                "Влажность: " + humid + "%\n" +
                "Давление: " + press + " hPa\n" +
                "Ветер: " + speed + " м/с, " + deg + " градусов" + "\n" +
                "Облачность: " + clouds + "%\n" +
                "Текущее время: " + new Date().toString();
        return s;
    }
}
