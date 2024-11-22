package db;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainWindow {

    public static void main(String[] args) {
        // Create the main frame
        JFrame frame = new JFrame("Main GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setLayout(null);

        // Add "Add Abschlussarbeit" button
        JButton addAbschlussarbeitButton = new JButton("Abschlussarbeit hinzugefügen");
        addAbschlussarbeitButton.setBounds(100, 50, 270, 50);
        frame.add(addAbschlussarbeitButton);

        // Add "List Notenspiegel" button
        JButton listNotenspiegelButton = new JButton("List Notenspiegel");
        listNotenspiegelButton.setBounds(100, 150, 270, 50);
        frame.add(listNotenspiegelButton);
        // Add "List Pending Grades" button
        JButton listPendingGradesButton = new JButton("Prüfungen ohne Bewertung (6 woche +)");
        listPendingGradesButton.setBounds(100, 250, 270, 50);
        frame.add(listPendingGradesButton);

        // Action listener for "Add Abschlussarbeit"
        addAbschlussarbeitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose(); // Close the main frame
                AbschlussarbeitForm.main(null); // Open the Abschlussarbeit form
            }
        });

        // Action listener for "List Notenspiegel"
        listNotenspiegelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose(); // Close the main frame
                ListNotenspiegelForm.main(null); // Open the ListNotenspiegel form
            }
        });
        // Action listener for "List Pending Grades"
        listPendingGradesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose(); // Close the main frame
                ListPendingGradesWithAddNoteForm.main(null); // Open the ListPendingGrades form
            }
        });
        // Make the frame visible
        frame.setVisible(true);
    }

}
