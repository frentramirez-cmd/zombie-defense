package mygame;

import java.awt.*;

public class Bullet {
    private double x, y, dx, dy;
    private final double speed = 25;
    private final int size = 8;
    private double originX, originY;
    private double maxDistance = -1; // -1 = unlimited

    public Bullet(double x, double y, double angle) {
        this(x, y, angle, -1);
    }

    public Bullet(double x, double y, double angle, double maxDistance) {
        this.x = x;
        this.y = y;
        this.originX = x;
        this.originY = y;
        this.maxDistance = maxDistance;
        dx = Math.cos(angle) * speed;
        dy = Math.sin(angle) * speed;
    }

    public void update() {
        x += dx;
        y += dy;
    }

    public boolean isOffScreen() {
        // Use window bounds (match GamePanel window settings: 1200x800)
        if (x < 0 || x > 1200 || y < 0 || y > 800) return true;
        if (maxDistance > 0) {
            double dist = Math.hypot(x - originX, y - originY);
            if (dist >= maxDistance) return true;
        }
        return false;
    }

    public void draw(Graphics g) {
        g.setColor(Color.YELLOW);
        g.fillOval((int) x - size / 2, (int) y - size / 2, size, size);
    }

    public boolean collidesWith(Zombie z) {
        double dist = Math.hypot(z.getX() - x, z.getY() - y);
        return dist < 25; // Bullet hits if within 25px of zombie center
    }
    
    public double getX() { return x; }
    public double getY() { return y; }
    
    // Calculate distance traveled from origin
    public double getDistanceTraveled() {
        return Math.hypot(x - originX, y - originY);
    }
}
