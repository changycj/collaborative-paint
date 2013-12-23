package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.SwingUtilities;

public class CollaborativePaintClient {
    
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    
    public CollaborativePaintClient(InetAddress address, int port) throws IOException {
        socket = new Socket(address, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
        
    }
    
    public void connect() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Login(out, in); // opens login window upon connection           
            }
        });
    }
    
    public static void main(String[] args) {
        int port = 4444;
                
        try {
            InetAddress address = InetAddress.getByName("localhost");
            CollaborativePaintClient client = new CollaborativePaintClient(address, port);
            client.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }  
    }    
}