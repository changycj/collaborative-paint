package server;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.xml.bind.DatatypeConverter;

import model.Canvas;
import model.Drawboard;
import model.Interpreter;

public class DrawboardServer extends JPanel implements Drawboard{

    private static final long serialVersionUID = 1L;
    private final int width = 500;
    private final int height = 500;
    
    // list of socket output/input and users
    private ArrayList<PrintWriter> outs = new ArrayList<PrintWriter>();
    private ArrayList<BufferedReader> ins = new ArrayList<BufferedReader>();
    private HashMap<Integer, String> users = new HashMap<Integer, String>();
    private int userID = 0;
    
    private Canvas canvas;
    private Interpreter interpreter;
    
    public DrawboardServer() {
        
        canvas = new Canvas(width, height);
        interpreter = new Interpreter(this, true);
        
        setPreferredSize(new Dimension(width, height));
        this.add(canvas);
    }
    
    // initialize client upon connection
    public void initClient(PrintWriter out, BufferedReader in, String username) throws IOException {       
        outs.add(out);
        ins.add(in);        
        userID++;
        out.println("USERID " + userID); // assigns ID to client
        out.println(getStartingImage()); // sends current canvas to client
        
        String userText = "USERS";
        for (int key : users.keySet()) {
            userText += String.format(" %d %s", key, users.get(key));
        }
        out.println(userText); // tells client who is online currently
        addUser(userID, username); // adds client to list of users
        
    }
    
    public Canvas getCanvas() {
        return canvas;
    }
    
    public void addUser(int id, String username) {
        users.put(id, username);
        sendToAll(String.format("NEWUSER %d %s", id, username));
        // tells all clients that there is a new user
    }
    
    public void removeUser(int id, String username) {
        users.remove(id);
        sendToAll(String.format("REMOVE %d %s", id, username));
        // tells all clients that a user has left
    }
    
    public String getStartingImage() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(canvas.getDrawingBuffer(), "png", baos);
        baos.flush();
        byte[] imageInByte = baos.toByteArray();
        baos.close();
        return DatatypeConverter.printBase64Binary(imageInByte);
        // sends drawing buffer to client
    }
    
    public void processRequest(String request) throws IOException {
        String reply = interpreter.interpret(request);
        sendToAll(reply);
        // interpret drawing requests, interpreter approves, and sends to all clients
    }
    
    public void sendToAll(String message) {
        for (PrintWriter out : outs) {
            out.println(message);
        } // sends message to everyone
    }
    
}