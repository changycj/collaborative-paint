package model;

// implemented by DrawboardServer and ClientDrawboard

public interface Drawboard {
    
    public Canvas getCanvas();
    public void addUser(int id, String username);
    public void removeUser(int id, String username);
    
}