package db;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Die Klasse ListPendingGradesWithAddNoteForm stellt eine grafische
 * Benutzeroberfläche (GUI) bereit,
 * um Prüfungen ohne Noten anzuzeigen. Benutzer können durch Doppelklick auf
 * eine Zeile eine Note hinzufügen.
 */
public class ListPendingGradesWithAddNoteForm {

    public static void main(String[] args) {
        JFrame frame = createMainFrame();
        DefaultTableModel tableModel = initializeTable(frame);

        loadPendingGrades(tableModel);

        frame.setVisible(true);
    }

    /**
     * Erstellt und initialisiert das Hauptfenster.
     *
     * @return Das Hauptfenster (JFrame).
     */
    private static JFrame createMainFrame() {
        JFrame frame = new JFrame("Prüfungen ohne Bewertung (6 woche +)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        JLabel hintLabel = new JLabel(
                "Hinweis: Wählen Sie eine Zeile aus und doppelklicken Sie mit der rechten Taste, um eine Note hinzuzufügen",
                SwingConstants.CENTER);
        hintLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        hintLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        frame.add(hintLabel, BorderLayout.NORTH);

        JButton backButton = new JButton("Zurück");
        backButton.addActionListener(e -> {
            frame.dispose();
            MainWindow.main(null);
        });
        frame.add(backButton, BorderLayout.SOUTH);

        return frame;
    }

    /**
     * Initialisiert die Tabelle und fügt sie dem angegebenen Fenster hinzu.
     *
     * @param frame Das Fenster, zu dem die Tabelle hinzugefügt wird.
     * @return Das TableModel der Tabelle.
     */
    private static DefaultTableModel initializeTable(JFrame frame) {
        String[] columnNames = { "Student Name", "Angemeldet Seit", "PrüfungsDatum", "Modul", "Student ID",
                "PrüfungsID", "Versuch" };
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Hide the "Student ID" and "PrüfungsID" columns
        hideColumn(table, 4);
        hideColumn(table, 5);

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        handleRowDoubleClick(tableModel, row, frame);
                    }
                }
            }
        });

        return tableModel;
    }

    /**
     * Versteckt Spalte in einer Tabelle.
     *
     * @param table       Die Tabelle, in der die Spalte versteckt werden soll.
     * @param columnIndex Der Index der zu versteckenden Spalte.
     */
    private static void hideColumn(JTable table, int columnIndex) {
        table.getColumnModel().getColumn(columnIndex).setMinWidth(0);
        table.getColumnModel().getColumn(columnIndex).setMaxWidth(0);
        table.getColumnModel().getColumn(columnIndex).setPreferredWidth(0);
    }

    /**
     * Behandelt den Doppelklick auf eine Tabellenzeile.
     *
     * @param tableModel Das TableModel der Tabelle.
     * @param row        Die ausgewählte Zeile.
     * @param frame      Das Hauptfenster.
     */
    private static void handleRowDoubleClick(DefaultTableModel tableModel, int row, JFrame frame) {
        int studentId = Integer.parseInt(tableModel.getValueAt(row, 4).toString());
        int pruefungsId = Integer.parseInt(tableModel.getValueAt(row, 5).toString());
        String studentName = (String) tableModel.getValueAt(row, 0);
        String modul = (String) tableModel.getValueAt(row, 3);
        int versuch = Integer.parseInt(tableModel.getValueAt(row, 6).toString()); // Retrieve the 'Versuch' value
        showAddNoteDialog(frame, tableModel, row, studentId, pruefungsId, studentName, modul, versuch);
    }

    /**
     * Lädt die ausstehenden Prüfungen ohne Noten aus der Datenbank und
     * füllt die Tabelle.
     *
     * @param tableModel Das TableModel, das mit den Daten gefüllt wird.
     */
    private static void loadPendingGrades(DefaultTableModel tableModel) {
        String query = "SELECT p.datum, m.titel AS modul_title, " +
                "s.id AS student_id, s.Vorname || ' ' || s.Nachname AS student_name, " +
                "sp.seit, p.id AS pruefungsid, COALESCE(sap.versuch, 0) AS versuch " +
                "FROM pruefungen p " +
                "JOIN module m ON p.modul_id = m.id " +
                "JOIN studentpruefungenanmelden sp ON p.id = sp.pruefung_id " +
                "JOIN student s ON sp.student_id = s.id " +
                "LEFT JOIN studentabsolviertpruefungen sap " +
                "ON sp.student_id = sap.student_id AND sp.pruefung_id = sap.prüfungsid " +
                "WHERE sap.note IS NULL AND p.datum < NOW() - INTERVAL '6 weeks'";

        try (Connection connection = PostgreSQLConnection.getConnection();
                PreparedStatement stmt = connection.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                tableModel.addRow(new Object[] {
                        rs.getString("student_name"),
                        rs.getDate("seit").toString(),
                        rs.getDate("datum").toString(),
                        rs.getString("modul_title"),
                        rs.getInt("student_id"),
                        rs.getInt("pruefungsid"),
                        rs.getInt("versuch")

                });
            }

        } catch (Exception e) {
            handleException("Fehler beim Abrufen der Daten", e);
        }
    }

    /**
     * Zeigt einen Dialog zum Hinzufügen einer Note an.
     *
     * @param frame       Das Hauptfenster.
     * @param tableModel  Das TableModel der Tabelle.
     * @param row         Die ausgewählte Tabellenzeile.
     * @param studentId   Die ID des Studenten.
     * @param pruefungsId Die ID der Prüfung.
     * @param studentName Der Name des Studenten.
     * @param modul       Der Name des Moduls.
     * @param versuch     versuch des Moduls.
     */
    private static void showAddNoteDialog(JFrame frame, DefaultTableModel tableModel, int row, int studentId,
            int pruefungsId, String studentName, String modul, int versuch) {
        JTextField noteField = new JTextField();
        JPanel panel = new JPanel(new GridLayout(3, 1));
        panel.add(new JLabel("Student Name: " + studentName));
        panel.add(new JLabel("Module: " + modul));

        panel.add(new JLabel("Note eingeben (1-4 für BE, >4 für NB):"));
        panel.add(noteField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Note hinzufügen", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                int grade = Integer.parseInt(noteField.getText().trim());

                String status;
                if (grade >= 1 && grade <= 4) {
                    status = "BE";
                } else if (grade > 4) {
                    status = "NB";
                } else {
                    JOptionPane.showMessageDialog(frame,
                            "Ungültige Note. Die Note muss 1 oder größer sein.",
                            "Fehler",
                            JOptionPane.ERROR_MESSAGE);
                    return; // Exit if the grade is invalid
                }

                updateDatabase(studentId, pruefungsId, grade, status, versuch);
                JOptionPane.showMessageDialog(frame, "Note erfolgreich hinzugefügt!");
                tableModel.setRowCount(0);
                loadPendingGrades(tableModel);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Ungültige Note. Bitte geben Sie eine gültige Zahl ein.", "Fehler",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Aktualisiert die Datenbank mit der hinzugefügten Note.
     *
     * @param studentId   Die ID des Studenten.
     * @param pruefungsId Die ID der Prüfung.
     * @param grade       Die hinzugefügte Note.
     * @param status      Der Status basierend auf der Note (BE oder NB).
     */
    private static void updateDatabase(int studentId, int pruefungsId, int grade, String status, int versuch) {

        String updateQuery = "UPDATE studentabsolviertpruefungen " +
                "SET note = ?, status = ? " +
                "WHERE student_id = ? AND prüfungsid = ? AND versuch = ?";

        String deleteQuery = "DELETE FROM studentpruefungenanmelden " +
                "WHERE student_id = ? AND pruefung_id = ?";

        try (Connection connection = PostgreSQLConnection.getConnection();
                PreparedStatement updateStmt = connection.prepareStatement(updateQuery);
                PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)) {

            updateStmt.setInt(1, grade); // Update note
            updateStmt.setString(2, status); // Update status (BE or NB)
            updateStmt.setInt(3, studentId); // Filter by student_id
            updateStmt.setInt(4, pruefungsId); // Filter by pruefungsId
            updateStmt.setInt(5, versuch); // Filter by versuch

            updateStmt.executeUpdate();

            deleteStmt.setInt(1, studentId);
            deleteStmt.setInt(2, pruefungsId);
            deleteStmt.executeUpdate();

        } catch (Exception e) {
            handleException("Fehler beim Aktualisieren der Daten", e);
        }
    }

    private static void handleException(String message, Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, message + ": " + e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
    }
}
