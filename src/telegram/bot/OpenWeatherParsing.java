package telegram.bot;

import com.github.prominence.openweathermap.api.enums.Accuracy;
import com.github.prominence.openweathermap.api.enums.Language;
import com.github.prominence.openweathermap.api.enums.UnitSystem;
import com.github.prominence.openweathermap.api.impl.OpenWeatherMapClient;

public class OpenWeatherParsing {
    OpenWeatherMapClient openWeatherMapClient;

    public OpenWeatherParsing() {
        openWeatherMapClient = new OpenWeatherMapClient("2d5ba0d2e4b46c41d9037fc141689ff8");
    }

    public String getCurWeatherByCity(String city) {
        return openWeatherMapClient
                .currentWeather()
                .single()
                .byCityName(city)
                .accuracy(Accuracy.ACCURATE)
                .language(Language.RUSSIAN)
                .unitSystem(UnitSystem.METRIC)
                .retrieve()
                .asJava()
                .toString();
    }
}
