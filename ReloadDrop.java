package mygame;

import java.awt.*;

public class ReloadDrop {
    private double x, y;
    private final int size = 20;
    private final long spawnTime;
    private final long lifetime = 20000; // 20 seconds lifetime

    public ReloadDrop(double x, double y) {
        this.x = x;
        this.y = y;
        this.spawnTime = System.currentTimeMillis();
    }

    public void draw(Graphics g) {
        g.setColor(Color.YELLOW);
        // Draw ammo box
        g.fillRect((int)x - size/2, (int)y - size/2, size, size);
        g.setColor(Color.BLACK);
        g.drawRect((int)x - size/2, (int)y - size/2, size, size);
        // Draw cross on box
        g.drawLine((int)x - size/3, (int)y, (int)x + size/3, (int)y);
        g.drawLine((int)x, (int)y - size/3, (int)x, (int)y + size/3);
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - spawnTime >= lifetime;
    }

    public boolean collidesWith(Player p) {
        double dist = Math.hypot(p.getX() - x, p.getY() - y);
        return dist < (size + 20); // Generous collision radius for easy pickup
    }

    public double getX() { return x; }
    public double getY() { return y; }
}