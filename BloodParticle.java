package mygame;

import java.awt.*;

public class BloodParticle {
    private double x, y;
    private double vx, vy; // velocity
    private int lifetime; // frames remaining
    private final int maxLifetime;
    private static final int SIZE = 10;
    private static final double GRAVITY = 0.2;

    public BloodParticle(double x, double y, double vx, double vy, int lifetime) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.lifetime = lifetime;
        this.maxLifetime = lifetime;
    }

    public void update() {
        x += vx;
        y += vy;
        vy += GRAVITY; // Apply gravity
        lifetime--;
    }

    public void draw(Graphics g) {
        // Fade out as lifetime decreases
        float alpha = (float) lifetime / maxLifetime;
        
        // Calculate red color with fade
        int red = 200;
        int green = 0;
        int blue = 0;
        
        g.setColor(new Color(red, green, blue, (int) (alpha * 255)));
        g.fillOval((int) x - SIZE / 2, (int) y - SIZE / 2, SIZE, SIZE);
    }

    public boolean isDone() {
        return lifetime <= 0;
    }

    public double getX() { return x; }
    public double getY() { return y; }
}
