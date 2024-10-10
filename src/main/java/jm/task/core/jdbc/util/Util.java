package jm.task.core.jdbc.util;

import jm.task.core.jdbc.exception.ItmConnectionException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.*;


public class Util {

    private static final String PATH_TO_RESOURCES = "src/main/resources/";
    private static final String DB_PROPERTIES_FILE_NAME = "db_config.properties";
    private static Connection connection; // for JDBC
    private static final Logger LOGGER = Util.getLogger(Util.class);


    public static Connection getConnection() {
        Properties properties = new Properties();

        String url = null;
        String username = null;
        String password = null;
        String driver = null;

        try (Reader reader = new InputStreamReader(
                new FileInputStream(PATH_TO_RESOURCES + DB_PROPERTIES_FILE_NAME) , StandardCharsets.UTF_8)) {
            properties.load(reader);

            url = properties.getProperty("db.url");
            username = properties.getProperty("db.username");
            password = properties.getProperty("db.password");
            driver = properties.getProperty("db.driver");

            LOGGER.fine("getConnection(): Properties loaded successfully; " +
                    "driver = '" + driver + "'; url = '" + url + "'; username = '" + username + "'");

            /*
             // Данный кусок кода почему-то не работает корректно, передается ЛИБО имя метода ЛИБО имя класса, вместе никак!
            LOGGER.logp(
                    Level.FINE,
                    Thread.currentThread().getStackTrace()[1].getMethodName(), // Имя текущего метода получим динамически
                    Thread.currentThread().getStackTrace()[0].getClassName(), // Имя текущего класса получим динамически
                    "Тестовое сообщение"); // удалить
            */

            Class.forName(driver); // загрузить класс драйвера JDBC в память и зарегистрировать его в DriverManager
            connection = DriverManager.getConnection(url, username, password); // Установка соединения с БД

        } catch (FileNotFoundException e) { // файл 'db_config.properties' не найден по указанному пути
            String message = String.format("The file named '%s' was not found", DB_PROPERTIES_FILE_NAME);
            LOGGER.log(Level.SEVERE, message, e);
            throw new ItmConnectionException(message, e);
        } catch (IOException e) { // Чтение файла 'db_config.properties' не удалось
            String message = String.format("Failed to read the file named '%s'", DB_PROPERTIES_FILE_NAME);
            LOGGER.log(Level.SEVERE, message, e);
            throw new ItmConnectionException(message, e);
        } catch (ClassNotFoundException e) { // Class.forName(driver) - загрузка драйвера JDBC не удалась
            String message = String.format("Failed to load the driver named '%s'", driver);
            LOGGER.log(Level.SEVERE, message, e);
            throw new ItmConnectionException(message, e);
        } catch (SQLException e) { // DriverManager.getConnection(url, username, password) - соединение с БД не удалось
            String message = String.format("Failed to connect to the database. " +
                    "Parameters: driver: '%s' url: '%s'; username: '%s'", driver, url, username);
            LOGGER.log(Level.SEVERE, message, e);
            throw new ItmConnectionException(message, e);
        }
        LOGGER.log(Level.FINER, "getConnection(): Connection established successfully");
        return connection;
    }



    public static boolean isExistsTable() {
        boolean result = false;
        try (Statement statement = connection.createStatement()) {
            String query = "SELECT 1 FROM users ";

            /*
        - Запрос не выбирает никакие данные из таблицы, а просто возвращает '1' за каждую строку
        - Если в таблице есть строки, то будет возвращен результат с таким количеством строк, сколько записей в таблице.
        - Если таблица пуста, запрос вернет пустой результат (без строк).
        - Если таблица users не существует, роизойдет ошибка: 'ERROR: relation "users" does not exist.' Это означает, что таблицы с именем 'users' нет в базе данных.
            */

            ResultSet resultSet = statement.executeQuery(query);
            result = resultSet.next();
        } catch (SQLException e) {
            LOGGER.warning("SQLException " + Arrays.toString(e.getStackTrace()));
            e.printStackTrace();
        }
        return result;
    }



    public static Logger getLogger(Class clazz) {
        StringBuilder sb = new StringBuilder();
        sb.append("logger_");
        sb.append(clazz.getSimpleName());
        sb.append("_config.properties");

        try (Reader reader = new InputStreamReader(new FileInputStream(PATH_TO_RESOURCES + sb), StandardCharsets.UTF_8)) {
            Properties properties = new Properties();
            properties.load(reader);

            Logger logger = Logger.getLogger(clazz.getName()); // Имя логера будет соответствовать имени переданного класса
            LogManager.getLogManager().reset(); // Очищает все обработчики, которые по умолчанию добавляются к корневому логгеру. Это нужно для того, чтобы предотвратить дублирование вывода в консоль.
            logger.setLevel(Level.parse(properties.getProperty("global.level").toUpperCase())); // Устанавливаем общий уровень для логгера
            logger.setUseParentHandlers(false); // Отключает использование родительских обработчиков, чтобы логгирование не дублировалось.

            if (Boolean.parseBoolean(properties.getProperty("file.is_logging_include"))) { // Создаем обработчик для записи в файл
                FileHandler fileHandler = new FileHandler( // Создаем обработчик для записи в файл
                        properties.getProperty("file.log_path") + properties.getProperty("file.log_name"),
                        Boolean.parseBoolean(properties.getProperty("file.add_true_or_overwrite_false"))); // true - для добавления в конец файла
                fileHandler.setFormatter(new CustomFormatter()); // Используем кастомный форматтер для форматирования вывода в файл
                fileHandler.setLevel(Level.parse(properties.getProperty("file.level_log").toUpperCase())); // Уровень логирования для файла
                logger.addHandler(fileHandler); // Добавляем обработчики к логгеру
            }

            if (Boolean.parseBoolean(properties.getProperty("console.is_logging_include"))) { // Создаем обработчик для вывода в консоль
                ConsoleHandler consoleHandler = new ConsoleHandler();
                consoleHandler.setFormatter(new CustomFormatter()); // Используем кастомный форматтер для консоли
                consoleHandler.setLevel(Level.parse(properties.getProperty("console.level_log").toUpperCase())); // Уровень логирования для консоли
                logger.addHandler(consoleHandler); // Добавляем обработчики к логгеру
            }

            return logger;
        } catch (IOException e) {
            String message = String.format("Ошибка при настройке логгера. //\nFailed to connect to the database. " +
                    "The logger configuration file '%s' was not found in the directory '%s'", sb, PATH_TO_RESOURCES);
            throw new ItmConnectionException(message, e);
        }
    }

    // Кастомный форматтер для форматирования лога по шаблону: "%1$tF %1$tT %4$s %2$s %5$s%6$s%n"
    private static class CustomFormatter extends Formatter {
        private static final String DELIMITER = " // "; // Собственный кастомный разделитель
        private static final String PATTERN =
                  "%1$tF" + DELIMITER
                + "%1$tT" + DELIMITER
                + "%4$s" + DELIMITER
                + "%2$s" + DELIMITER
                + "%5$s%6$s%n";


        @Override
        public String format(LogRecord record) {
            return String.format(PATTERN,
                    new java.util.Date(record.getMillis()), // Дата и время
                    record.getSourceClassName(), // Имя класса
                    record.getSourceMethodName(), // Имя метода
                    record.getLevel().getName(), // Уровень логирования
                    formatMessage(record), // Сообщение
                    record.getThrown() != null ? " " + record.getThrown() : ""); // Исключения (если есть)
        }
    }
}
