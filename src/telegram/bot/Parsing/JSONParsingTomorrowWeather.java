package telegram.bot.Parsing;

import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.net.HttpURLConnection;

public class JSONParsingTomorrowWeather extends JSONParsing {

    public JSONParsingTomorrowWeather() {}

    @Override
    public void readJSON(HttpURLConnection urlConnection) throws IOException {

    }

    @Override
    public void readJSONSubParams(JsonReader jsonReader) throws IOException {

    }

    @Override
    public void readJSONWeather(JsonReader jsonReader) throws IOException {

    }

    @Override
    public String toString() {
        return null;
    }
}
