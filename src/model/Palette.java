package model;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.PrintWriter;

public class Palette implements MouseListener, MouseMotionListener{

    private Point lastPoint;
    private Canvas canvas;
    private PrintWriter out;
    
    public Palette(Canvas canvas, PrintWriter out) {
        this.canvas = canvas;
        this.out = out;            
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        lastPoint = e.getPoint();
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        Point point = e.getPoint();
        
        switch(canvas.getMode()) {
        case DRAW: // draws line
            out.println("DRAW" + " " 
                    + lastPoint.x + " " + lastPoint.y + " " 
                    + point.x + " " + point.y + " "
                    + canvas.getStrokeWidth() + " "
                    + canvas.getStrokeColor().getRGB());
//            canvas.draw(lastPoint.x, lastPoint.y, point.x, point.y, 
//                    canvas.getStrokeWidth(), canvas.getStrokeColor());
            break;
        case ERASE: // erases
            out.println("ERASE" + " " 
                    + lastPoint.x + " " + lastPoint.y + " " 
                    + point.x + " " + point.y + " "
                    + canvas.getStrokeWidth());
//            canvas.erase(lastPoint.x, lastPoint.y, point.x, point.y, 
//                    canvas.getStrokeWidth());
            break;
        }
        lastPoint = point;
    }

    @Override public void mouseMoved(MouseEvent e) { }
    @Override public void mouseClicked(MouseEvent e) { }
    @Override public void mouseReleased(MouseEvent e) { }
    @Override public void mouseEntered(MouseEvent e) { }
    @Override public void mouseExited(MouseEvent e) { } 
}