package client;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;


public class Login extends JFrame {

    private static final long serialVersionUID = 1L;
    private final PrintWriter out;
    private final BufferedReader in;
    
    public Login(PrintWriter out, BufferedReader in) {       
        this.out = out;
        this.in = in;
        
        setTitle("Login Collaborative Paint");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        JPanel panel = new LoginPanel();
        add(panel);
        pack();
        setVisible(true);
    }
    
    public void loggedIn(final String name, final int userID, final int gameID) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                close(); // close login window
                new ClientDrawboard(userID, name, gameID, out, in); // and open drawboard
            }
        });
    }
    
    public void close() {
        setVisible(false);
        this.dispose();
    }
        
    private class LoginPanel extends JPanel {
        
        private static final long serialVersionUID = 1L;
        private JButton loginButton;
        private JLabel nameMessage;
        private JLabel errorMessage;
        private JLabel boardMessage;
        private JTextField username;
        private JTextField boardNum;
        
        public LoginPanel() {
            GroupLayout layout = new GroupLayout(this);
            setLayout(layout);            
            layout.setAutoCreateContainerGaps(true);
            layout.setAutoCreateGaps(true);
                        
            loginButton = new JButton("Login");
            
            boardMessage = new JLabel("Enter game ID: ");
            boardNum = new JTextField();
            
            nameMessage = new JLabel("Enter username: ");
            username = new JTextField();
            
            errorMessage = new JLabel("Invalid username and/or Paint ID.");
            errorMessage.setForeground(this.getBackground());
            
            ActionListener loginListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String name = username.getText();
                    String game = boardNum.getText();
                    
                    // checks if input is valid format
                    if (!name.matches("[A-Za-z0-9._]+") || !game.matches("[0-9]+")) {
                        errorMessage.setForeground(Color.RED);
                        boardNum.setText("");
                        username.setText("");
                    } else {                       
                        try {
                            int gameID = Integer.parseInt(game);         
                            out.println(String.format("BOARD %d NEWUSER %s", gameID, name));
                            
                            String approved = in.readLine();
                            if (approved.startsWith("USERID")) { // valid input
                                String[] args = approved.split(" ");
                                int userID = Integer.parseInt(args[1]);
                                loggedIn(name, userID, gameID); // opens drawboard
                                
                            } else { // approved == "ERROR", invalid input
                                errorMessage.setForeground(Color.RED);
                                boardNum.setText("");
                                username.setText("");
                            }
                            
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }                        
                    }
                    
                }                
            };
            
            loginButton.addActionListener(loginListener);
            username.addActionListener(loginListener);
            boardNum.addActionListener(loginListener);

            // layout for window
            layout.setHorizontalGroup(
                    layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(nameMessage, 120, 120, 120)
                                .addComponent(username))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(boardMessage, 120, 120, 120)
                                .addComponent(boardNum))
                        .addComponent(errorMessage, 300, 300, 300)
                        .addComponent(loginButton));
            
            layout.setVerticalGroup(
                    layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                                .addComponent(nameMessage)
                                .addComponent(username))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(boardMessage)
                                .addComponent(boardNum))
                        .addComponent(errorMessage)
                        .addComponent(loginButton));
        }
    }
}