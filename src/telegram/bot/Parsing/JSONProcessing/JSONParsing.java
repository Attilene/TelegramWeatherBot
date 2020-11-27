package telegram.bot.Parsing.JSONProcessing;

import com.google.gson.stream.JsonReader;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.logging.Logger;

public abstract class JSONParsing {
    Double lon, lat, speed, temp, max_temp, min_temp;
    Integer id, press, humid, clouds, deg;
    String city, country;
    List<String> weather;

    public JSONParsing() {}

    public void setLat(Double lat) { this.lat = lat; }

    public void setLon(Double lon) { this.lon = lon; }

    public void setCity(String name) { this.city = name; }

    public void setId(Integer id) { this.id = id; }

    public void setCountry(String country) { this.country = country; }

    public void setClouds(Integer clouds) { this.clouds = clouds; }

    public void setSpeed(Double speed) { this.speed = speed; }

    public void setDeg(Integer deg) { this.deg = deg; }

    public void setHumid(Integer humid) { this.humid = humid; }

    public void setTemp(Double temp) { this.temp = temp; }

    public void setMax_temp(Double max_temp) { this.max_temp = max_temp; }

    public void setMin_temp(Double min_temp) { this.min_temp = min_temp; }

    public void setPress(Integer press) { this.press = press; }

    public void setWeather(List<String> weather) { this.weather = weather; }

    public Double getLat() { return lat; }

    public Double getLon() { return lon; }

    public String getCity() { return city; }

    public Integer getId() { return id; }

    public String getCountry() { return country; }

    public Double getSpeed() { return speed; }

    public Integer getDeg() { return deg; }

    public Double getTemp() { return temp; }

    public Double getMax_temp() { return max_temp; }

    public Double getMin_temp() { return min_temp; }

    public Integer getClouds() { return clouds; }

    public Integer getHumid() { return humid; }

    public Integer getPress() { return press; }

    public List<String> getWeather() { return weather; }

    public abstract void readJSON(HttpURLConnection urlConnection);

    public abstract void readJSONSubParams(JsonReader jsonReader);

    public abstract void readJSONWeather(JsonReader jsonReader);
}
