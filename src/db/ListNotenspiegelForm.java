package db;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;

/**
 * Die Klasse ListNotenspiegelForm erstellt eine grafische Benutzeroberfläche
 * (GUI), um den Notenspiegel aus einer Datenbank anzuzeigen.
 */

public class ListNotenspiegelForm {

    public static void main(String[] args) {
        // Erstellen des Fensters
        JFrame frame = new JFrame("List Notenspiegel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 700);
        frame.setLayout(new BorderLayout());

        // "Zurück"-Button
        JButton backButton = new JButton("Zurück");
        frame.add(backButton, BorderLayout.NORTH);

        backButton.addActionListener(e -> {
            frame.dispose(); // Schließt das aktuelle Fenster
            MainWindow.main(null); // Öffnet die Haupt-GUI
        });

        // Erstellen der Tabelle
        String[] columnNames = { "Student Name", "Professor Name", "Module Name", "Note", "Versuch", "Status" };
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);

        // Hinzufügen eines RowSorters für die Tabelle
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Filterpanel erstellen
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new FlowLayout());

        // Textfeld für allgemeine Suche
        JTextField searchField = new JTextField(20);
        searchField.setToolTipText("Suche nach Name oder Modul");
        filterPanel.add(new JLabel("Suche nach Name oder Modul:"));
        filterPanel.add(searchField);

        // Dropdown-Menü für Statusfilter
        JComboBox<String> statusFilter = new JComboBox<>(new String[] { "Alle", "BE", "NB" });
        filterPanel.add(new JLabel("Status:"));
        filterPanel.add(statusFilter);

        // Dropdown-Menü für Studentenfilter
        JComboBox<String> studentFilter = new JComboBox<>();
        studentFilter.addItem("Alle");
        filterPanel.add(new JLabel("Student:"));
        filterPanel.add(studentFilter);

        frame.add(filterPanel, BorderLayout.SOUTH);

        // Filter-Ereignisse
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filterTable();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filterTable();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filterTable();
            }

            private void filterTable() {
                applyFilters(sorter, searchField, statusFilter, studentFilter);
            }
        });

        statusFilter.addActionListener(e -> applyFilters(sorter, searchField, statusFilter, studentFilter));
        studentFilter.addActionListener(e -> applyFilters(sorter, searchField, statusFilter, studentFilter));

        // Daten in die Tabelle laden
        DatabaseUtils.loadNotenspiegelData(tableModel, studentFilter);

        // Fenster sichtbar machen
        frame.setVisible(true);
    }

    /**
     * Wendet die Filter auf die Tabelle basierend auf dem Suchfeld, Status und
     * Student an.
     */
    private static void applyFilters(TableRowSorter<DefaultTableModel> sorter, JTextField searchField,
            JComboBox<String> statusFilter, JComboBox<String> studentFilter) {
        String searchText = searchField.getText().toLowerCase();
        String selectedStatus = (String) statusFilter.getSelectedItem();
        String selectedStudent = (String) studentFilter.getSelectedItem();

        sorter.setRowFilter(new RowFilter<DefaultTableModel, Object>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
                String studentName = entry.getStringValue(0).toLowerCase();
                String professorName = entry.getStringValue(1).toLowerCase();
                String moduleName = entry.getStringValue(2).toLowerCase();
                String rowStatus = entry.getStringValue(5);

                boolean matchesText = studentName.contains(searchText)
                        || professorName.contains(searchText)
                        || moduleName.contains(searchText);

                boolean matchesStatus = "Alle".equals(selectedStatus) || rowStatus.equalsIgnoreCase(selectedStatus);
                boolean matchesStudent = "Alle".equals(selectedStudent)
                        || studentName.equalsIgnoreCase(selectedStudent);

                return matchesText && matchesStatus && matchesStudent;
            }
        });
    }
}
