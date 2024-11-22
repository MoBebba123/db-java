package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 * Die Klasse DatabaseUtils bietet Dienstprogramme für Datenbankoperationen.
 */
public class DatabaseUtils {

    /**
     * Fügt eine neue Abschlussarbeit in die Datenbank ein, sofern der Student nicht
     * bereits registriert ist.
     *
     * @param thema        Das Thema der Abschlussarbeit.
     * @param startdatum   Das Startdatum der Abschlussarbeit.
     * @param anmeldedatum Das Datum der Anmeldung.
     * @param abgabedatum  Das Abgabedatum der Abschlussarbeit.
     * @param studentId    Die ID des Studenten, der die Abschlussarbeit einreicht.
     * @param profId       Die ID des Professors, der die Abschlussarbeit betreut.
     * @throws SQLException Wenn ein Fehler bei der Datenbankoperation auftritt oder
     *                      der Student bereits registriert ist.
     */

    public static void addAbschlussarbeit(String thema, java.sql.Timestamp startdatum, java.sql.Timestamp anmeldedatum,
            java.sql.Timestamp abgabedatum, int studentId, int profId) throws SQLException {
        // Überprüfen, ob die Eingaben gültig sind
        if (thema == null || thema.trim().isEmpty()) {
            throw new IllegalArgumentException("Das Thema darf nicht leer sein.");
        }
        if (startdatum == null) {
            throw new IllegalArgumentException("Das Startdatum darf nicht leer sein.");
        }
        if (anmeldedatum == null) {
            throw new IllegalArgumentException("Das Anmeldedatum darf nicht leer sein.");
        }
        if (abgabedatum == null) {
            throw new IllegalArgumentException("Das Abgabedatum darf nicht leer sein.");
        }
        if (studentId <= 0) {
            throw new IllegalArgumentException("Die Student-ID muss gültig sein.");
        }
        if (profId <= 0) {
            throw new IllegalArgumentException("Die Professor-ID muss gültig sein.");
        }

        // SQL-Abfrage zum Überprüfen, ob der Student bereits registriert ist
        String checkQuery = "SELECT COUNT(*) FROM Abschlussarbeit WHERE Studentid = ?";
        // SQL-Abfrage zum Einfügen einer neuen Abschlussarbeit
        String insertQuery = "INSERT INTO Abschlussarbeit (Thema, Startdatum, Anmeldedatum, Abgabedatum, Studentid, profid) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection connection = PostgreSQLConnection.getConnection();
                PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {

            // Überprüfen, ob der Student bereits registriert ist
            checkStmt.setInt(1, studentId);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new SQLException("Der Student ist bereits für eine Abschlussarbeit registriert.");
                }
            }

            // Einfügen der neuen Abschlussarbeit, falls der Student nicht registriert ist
            try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                insertStmt.setString(1, thema);
                insertStmt.setTimestamp(2, startdatum);
                insertStmt.setTimestamp(3, anmeldedatum);
                insertStmt.setTimestamp(4, abgabedatum);
                insertStmt.setInt(5, studentId);
                insertStmt.setInt(6, profId);

                insertStmt.executeUpdate();
                System.out.println("Die Abschlussarbeit wurde erfolgreich hinzugefügt!");
            }
        }
    }

    /**
     * Lädt die Notenspiegel-Daten aus der Datenbank und fügt sie der Tabelle hinzu.
     * Füllt auch die Studentendaten in das Dropdown-Menü ein.
     */
    public static void loadNotenspiegelData(DefaultTableModel tableModel, JComboBox<String> studentFilter) {
        String query = "SELECT " +
                "s.Vorname || ' ' || s.Nachname AS student_name, " +
                "p.Vorname || ' ' || p.Nachname AS professor_name, " +
                "m.titel AS module_name, " +
                "sap.Note, " +
                "sap.versuch, " +
                "sap.Status " +
                "FROM StudentAbsolviertPruefungen sap " +
                "JOIN Student s ON sap.Student_ID = s.ID " +
                "JOIN Pruefungen pr ON sap.Prüfungsid = pr.ID " +
                "JOIN Professor p ON pr.prof_id = p.ID " +
                "JOIN Module m ON pr.Modul_ID = m.ID";

        try (Connection connection = PostgreSQLConnection.getConnection();
                PreparedStatement stmt = connection.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {

            ArrayList<String> studentNames = new ArrayList<>();
            while (rs.next()) {
                String studentName = rs.getString("student_name");
                String professorName = rs.getString("professor_name");
                String moduleName = rs.getString("module_name");
                Object note = rs.getObject("Note"); // Note als Object abrufen, um null zu unterstützen
                int versuch = rs.getInt("versuch");
                String status = rs.getString("Status");
                // Wenn Status null oder leer ist, setzen wir "NN"
                String statusValue = (status == null || status.trim().isEmpty()) ? "noch nicht bewertet" : status;

                // Wenn Note null ist, setzen wir "NN"
                String noteValue = (note == null) ? "noch nicht bewertet" : String.valueOf(note);

                // Daten in die Tabelle einfügen
                tableModel.addRow(
                        new Object[] { studentName, professorName, moduleName, noteValue, versuch, statusValue });

                // Studentennamen sammeln
                if (!studentNames.contains(studentName)) {
                    studentNames.add(studentName);
                }
            }

            // Studentennamen im Dropdown hinzufügen
            for (String name : studentNames) {
                studentFilter.addItem(name);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Fehler beim Laden der Daten: " + e.getMessage(), "Fehler",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Fetch students dynamically from the database
    public static Map<String, Integer> loadStudentsFromDB() {
        Map<String, Integer> students = new HashMap<>();
        String query = "SELECT ID, Vorname, Nachname FROM Student";
        try (Connection connection = PostgreSQLConnection.getConnection();
                PreparedStatement stmt = connection.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("ID");
                String name = rs.getString("Vorname") + " " + rs.getString("Nachname");
                students.put(name, id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return students;
    }

    // Fetch professors dynamically from the database
    public static Map<String, Integer> loadProfessorsFromDB() {
        Map<String, Integer> professors = new HashMap<>();
        String query = "SELECT ID, Vorname, Nachname FROM Professor";
        try (Connection connection = PostgreSQLConnection.getConnection();
                PreparedStatement stmt = connection.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("ID");
                String name = rs.getString("Vorname") + " " + rs.getString("Nachname");
                professors.put(name, id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return professors;
    }
}
