package jm.task.core.jdbc.service;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.dao.*;
import jm.task.core.jdbc.util.Util;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class UserServiceImpl implements UserService {

    private final UserDao userDao = new UserDaoJDBCImpl(); // для JDBC
    private static final Logger LOGGER = Util.getLogger(UserServiceImpl.class);


    public void createUsersTable() {
        userDao.createUsersTable();
        LOGGER.log(Level.FINEST, "createUsersTable(): finished");
    }

    public void dropUsersTable() {
        userDao.dropUsersTable();
        LOGGER.log(Level.FINEST, "dropUsersTable(): finished");
    }

    public void saveUser(String name, String lastName, byte age) {
        userDao.saveUser(name, lastName, age);
        LOGGER.log(Level.FINEST, "saveUser(): finished");
    }

    public void removeUserById(long id) {
        userDao.removeUserById(id);
        LOGGER.log(Level.FINEST, "removeUserById(): finished");
    }

    public List<User> getAllUsers() {
        List<User> result = userDao.getAllUsers();
        LOGGER.log(Level.FINEST, "getAllUsers(): finished");
        return result;
    }

    public void cleanUsersTable() {
        userDao.cleanUsersTable();
        LOGGER.log(Level.FINEST, "cleanUsersTable(): finished");
    }
}
