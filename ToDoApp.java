import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ToDoApp extends JFrame {
    private JTextField taskInputField;
    private DefaultListModel<String> listModel;
    private JList<String> taskList;
    private ArrayList<String> tasks;
    
    public ToDoApp() {
        tasks = new ArrayList<>();
        initializeGUI();
    }
    
    private void initializeGUI() {
        // Set up the main frame
        setTitle("ToDo List Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        
        // Create components
        createTopPanel();
        createCenterPanel();
        createBottomPanel();

        
        // Make frame visible
        setVisible(true);
    }
    
    private void createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel titleLabel = new JLabel("My ToDo List", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(51, 51, 51));
        
        topPanel.add(titleLabel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);
    }
    
    private void createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(new EmptyBorder(0, 10, 0, 10));
        
        // Input panel
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        taskInputField = new JTextField();
        taskInputField.setFont(new Font("Arial", Font.PLAIN, 14));
        taskInputField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        JButton addButton = new JButton("Add Task");
        addButton.setFont(new Font("Arial", Font.BOLD, 14));
        addButton.setBackground(new Color(76, 175, 80));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.addActionListener(new AddTaskListener());
        
        // Add Enter key functionality to text field
        taskInputField.addActionListener(new AddTaskListener());
        
        inputPanel.add(taskInputField, BorderLayout.CENTER);
        inputPanel.add(addButton, BorderLayout.EAST);
        
        // Task list
        listModel = new DefaultListModel<>();
        taskList = new JList<>(listModel);
        taskList.setFont(new Font("Arial", Font.PLAIN, 14));
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskList.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        JScrollPane scrollPane = new JScrollPane(taskList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Tasks"));
        scrollPane.setPreferredSize(new Dimension(400, 200));
        
        centerPanel.add(inputPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(centerPanel, BorderLayout.CENTER);
    }
    
    private void createBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JButton deleteButton = new JButton("Delete Selected");
        deleteButton.setFont(new Font("Arial", Font.BOLD, 14));
        deleteButton.setBackground(new Color(244, 67, 54));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setFocusPainted(false);
        deleteButton.addActionListener(new DeleteTaskListener());
        
        JButton clearAllButton = new JButton("Clear All");
        clearAllButton.setFont(new Font("Arial", Font.BOLD, 14));
        clearAllButton.setBackground(new Color(255, 152, 0));
        clearAllButton.setForeground(Color.WHITE);
        clearAllButton.setFocusPainted(false);
        clearAllButton.addActionListener(new ClearAllListener());
        
        bottomPanel.add(deleteButton);
        bottomPanel.add(clearAllButton);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    // ActionListener for adding tasks
    private class AddTaskListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String task = taskInputField.getText().trim();
            if (!task.isEmpty()) {
                tasks.add(task);
                listModel.addElement(task);
                taskInputField.setText("");
                taskInputField.requestFocus();
            } else {
                JOptionPane.showMessageDialog(ToDoApp.this, 
                    "Please enter a task!", "Empty Task", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    
    // ActionListener for deleting selected task
    private class DeleteTaskListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedIndex = taskList.getSelectedIndex();
            if (selectedIndex != -1) {
                tasks.remove(selectedIndex);
                listModel.remove(selectedIndex);
            } else {
                JOptionPane.showMessageDialog(ToDoApp.this, 
                    "Please select a task to delete!", "No Selection", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    // ActionListener for clearing all tasks
    private class ClearAllListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!tasks.isEmpty()) {
                int result = JOptionPane.showConfirmDialog(ToDoApp.this,
                    "Are you sure you want to clear all tasks?", 
                    "Confirm Clear All", JOptionPane.YES_NO_OPTION);
                
                if (result == JOptionPane.YES_OPTION) {
                    tasks.clear();
                    listModel.clear();
                }
            } else {
                JOptionPane.showMessageDialog(ToDoApp.this, 
                    "No tasks to clear!", "Empty List", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    public static void main(String[] args) {
        // Create and run the application
        SwingUtilities.invokeLater(() -> new ToDoApp());
    }
}