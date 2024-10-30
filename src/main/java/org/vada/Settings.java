package org.vada;
import io.github.cdimascio.dotenv.Dotenv;

public class Settings {
    private static final Dotenv dotenv = Dotenv.load();
    public static final String HOST = dotenv.get("DB_HOST");
    public static final String PASSWORD = dotenv.get("DB_PASSWORD");
    public static final String USER = dotenv.get("DB_USER");
    public static final String PORT = dotenv.get("DB_PORT");
    public static final String DRIVER = dotenv.get("DB_DRIVER");
    public static final String DB_NAME = dotenv.get("DB_NAME");
    public static final String URL = "jdbc:" + DRIVER + "://" + HOST + ":" + PORT + "/" + DB_NAME;

    public static final int HTTP_RESPONSE_NOT_FOUND = 404;
}
