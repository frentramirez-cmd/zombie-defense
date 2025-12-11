package mygame;

import java.awt.*;
import java.util.ArrayList;

public class Player {
    private int x, y, size = 100;
    private int health = 150;
    private final double speed = SpeedConfig.PLAYER_SPEED; // Adjustable player speed

    // New: Movement state flags
    private boolean movingUp = false;
    private boolean movingDown = false;
    private boolean movingLeft = false;
    private boolean movingRight = false;
    
    // Knockback effect
    private double knockbackX = 5;
    private double knockbackY = 5;
    private double knockbackDecay = 0.85; // How quickly knockback wears off
    
    // Image for player
    private java.awt.image.BufferedImage playerImage = null;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    // New: Setter methods for movement flags
    public void setMovingUp(boolean movingUp) {
        this.movingUp = movingUp;
    }

    public void setMovingDown(boolean movingDown) {
        this.movingDown = movingDown;
    }

    public void setMovingLeft(boolean movingLeft) {
        this.movingLeft = movingLeft;
    }

    public void setMovingRight(boolean movingRight) {
        this.movingRight = movingRight;
    }

    // Modified: update() method now handles player movement and collision with screen edges
    public void update() {
        if (movingUp) y -= (int) speed;
        if (movingDown) y += (int) speed;
        if (movingLeft) x -= (int) speed;
        if (movingRight) x += (int) speed;
        
        // Apply knockback effect
        x += (int) knockbackX;
        y += (int) knockbackY;
        knockbackX *= knockbackDecay;
        knockbackY *= knockbackDecay;

        // Boundary checks for window (1200x800)
        // x boundary
        if (x - size / 2 < 0) x = size / 2;
        if (x + size / 2 > 1200) x = 1200 - size / 2;
        // y boundary
        if (y - size / 2 < 0) y = size / 2;
        if (y + size / 2 > 800) y = 800 - size / 2;
    }
    
    // Check collision with zombies and push player back if colliding
    public void checkZombieCollisions(ArrayList<Zombie> zombies, ArrayList<BloodParticle> bloodParticles) {
        final double collisionRadius = size / 2 + 20; // Smaller collision radius
        for (Zombie z : zombies) {
            double dist = Math.hypot(z.getX() - x, z.getY() - y);
            if (dist < collisionRadius) {
                // Automatically damage player on collision
                z.attemptDamage(this, bloodParticles);
                
                // Push player away from zombie
                double angle = Math.atan2(y - z.getY(), x - z.getX());
                x = (int) (z.getX() + Math.cos(angle) * collisionRadius);
                y = (int) (z.getY() + Math.sin(angle) * collisionRadius);
                
                // Clamp to screen bounds after collision push
                if (x - size / 2 < 0) x = size / 2;
                if (x + size / 2 > 1200) x = 1200 - size / 2;
                if (y - size / 2 < 0) y = size / 2;
                if (y + size / 2 > 800) y = 800 - size / 2;
            }
        }
    }
    
    // Apply knockback force to the player (from zombie attacks)
    public void applyKnockback(double angle, double force) {
        knockbackX = Math.cos(angle) * force;
        knockbackY = Math.sin(angle) * force;
    }

    public void draw(Graphics g) {
        // Fallback to cyan circle if no weapon
        g.setColor(Color.CYAN);
        g.fillOval(x - size / 2, y - size / 2, size, size);
    }
    
    public void draw(Graphics g, int mouseX, int mouseY, Weapon weapon) {
        // Load weapon-based image
        String imageFile = getImageForWeapon(weapon);
        playerImage = ImageLoader.loadImage(imageFile);
        
        if (playerImage != null) {
            // Calculate rotation angle to face mouse
            double angle = Math.atan2(mouseY - y, mouseX - x);
            // Offset by -90 degrees since image faces north
            angle -= Math.PI / -2;
            
            Graphics2D g2d = (Graphics2D) g;
            
            // Save the current transform
            java.awt.geom.AffineTransform oldTransform = g2d.getTransform();
            
            // Translate to player position
            g2d.translate(x, y);
            // Rotate to face mouse
            g2d.rotate(angle);
            // Draw image centered at origin
            g2d.drawImage(playerImage, -size / 2, -size / 2, size, size, null);
            
            // Restore the transform
            g2d.setTransform(oldTransform);
        } else {
            // Fallback to cyan circle if image not found
            g.setColor(Color.CYAN);
            g.fillOval(x - size / 2, y - size / 2, size, size);
        }
    }
    
    // Get the appropriate image filename for the weapon
    private String getImageForWeapon(Weapon weapon) {
        String weaponName = weapon.getName();
        if (weaponName.contains("Assault Rifle")) {
            return "Ar1.png";
        } else if (weaponName.contains("Pistol")) {
            return "pistol.png";
        } else if (weaponName.contains("Shotgun")) {
            return "shotgun.png"; // use shotgun sprite
        }
        return "Ar1.png"; // Default fallback
    }

    public void takeDamage(int dmg) {
        health -= dmg;
        if (health < 0) health = 0;
    }
    
    public int getX() { return x; }
    public int getY() { return y; }
    public int getSize() { return size; }
    public int getHealth() { return health; }
}