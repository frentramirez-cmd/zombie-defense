package mygame;

import java.awt.*;

public class TextEffect {
    private double x, y;
    private String text;
    private Color color;
    private long startTime;
    private final long duration = 5000; // How long the text stays visible (ms)
    private final double floatSpeed = 0.5; // How fast it floats upward

    public TextEffect(int x, int y, String text, Color color) {
        this.x = x;
        this.y = y;
        this.text = text;
        this.color = color;
        this.startTime = System.currentTimeMillis();
    }

    public void update() {
        y -= floatSpeed; // Float upward
    }

    public void draw(Graphics g) {
        long age = System.currentTimeMillis() - startTime;
        if (age >= duration) return;

        // Fade out over the duration
        float alpha = 1.0f - (age / (float)duration);
        if (alpha < 0) alpha = 0;
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        
        // Save the old composite
        Composite oldComp = g2d.getComposite();
        
        // Set alpha transparency
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2d.setColor(color);
        g2d.drawString(text, (int)x, (int)y);
        
        // Restore the old composite
        g2d.setComposite(oldComp);
    }

    public boolean isDone() {
        return System.currentTimeMillis() - startTime >= duration;
    }
}