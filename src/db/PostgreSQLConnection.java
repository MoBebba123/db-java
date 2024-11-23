package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import io.github.cdimascio.dotenv.Dotenv;

/**
 * Die Klasse PostgreSQLConnection bietet eine Methode zur Herstellung einer
 * Verbindung
 * mit einer PostgreSQL-Datenbank.
 * 
 * Diese Klasse verwendet den JDBC-Treiber, um die Verbindung herzustellen. Die
 * Verbindungsdetails wie Host, Port, Benutzername, Passwort und Datenbankname
 * sind
 * als Konstanten definiert.
 */

public class PostgreSQLConnection {

    // Lade die .env-Datei
    public static Dotenv dotenv = Dotenv.load();

    /** Hostname des PostgreSQL-Servers. */
    private static final String HOST = dotenv.get("POSTGRES_HOST");

    /** Portnummer des PostgreSQL-Servers. */
    private static final String PORT = dotenv.get("POSTGRES_PORT");

    /** Benutzername für die Authentifizierung bei der Datenbank. */
    private static final String USER = dotenv.get("POSTGRES_USER");

    /** Passwort für die Authentifizierung bei der Datenbank. */
    private static final String PASSWORD = dotenv.get("POSTGRES_PASSWORD");

    /** Name der Datenbank, mit der die Verbindung hergestellt wird. */
    private static final String DATABASE = dotenv.get("POSTGRES_DATABASE");

    /**
     * Stellt eine Verbindung zur PostgreSQL-Datenbank her.
     * 
     * @return Ein aktives {@link Connection}-Objekt zur PostgreSQL-Datenbank.
     * @throws SQLException Wenn ein Fehler bei der Verbindung auftritt, z. B. wenn
     *                      die Verbindungsdetails falsch sind oder der Server nicht
     *                      erreichbar ist.
     */
    public static Connection getConnection() throws SQLException {
        // Formatieren der JDBC-URL
        String url = String.format("jdbc:postgresql://%s:%s/%s", HOST, PORT, DATABASE);

        // Herstellung der Verbindung
        return DriverManager.getConnection(url, USER, PASSWORD);
    }
}
