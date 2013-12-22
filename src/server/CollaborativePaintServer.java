package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class CollaborativePaintServer {
    
    private ServerSocket serverSocket;
    // list of all board servers
    private HashMap<Integer, DrawboardServer> boardServers = new HashMap<Integer, DrawboardServer>();
    
    public CollaborativePaintServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);       
    }
    
    public void serve() throws IOException {
        while (true) {
            final Socket socket = serverSocket.accept();
            
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // handles connection from one client
                        handleConnection(socket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            thread.start();
        }
    }
    
    public void handleConnection(Socket socket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        DrawboardServer master = null;

        for (String line = in.readLine(); line != null; line = in.readLine()) {
                        
            // initializing, allocated to correct server
            if (line.startsWith("BOARD")) {              
                String[] args = line.split(" ");
                int gameID = Integer.parseInt(args[1]);
                String username = args[3];
                
                if (boardServers.containsKey(gameID)) { // if game already exists
                    master = boardServers.get(gameID);
                    master.initClient(out, in, username);
                } else if (gameID > 0) { // game doesn't exist, but valid ID, creates new game
                    master = new DrawboardServer();
                    boardServers.put(gameID, master);
                    master.initClient(out, in, username);
                } else {
                    out.println("ERROR");
                }              
                
            // client closed window, closes connection
            } else if (line.startsWith("REMOVE")) {
                String[] args = line.split(" ");
                int id = Integer.parseInt(args[1]);
                String username = args[2];               
                master.removeUser(id, username);
                return;
            
            // not initializing nor ending, must be drawing requests
            } else {
                master.processRequest(line);
            }
        }       
    }
    
    public static void main(String[] args) {
        int port = 4444; // default port
        
        try {
            CollaborativePaintServer server = new CollaborativePaintServer(port);
            server.serve();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }    
}