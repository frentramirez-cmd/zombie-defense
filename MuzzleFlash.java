package mygame;

import java.awt.*;

public class MuzzleFlash {
    private double x, y;
    private double angle;
    private String weaponName;
    private long startTime;
    private int duration = 120; // ms
    private int size = 24;

    public MuzzleFlash(double x, double y, double angle, String weaponName) {
        this.x = x;
        this.y = y;
        this.angle = angle;
        this.weaponName = weaponName;
        this.startTime = System.currentTimeMillis();

        // Tune size by weapon
        if (weaponName.equals("Shotgun")) {
            this.size = 44;
            this.duration = 160;
        } else if (weaponName.contains("Assault") || weaponName.contains("Rifle")) {
            this.size = 28;
            this.duration = 100;
        } else { // pistol or default
            this.size = 20;
            this.duration = 90;
        }
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - startTime >= duration;
    }

    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        long elapsed = System.currentTimeMillis() - startTime;
        float alpha = 1.0f - Math.min(1.0f, (float) elapsed / (float) duration);

        // Choose color for flash
        Color flashColor = new Color(255, 200, 50, 255);
        if (weaponName.equals("Shotgun")) flashColor = new Color(255, 170, 60, 255);
        if (weaponName.contains("Assault") || weaponName.contains("Rifle")) flashColor = new Color(255, 220, 90, 255);

        Composite oldComp = g2.getComposite();
        Color oldColor = g2.getColor();
        java.awt.geom.AffineTransform oldTransform = g2.getTransform();

        g2.translate(x, y);
        g2.rotate(angle);

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0f, alpha)));
        g2.setColor(flashColor);

        // Draw a triangular flash pointing along +x axis
        int w = size;
        int h = size / 2;
        int[] xs = {0, w, 0};
        int[] ys = { -h, 0, h };
        g2.fillPolygon(xs, ys, 3);

        // Add a small inner highlight
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0f, alpha * 0.6f)));
        g2.setColor(Color.WHITE);
        int iw = Math.max(4, w / 4);
        int ih = Math.max(2, h / 3);
        int[] ixs = {2, iw + 2, 2};
        int[] iys = { -ih, 0, ih };
        g2.fillPolygon(ixs, iys, 3);

        // restore
        g2.setTransform(oldTransform);
        g2.setComposite(oldComp);
        g2.setColor(oldColor);
    }
}
