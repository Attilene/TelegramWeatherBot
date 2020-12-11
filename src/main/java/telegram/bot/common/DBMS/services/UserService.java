package telegram.bot.common.DBMS.services;

import telegram.bot.common.DBMS.dao.UserDao;
import telegram.bot.common.DBMS.models.User;

import java.util.List;

public class UserService {
    private final UserDao userDao = new UserDao();

    public UserService() {}

    public User findUser(Long id) { return userDao.findUserById(id); }

    public List<User> findAllUsers() { return userDao.findAll(); }

    public void saveUser(User user) { userDao.save(user); }

    public void updateUser(User user) { userDao.update(user); }

    public void deleteUser(User user) { userDao.delete(user); }
}
