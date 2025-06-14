import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class StudentForm1 extends JFrame {
    static final String URL = "jdbc:mysql://localhost:3306/student_db?useSSL=false";
    static final String USER = "root";
    static final String PASSWORD = "sathwik@sql";

    private JTextField nameField, ageField, phoneField, dobField, emailField, fatherField, motherField;
    private JTextArea addressArea, displayArea;
    private JComboBox<String> branchBox;
    private JRadioButton maleButton, femaleButton;
    private JButton submitButton, clearButton, displayButton;

    public StudentForm1() {
        setTitle("Student Form");
        setSize(600, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBounds(0, 0, 600, 700);
        add(panel);

        int y = 20;
        nameField = addField(panel, "Name:", 50, y); y += 40;
        ageField = addField(panel, "Age:", 50, y); y += 40;
        phoneField = addField(panel, "Phone:", 50, y); y += 40;
        emailField = addField(panel, "Email:", 50, y); y += 40;
        fatherField = addField(panel, "Father's Name:", 50, y); y += 40;
        motherField = addField(panel, "Mother's Name:", 50, y); y += 40;

        JLabel branchLabel = new JLabel("Branch:");
        branchLabel.setBounds(50, y, 100, 25);
        panel.add(branchLabel);

        branchBox = new JComboBox<>(new String[]{
            "CSE", "ECE", "EEE", "MECH", "CIVIL", "IT", "AIML", "DS", "AGRI", "BIOTECH", "CHEM", "AUTOMOBILE", "AERO", "TEXTILE", "MINING"
        });
        branchBox.setBounds(180, y, 200, 25);
        panel.add(branchBox);
        y += 40;

        JLabel genderLabel = new JLabel("Gender:");
        genderLabel.setBounds(50, y, 100, 25);
        panel.add(genderLabel);

        maleButton = new JRadioButton("Male");
        femaleButton = new JRadioButton("Female");
        maleButton.setBounds(180, y, 80, 25);
        femaleButton.setBounds(260, y, 80, 25);
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(maleButton);
        genderGroup.add(femaleButton);
        panel.add(maleButton);
        panel.add(femaleButton);
        y += 40;

        dobField = addField(panel, "DOB:", 50, y); y += 40;

        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setBounds(50, y, 100, 25);
        panel.add(addressLabel);

        addressArea = new JTextArea();
        addressArea.setBounds(180, y, 200, 50);
        panel.add(addressArea);
        y += 60;

        submitButton = new JButton("Submit");
        clearButton = new JButton("Clear");
        displayButton = new JButton("Display");

        submitButton.setBounds(50, y, 100, 30);
        clearButton.setBounds(180, y, 100, 30);
        displayButton.setBounds(310, y, 100, 30);
        panel.add(submitButton);
        panel.add(clearButton);
        panel.add(displayButton);
        y += 50;

        displayArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(displayArea);
        scrollPane.setBounds(50, y, 500, 200);
        panel.add(scrollPane);
        displayArea.setEditable(false);

        submitButton.addActionListener(e -> saveToDatabase());
        clearButton.addActionListener(e -> clearFields());
        displayButton.addActionListener(e -> fetchFromDatabase());
    }

    private JTextField addField(JPanel panel, String label, int x, int y) {
        JLabel l = new JLabel(label);
        l.setBounds(x, y, 120, 25);
        panel.add(l);
        JTextField t = new JTextField();
        t.setBounds(180, y, 200, 25);
        panel.add(t);
        return t;
    }

    private void saveToDatabase() {
        String name = nameField.getText();
        String age = ageField.getText();
        String phone = phoneField.getText();
        String email = emailField.getText();
        String father = fatherField.getText();
        String mother = motherField.getText();
        String branch = (String) branchBox.getSelectedItem();
        String gender = maleButton.isSelected() ? "Male" : (femaleButton.isSelected() ? "Female" : "Not Specified");
        String dob = dobField.getText();
        String address = addressArea.getText();

        if (name.isEmpty() || age.isEmpty() || phone.isEmpty() || dob.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all required fields!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO students (name, age, phone, email, father, mother, branch, gender, dob, address) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            stmt.setString(1, name);
            stmt.setInt(2, Integer.parseInt(age));
            stmt.setString(3, phone);
            stmt.setString(4, email);
            stmt.setString(5, father);
            stmt.setString(6, mother);
            stmt.setString(7, branch);
            stmt.setString(8, gender);
            stmt.setString(9, dob);
            stmt.setString(10, address);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Student record saved!");
            clearFields();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fetchFromDatabase() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM students")) {

            StringBuilder records = new StringBuilder("All Student Records:\n");
            while (rs.next()) {
                records.append("Name: ").append(rs.getString("name"))
                        .append(", Age: ").append(rs.getInt("age"))
                        .append(", Phone: ").append(rs.getString("phone"))
                        .append(", Email: ").append(rs.getString("email"))
                        .append(", Father: ").append(rs.getString("father"))
                        .append(", Mother: ").append(rs.getString("mother"))
                        .append(", Branch: ").append(rs.getString("branch"))
                        .append(", Gender: ").append(rs.getString("gender"))
                        .append(", DOB: ").append(rs.getString("dob"))
                        .append(", Address: ").append(rs.getString("address"))
                        .append("\n\n");
            }
            displayArea.setText(records.toString());

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        nameField.setText("");
        ageField.setText("");
        phoneField.setText("");
        emailField.setText("");
        fatherField.setText("");
        motherField.setText("");
        dobField.setText("");
        addressArea.setText("");
        maleButton.setSelected(false);
        femaleButton.setSelected(false);
        branchBox.setSelectedIndex(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentForm1().setVisible(true));
    }
}
