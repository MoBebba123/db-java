package db;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Map;

/**
 * Die Klasse AbschlussarbeitForm implementiert eine grafische
 * Benutzeroberfläche (GUI) zur
 * Eingabe und Speicherung von Abschlussarbeiten in einer Datenbank.
 * 
 * Die Benutzeroberfläche ermöglicht die Auswahl von Studenten und Professoren
 * aus einer
 * dynamischen Dropdown-Liste sowie die Eingabe von Themen und Datumsfeldern.
 */

public class AbschlussarbeitForm {

    public static void main(String[] args) {
        // Create the frame
        JFrame frame = new JFrame("Abschlussarbeit hinzugefügen");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setLayout(null);

        // Create a "Return" button
        JButton returnButton = new JButton("Zurück");
        returnButton.setBounds(10, 10, 100, 25);
        frame.add(returnButton);

        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose(); // Schließt das aktuelle Fenster
                MainWindow.main(null); // Öffnet die Haupt-GUI
            }
        });

        // Create labels and fields
        JLabel themaLabel = new JLabel("Thema:");
        themaLabel.setBounds(50, 50, 100, 25);
        frame.add(themaLabel);

        JTextField themaField = new JTextField();
        themaField.setBounds(150, 50, 250, 25);
        frame.add(themaField);

        JLabel startdatumLabel = new JLabel("Startdatum:");
        startdatumLabel.setBounds(50, 90, 100, 25);
        frame.add(startdatumLabel);

        JSpinner startdatumPicker = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor startEditor = new JSpinner.DateEditor(startdatumPicker, "yyyy-MM-dd");
        startdatumPicker.setEditor(startEditor);
        startdatumPicker.setBounds(150, 90, 250, 25);
        frame.add(startdatumPicker);

        JLabel anmeldedatumLabel = new JLabel("Anmeldedatum:");
        anmeldedatumLabel.setBounds(50, 130, 100, 25);
        frame.add(anmeldedatumLabel);

        JSpinner anmeldedatumPicker = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor anmeldedatumEditor = new JSpinner.DateEditor(anmeldedatumPicker, "yyyy-MM-dd");
        anmeldedatumPicker.setEditor(anmeldedatumEditor);
        anmeldedatumPicker.setBounds(150, 130, 250, 25);
        frame.add(anmeldedatumPicker);

        JLabel abgabedatumLabel = new JLabel("Abgabedatum:");
        abgabedatumLabel.setBounds(50, 170, 100, 25);
        frame.add(abgabedatumLabel);

        JSpinner abgabedatumPicker = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor abgabedatumEditor = new JSpinner.DateEditor(abgabedatumPicker, "yyyy-MM-dd");
        abgabedatumPicker.setEditor(abgabedatumEditor);
        abgabedatumPicker.setBounds(150, 170, 250, 25);
        frame.add(abgabedatumPicker);

        JLabel studentLabel = new JLabel("Student:");
        studentLabel.setBounds(50, 210, 100, 25);
        frame.add(studentLabel);

        JComboBox<String> studentDropdown = new JComboBox<>();
        Map<String, Integer> studentMap = DatabaseUtils.loadStudentsFromDB();
        for (String studentName : studentMap.keySet()) {
            studentDropdown.addItem(studentName);
        }
        studentDropdown.setBounds(150, 210, 250, 25);
        frame.add(studentDropdown);

        JLabel professorLabel = new JLabel("Professor:");
        professorLabel.setBounds(50, 250, 100, 25);
        frame.add(professorLabel);

        JComboBox<String> professorDropdown = new JComboBox<>();
        Map<String, Integer> professorMap = DatabaseUtils.loadProfessorsFromDB();
        for (String professorName : professorMap.keySet()) {
            professorDropdown.addItem(professorName);
        }
        professorDropdown.setBounds(150, 250, 250, 25);
        frame.add(professorDropdown);

        // Add button to submit form
        JButton submitButton = new JButton("Abschlussarbeit hinzugefügen");
        submitButton.setBounds(150, 300, 220, 30);
        frame.add(submitButton);

        // Add action listener to the button
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String thema = themaField.getText();
                java.util.Date startdatum = (java.util.Date) startdatumPicker.getValue();
                java.util.Date anmeldedatum = (java.util.Date) anmeldedatumPicker.getValue();
                java.util.Date abgabedatum = (java.util.Date) abgabedatumPicker.getValue();

                String selectedStudent = (String) studentDropdown.getSelectedItem();
                int studentId = studentMap.get(selectedStudent);

                String selectedProfessor = (String) professorDropdown.getSelectedItem();
                int profId = professorMap.get(selectedProfessor);

                try {
                    // Use java.sql.Timestamp for date values
                    DatabaseUtils.addAbschlussarbeit(
                            thema,
                            new java.sql.Timestamp(startdatum.getTime()),
                            new java.sql.Timestamp(anmeldedatum.getTime()),
                            new java.sql.Timestamp(abgabedatum.getTime()),
                            studentId,
                            profId);
                    JOptionPane.showMessageDialog(frame, "Die Abschlussarbeit wurde erfolgreich hinzugefügt!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
                }
            }
        });

        // Make the frame visible
        frame.setVisible(true);
    }

}
