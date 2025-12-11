package mygame;

import java.awt.*;
import java.util.ArrayList;

public class Zombie {
    private double x, y;
    private int size = 100;
    private double speed;
    private int maxHealth;
    private int health;
    private int damage;
    // Per-zombie damage cooldown so each zombie damages the player on its own timer
    private long lastDamageTime = 0;
    private final long damageCooldown = 1000; // milliseconds between this zombie's damaging ticks
    
    // Stagger effect variables
    private double staggerX = 0;
    private double staggerY = 0;
    private double staggerRecovery = 0.9; // How quickly stagger wears off (0-1)
    
    // Image for zombie
    private java.awt.image.BufferedImage zombieImage = null;

    public Zombie(int x, int y, int wave) {
        this.x = x;
        this.y = y;
        this.speed = SpeedConfig.getZombieSpeed(wave); // Use adjustable speed from config
        this.maxHealth = 40 + wave * 10;
        this.health = maxHealth;
        this.damage = 5 + wave;
    }

    public void moveTowards(int px, int py, ArrayList<Zombie> otherZombies, Player player) {
        // Update stagger effect
        staggerX *= staggerRecovery;
        staggerY *= staggerRecovery;

        // Calculate desired movement directly toward player
        double dx = px - x;
        double dy = py - y;
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist > 0) {
            double moveX = (dx / dist) * speed + staggerX;
            double moveY = (dy / dist) * speed + staggerY;

            double newX = x + moveX;
            double newY = y + moveY;

            // Prevent passing through the player: if movement would overlap the player,
            // place this zombie at the collision boundary so it doesn't get stuck in an
            // invisible circle while the player is stationary.
            // Use a slightly smaller contact distance to match player's collision checks
            double collisionSum = size / 2 + 20;
            double playerDist = Math.hypot(player.getX() - newX, player.getY() - newY);
            if (playerDist < collisionSum) {
                // Move zombie to be exactly at the collision boundary toward the player
                double dxp = player.getX() - x;
                double dyp = player.getY() - y;
                double distToPlayer = Math.hypot(dxp, dyp);
                if (distToPlayer > 0) {
                    double nx = dxp / distToPlayer;
                    double ny = dyp / distToPlayer;
                    x = (int) (player.getX() - nx * collisionSum);
                    y = (int) (player.getY() - ny * collisionSum);
                }
                return;
            }

            // Slide along other zombies if colliding
            for (Zombie other : otherZombies) {
                if (other != this) {
                    double otherDist = Math.hypot(other.x - newX, other.y - newY);
                    if (otherDist < size) {
                        double pushX = newX - other.x;
                        double pushY = newY - other.y;
                        double pushDist = Math.sqrt(pushX * pushX + pushY * pushY);
                        if (pushDist > 0) {
                            double slide = 0.5;
                            moveX += (pushX / pushDist) * speed * slide;
                            moveY += (pushY / pushDist) * speed * slide;
                        }
                    }
                }
            }

            x += moveX;
            y += moveY;
        }
    }
    
    // Add method to apply stagger effect
    public void applyStagger(double angle, String weaponType) {
        double staggerForce;
        if (weaponType.equals("Assault Rifle")) {
            staggerForce = 2.0; // Strong stagger for AR
        } else if (weaponType.equals("Pistol")) {
            staggerForce = 5.0; // Light stagger for pistol
        } else if (weaponType.equals("Shotgun")) {
            staggerForce = 7.0; // Very strong knockback for shotgun
        } else {
            return; 
        }
        
        // Apply stagger force in the direction of the hit
        staggerX += Math.cos(angle) * staggerForce;
        staggerY += Math.sin(angle) * staggerForce;
    }

    public void draw(Graphics g) {
        // Try to load and draw image
        if (zombieImage == null) {
            zombieImage = ImageLoader.loadImage("zombie.png");
        }
        
        if (zombieImage != null) {
            // Draw scaled image (no rotation yet, will be added in draw with player position)
            g.drawImage(zombieImage, (int) x - size / 2, (int) y - size / 2, size, size, null);
        } else {
            // Fallback to green circle if image not found
            g.setColor(Color.GREEN);
            g.fillOval((int) x - size / 2, (int) y - size / 2, size, size);
        }

        // Health bar
        g.setColor(Color.DARK_GRAY);
        g.fillRect((int) x - 15, (int) y - size / 2 - 10, 30, 5);
        g.setColor(Color.RED);
        int healthWidth = (int) (30 * ((double) health / maxHealth));
        g.fillRect((int) x - 15, (int) y - size / 2 - 10, healthWidth, 5);
    }
    
    public void draw(Graphics g, Player player) {
        // Try to load and draw image
        if (zombieImage == null) {
            zombieImage = ImageLoader.loadImage("zombie.png");
        }
        
        if (zombieImage != null) {
            // Calculate rotation angle to face player
            double angle = Math.atan2(player.getY() - y, player.getX() - x);
            // Offset by -90 degrees since image faces north
            angle -= Math.PI/-2;
            
            Graphics2D g2d = (Graphics2D) g;
            
            // Save the current transform
            java.awt.geom.AffineTransform oldTransform = g2d.getTransform();
            
            // Translate to zombie position
            g2d.translate(x, y);
            // Rotate to face player
            g2d.rotate(angle);
            // Draw image centered at origin
            g2d.drawImage(zombieImage, -size / 2, -size / 2, size, size, null);
            
            // Restore the transform
            g2d.setTransform(oldTransform);
        } else {
            // Fallback to green circle if image not found
            g.setColor(Color.GREEN);
            g.fillOval((int) x - size / 2, (int) y - size / 2, size, size);
        }

        // Health bar
        g.setColor(Color.DARK_GRAY);
        g.fillRect((int) x - 15, (int) y - size / 2 - 10, 30, 5);
        g.setColor(Color.RED);
        int healthWidth = (int) (30 * ((double) health / maxHealth));
        g.fillRect((int) x - 15, (int) y - size / 2 - 10, healthWidth, 5);
    }

    public boolean collidesWith(Player p) {
        double dist = Math.hypot(p.getX() - x, p.getY() - y);
        return dist < (size / 2 + 15);
    }

    /**
     * Attempt to damage the player. This enforces a cooldown per zombie so a single
     * zombie doesn't continuously damage the player every frame.
     * Returns true if damage was applied.
     */
    public boolean attemptDamage(Player p, java.util.ArrayList<BloodParticle> bloodParticles) {
        long now = System.currentTimeMillis();
        if (now - lastDamageTime >= damageCooldown) {
            p.takeDamage(damage);
            
            // Apply small knockback to player away from zombie
            double angle = Math.atan2(p.getY() - y, p.getX() - x);
            p.applyKnockback(angle, 4.0); // Small knockback force
            
            // Create damage impact particles at player position
            for (int i = 0; i < 8; i++) {
                double impactAngle = Math.random() * Math.PI * 2;
                double speed = 2 + Math.random() * 3;
                double vx = Math.cos(impactAngle) * speed;
                double vy = Math.sin(impactAngle) * speed;
                bloodParticles.add(new BloodParticle(p.getX(), p.getY(), vx, vy, 30));
            }
            
            lastDamageTime = now;
            return true;
        }
        return false;
    }

    public void takeDamage(int dmg) {
        health -= dmg;
        if (health < 0) health = 0;
    }

    public int getHealth() { return health; }
    public int getDamage() { return damage; }
    public int getX() { return (int) x; }
    public int getY() { return (int) y; }
    
    // Note: line-of-sight and group hunting logic were intentionally simplified.
    // Zombies always path toward the player's current position; pathfinding
    // handles obstacle navigation. Remove unused helpers to keep code clean.
}