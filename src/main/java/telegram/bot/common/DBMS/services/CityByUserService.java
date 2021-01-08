package telegram.bot.common.DBMS.services;

import telegram.bot.common.DBMS.dao.CityByUserDao;
import telegram.bot.common.DBMS.models.CityByUser;

import java.util.List;

public class CityByUserService {
    private final CityByUserDao cityByUserDao = new CityByUserDao();

    public CityByUserService() {}

    public CityByUser findCityByUser(Integer id) { return cityByUserDao.findCityByUserById(id); }

    public List<CityByUser> findAllCitiesByUser() { return cityByUserDao.findAll(); }

    public void saveCityByUser(CityByUser cityByUser) { cityByUserDao.save(cityByUser); }

    public void updateCityByUser(CityByUser cityByUser) { cityByUserDao.update(cityByUser); }

    public void deleteCityByUser(CityByUser cityByUser) { cityByUserDao.delete(cityByUser); }
}
