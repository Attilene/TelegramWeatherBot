package telegram.bot.common.DBMS.services;

import telegram.bot.common.DBMS.dao.CityDao;
import telegram.bot.common.DBMS.models.City;

import java.util.List;

public class CityService {
    private final CityDao cityDao = new CityDao();

    public CityService() {}

    public City findCity(Integer id) { return cityDao.findCityById(id); }

    public List<City> findAllCities() { return cityDao.findAll(); }

    public void saveCity(City city) { cityDao.save(city); }

    public void updateCity(City city) { cityDao.update(city); }

    public void deleteCity(City city) { cityDao.delete(city); }
}
