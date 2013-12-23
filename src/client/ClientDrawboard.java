package client;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import model.Canvas;
import model.Drawboard;
import model.Interpreter;
import model.Mode;

public class ClientDrawboard extends JFrame implements Drawboard {
    
    private static final long serialVersionUID = 1L;
    private final int width = 500;
    private final int height = 500;
    
    private final int gameID;
    private final int userID;
    private final String username;
    private final HashMap<Integer, String> users = new HashMap<Integer, String>(); 
            // all users including yourself
    
    private final PrintWriter out;
    private final BufferedReader in;
    private final Canvas canvas;
    private final Interpreter interpreter;
    
    private ClientDrawboardPanel panel;
    
    public ClientDrawboard(int id, String name, int game, final PrintWriter out, BufferedReader in) {
        gameID = game;
        userID = id;
        username = name;
        // server will send back "USERS" message, which includes all users including self,
        // so no need to add to user hash map right now
        
        this.out = out;
        this.in = in;
        
        interpreter = new Interpreter(this, false);
        canvas = new Canvas(width, height, this.out);
        
        ClientDrawboardWorker worker = new ClientDrawboardWorker();
        worker.execute();
        
        panel = new ClientDrawboardPanel();
        setTitle(String.format("Collaborative Paint %d - %s (ID: %d)",
                gameID, username, userID));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        add(panel);
        pack();
        setVisible(true);
        
        addWindowListener(new WindowListener() {

            @Override 
            public void windowClosing(WindowEvent e) {
                out.println(String.format("REMOVE %d %s", userID, username));
            }
            @Override 
            public void windowClosed(WindowEvent e) { 
                out.println(String.format("REMOVE %d %s", userID, username));
            }

            @Override public void windowOpened(WindowEvent e) { }
            @Override public void windowIconified(WindowEvent e) { }
            @Override public void windowDeiconified(WindowEvent e) { }
            @Override public void windowActivated(WindowEvent e) { }
            @Override public void windowDeactivated(WindowEvent e) { }            
        });
    }
    
    public Canvas getCanvas() {
        return canvas;
    }
    
    public void addUser(int id, String username) {
        users.put(id, username);
        panel.updateUsers();
    }
    
    public void removeUser(int id, String username) {
        users.remove(id);
        panel.updateUsers();
    }
    
    // processes requests from server on background thread, so UI remains responsive
    private class ClientDrawboardWorker extends SwingWorker<String, String> {
        @Override
        protected String doInBackground() throws Exception {
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                interpreter.interpret(line);
            }
            return null;
        }
    }
    
    private class ClientDrawboardPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private final JToggleButton drawButton;
        private final JToggleButton eraseButton;
        private final ButtonGroup modeGroup;
        private final JSlider widthSlider;
        private final JButton colorButton;
        private final JLabel currentUsers;

        public ClientDrawboardPanel() {
            GroupLayout layout = new GroupLayout(this);
            setLayout(layout);            
            layout.setAutoCreateContainerGaps(true);
            layout.setAutoCreateGaps(true);
                        
            ModeListener modeListener = new ModeListener();
            drawButton = new JToggleButton("Draw");
            drawButton.setName("Draw");
            drawButton.setSelected(true);   
            drawButton.addActionListener(modeListener);
            
            eraseButton = new JToggleButton("Erase");
            eraseButton.setName("Erase");
            eraseButton.addActionListener(modeListener);
            
            modeGroup = new ButtonGroup();
            modeGroup.add(drawButton);
            modeGroup.add(eraseButton);
                        
            widthSlider = new JSlider(1, 20, 1);
            widthSlider.setName("Stroke Width");
            
            widthSlider.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    int value = widthSlider.getValue();
                    canvas.setStrokeWidth(value);
                }             
            });
            
            colorButton = new JButton("Color");
            colorButton.setName("Color");
            
            colorButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Color color = JColorChooser.showDialog(colorButton, "Choose Color", 
                            canvas.getStrokeColor());
                    canvas.setStrokeColor(color);
                    colorButton.setForeground(color);
                }
            });
            
            currentUsers = new JLabel("Current Painters: ");   
            updateUsers();
            
            layout.setHorizontalGroup(
                    layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(drawButton)
                                .addComponent(eraseButton)
                                .addComponent(widthSlider)
                                .addComponent(colorButton))
                        .addComponent(canvas)
                        .addComponent(currentUsers));
            
            layout.setVerticalGroup(
                    layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup()
                                .addComponent(drawButton)
                                .addComponent(eraseButton)
                                .addComponent(widthSlider)
                                .addComponent(colorButton))
                        .addComponent(canvas)
                        .addComponent(currentUsers));
        }
        
        public void updateUsers() {
            String text = "Current Painters: ";
            for (String name : users.values()) {
                text += name + ", ";
            }
            currentUsers.setText(text.substring(0, text.length() - 2));
        }

        private class ModeListener implements ActionListener {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = ((JToggleButton) e.getSource()).getName();
                if (name.equals("Draw")) {
                    canvas.setMode(Mode.DRAW);
                } else if (name.equals("Erase")) {
                    canvas.setMode(Mode.ERASE);
                }
            }       
        }
    } 
}