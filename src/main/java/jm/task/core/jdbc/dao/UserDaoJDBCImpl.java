package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class UserDaoJDBCImpl implements UserDao {

    private final Connection connection;
    private static final Logger LOGGER = Util.getLogger(UserDaoJDBCImpl.class);


    public UserDaoJDBCImpl() {
        this.connection = Util.getConnection();
    }


    public void createUsersTable() {
        String[] sqls = {
                "DROP TABLE IF EXISTS users;",
                "DROP DATABASE IF EXISTS ITM_task012_TaskJDBC_Task_1_db;",
                "CREATE DATABASE ITM_task012_TaskJDBC_Task_1_db;",

                "CREATE TABLE users (" +
                    "user_id BIGSERIAL PRIMARY KEY, " +         // Поле id с автоинкрементом (эквивалент LONG в Java) // В PostgreSQL для создания автоинкрементного поля используется тип данных SERIAL, BIGSERIAL, или SMALLSERIAL.;
                    "user_name VARCHAR(50) NOT NULL, "  +       // Поле name с максимальной длиной 50 символов;
                    "user_lastName VARCHAR(70) NOT NULL, " +    // Поле lastName с максимальной длиной 70 символов;
                    "user_age SMALLINT NOT NULL);"
        };
        executeSql(connection, sqls);
        LOGGER.log(Level.FINER, "createUsersTable(): finished");
    }

    public void dropUsersTable() {
        String[] sqls = {
                "DROP TABLE IF EXISTS users;",                  // Удаление таблицы users из базы данных, если она существует
//                "DROP DATABASE IF EXISTS ITM_task012_TaskJDBC_Task_1_db;", // удалять саму БД, думаю пока не стоит
        };
        LOGGER.log(Level.FINER, "dropUsersTable(): finished");
    }


    public void saveUser(String name, String lastName, byte age) {
        if (!Util.isExistsTable()) {
            LOGGER.log(Level.INFO, "An attempt to save the User to a non-existent table. Creating a table for the User;");
            createUsersTable();
        }

        String sql = "INSERT INTO USERS (user_name, user_lastName, user_age) VALUES(?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, lastName);
            preparedStatement.setByte(3, age);

            preparedStatement.execute();
        } catch (SQLException e) {
            LOGGER.warning("SQLException: \n" + Arrays.toString(e.getStackTrace()));
            e.printStackTrace();
        }
        LOGGER.info("finished;");
    }


    public void removeUserById(long id) {
        String sql = "DELETE FROM users WHERE user_id = ?;";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, (int) id);

            preparedStatement.execute();
        } catch (SQLException e) {
            LOGGER.log(Level.INFO, "SQLException: \n" + Arrays.toString(e.getStackTrace()));
        }
        LOGGER.log(Level.FINE, "removeUserById('" + id + "'): finished;");
    }


    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users;";

        List<User> list = new ArrayList<>();
        try (Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getLong("user_id"));
                user.setName(resultSet.getString("user_name"));
                user.setLastName(resultSet.getString("user_lastName"));
                user.setAge(resultSet.getByte("user_age"));

                list.add(user);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.INFO, "SQLException: \n" + Arrays.toString(e.getStackTrace()));
        }
        LOGGER.log(Level.FINE, "getAllUsers(): finished;");
        return list;
    }

    public void cleanUsersTable() {
        String sql = "TRUNCATE users;";
        executeSql(connection, sql);
        LOGGER.log(Level.FINE,"cleanUsersTable(): finished;");
    }





    // services
    private void executeSql(Connection connection, String... sqls) {
        try (Statement statement = connection.createStatement()) {
            for (String sql : sqls) {
                statement.execute(sql);
                LOGGER.log(Level.FINEST, "executeSql: current statement = '" + statement + "'");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "SQLException: \n" + Arrays.toString(sqls) + "\n" + e.getMessage());
        }
    }
}
