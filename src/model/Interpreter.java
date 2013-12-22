package model;

import java.awt.Color;
import java.io.IOException;

public class Interpreter {
    
    private final Drawboard board;
    private boolean server;
    
    public Interpreter(Drawboard drawboard, boolean forServer) {
        this.board = drawboard;
        server = forServer;        
    }
    
    public String interpret(String request) throws IOException {
        String[] args = request.split(" ");
        
        if (args[0].equals("DRAW")) {
            int x1 = Integer.parseInt(args[1]);
            int y1 = Integer.parseInt(args[2]);
            int x2 = Integer.parseInt(args[3]);
            int y2 = Integer.parseInt(args[4]);
            int width = Integer.parseInt(args[5]);
            Color color = new Color(Integer.parseInt(args[6]));
            board.getCanvas().draw(x1, y1, x2, y2, width, color);
        }
        
        else if (args[0].equals("ERASE")) {
            int x1 = Integer.parseInt(args[1]);
            int y1 = Integer.parseInt(args[2]);
            int x2 = Integer.parseInt(args[3]);
            int y2 = Integer.parseInt(args[4]);
            int width = Integer.parseInt(args[5]);
            board.getCanvas().erase(x1, y1, x2, y2, width);  
        }
        
        else if (args[0].equals("USERS")) {
            if (!server) { // only clients should get this request
                int id;
                String name;
                for (int i = 1; i < args.length; i+=2) {
                    id = Integer.parseInt(args[i]);
                    name = args[i+1];
                    board.addUser(id, name);
                }
            }
        } 
        
        else if (args[0].equals("NEWUSER")) {
            if (!server) { // only clients should get this request
                int id = Integer.parseInt(args[1]);
                String name = args[2];
                board.addUser(id, name);
            }
        }
        
        else if (args[0].equals("REMOVE")) {
            if (!server) { // only clients should get this request
                int id = Integer.parseInt(args[1]);
                String name = args[2];
                board.removeUser(id, name);
            }
        }
        
        else {
            board.getCanvas().setDrawingBuffer(request);
        }
        
        return request;
        
    }
}