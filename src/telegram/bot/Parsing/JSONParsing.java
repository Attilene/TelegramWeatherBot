package telegram.bot.Parsing;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.FileReader;
import java.io.IOException;

public class JSONParsing {
    Double lon, lat;
    String namecity;
    JsonReader jsonReader;

    public JSONParsing() {}

    public void setLat(Double lat) { this.lat = lat; }

    public void setLon(Double lon) { this.lon = lon; }

    public void setNamecity(String name) { this.namecity = name; }

    public Double getLat() { return lat; }

    public Double getLon() { return lon; }

    public String getNamecity() { return namecity; }

    public void readJSON() throws IOException {
//        JsonReader jsonReader = new JsonReader(new InputStreamReader(urlConnection.getInputStream(), StandardCharsets.UTF_8));
        FileReader fileReader = new FileReader("C:/Users/Артем/Desktop/Parse.json");
        jsonReader = new JsonReader(fileReader);
        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            switch (name) {
                case "name" -> namecity = jsonReader.nextString();
                case "coord" -> readJSONCoord(jsonReader);
                default -> jsonReader.skipValue();
            }
        }
        jsonReader.endObject();
    }

    public void readJSONCoord(JsonReader reader) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case "lon" -> lon = jsonReader.nextDouble();
                case "lat" -> lat = jsonReader.nextDouble();
                default -> reader.skipValue();
            }
        }
        reader.endObject();
    }
}
