package model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.xml.bind.DatatypeConverter;

public class Canvas extends JPanel {

    private static final long serialVersionUID = 1L;
    private BufferedImage drawingBuffer;
    
    private final int width;
    private final int height;
    
    private int strokeWidth;
    private Color strokeColor;
    private Mode mode;
    private Palette palette;
    private PrintWriter out;

    // for server
    public Canvas(int width, int height) {
        this.width = width;
        this.height = height;
        setPreferredSize(new Dimension(this.width, this.height));
        makeDrawingBuffer();
    }
    
    // for client
    public Canvas(int width, int height, PrintWriter outRef) {
        this.width = width;
        this.height = height;
        setPreferredSize(new Dimension(this.width, this.height));        
        out = outRef;
        strokeWidth = 1;
        strokeColor = Color.BLACK;
        mode = Mode.DRAW;
        addPalette();
    }
    
    @Override
    public synchronized void paintComponent(Graphics g) {
        if (drawingBuffer == null) {
            makeDrawingBuffer();
        }
        
        g.drawImage(drawingBuffer, 0, 0, null);
    }
    
    private void makeDrawingBuffer() {
        drawingBuffer = new BufferedImage(this.getPreferredSize().width, 
                this.getPreferredSize().height, BufferedImage.TYPE_INT_RGB);
        
        fillWithWhite();
    }
    
    public synchronized BufferedImage getDrawingBuffer() {
        return drawingBuffer;
    }
    
    public synchronized void setDrawingBuffer(String data) throws IOException{
        byte[] binary = DatatypeConverter.parseBase64Binary(data);
        ByteArrayInputStream bais = new ByteArrayInputStream(binary);
        this.drawingBuffer = ImageIO.read(bais);
        repaint();
    }
    
    // for clients only
    private void addPalette() {
        palette = new Palette(this, out);
        addMouseListener(palette);
        addMouseMotionListener(palette);
    }
    
    // used by clients only
    public int getStrokeWidth() { return strokeWidth; }
    public void setStrokeWidth(int width) { strokeWidth = width; }
    public Color getStrokeColor() { return strokeColor; }
    public void setStrokeColor(Color color) { strokeColor = color; }
    public Mode getMode() { return mode; }
    public void setMode(Mode newMode) { mode = newMode; }
    
    // modifying drawing buffer
    public void fillWithWhite() {
        Graphics2D g2 = (Graphics2D) drawingBuffer.getGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, width, height);
        this.repaint();
    }
    
    public synchronized void draw(int x1, int y1, int x2, int y2, int width, Color color) {
        Graphics2D g2 = (Graphics2D) drawingBuffer.getGraphics();
        g2.setColor(color);
        g2.setStroke(new BasicStroke(width));
        g2.drawLine(x1, y1, x2, y2);       
        this.repaint();
    }
    
    public synchronized void erase(int x1, int y1, int x2, int y2, int width) {
        Graphics2D g2 = (Graphics2D) drawingBuffer.getGraphics();
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(width));
        g2.drawLine(x1, y1, x2, y2);
        this.repaint();
    }
}