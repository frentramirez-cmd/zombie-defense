package mygame;

import java.awt.*;
import java.awt.AlphaComposite;

public class Menu {
    private int width = 1200;
    private int height = 800;
    
    // Play button dimensions
    private int buttonX = 500;
    private int buttonY = 650;
    private int buttonWidth = 200;
    private int buttonHeight = 60;

    public void draw(Graphics g) {
        // Draw background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);

        // Draw title
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 60));
        g.drawString("ZOMBIE DEFENSE SURVIVAL", 200, 100);

        // Draw controls section
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        
        int startY = 180;
        int lineHeight = 35;
        
        g.drawString("CONTROLS:", 100, startY);
        g.drawString("WASD - Move", 100, startY + lineHeight);
        g.drawString("Mouse Click - Attack/Shoot", 100, startY + lineHeight * 2);
        g.drawString("1 - Pistol ", 100, startY + lineHeight * 3);
        g.drawString("2 - Assault Rifle ", 100, startY + lineHeight * 4);
        g.drawString("3 - Shotgun ", 100, startY + lineHeight * 5);
        g.drawString("Space - Reload ", 100, startY + lineHeight * 6);
        g.drawString("P - Pause", 100, startY + lineHeight * 7);
        g.drawString("R - Restart (end of game)", 100, startY + lineHeight * 8);

        // Draw play button
        g.setColor(Color.GREEN);
        g.fillRect(buttonX, buttonY, buttonWidth, buttonHeight);
        
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        FontMetrics fm = g.getFontMetrics();
        String buttonText = "PLAY";
        int textX = buttonX + (buttonWidth - fm.stringWidth(buttonText)) / 2;
        int textY = buttonY + ((buttonHeight - fm.getAscent()) / 2) + fm.getAscent();
        g.drawString(buttonText, textX, textY);
    }

    public boolean isPlayButtonClicked(int mouseX, int mouseY) {
        return mouseX >= buttonX && mouseX <= buttonX + buttonWidth &&
               mouseY >= buttonY && mouseY <= buttonY + buttonHeight;
    }

    public void drawPauseOverlay(Graphics g) {
        // Draw semi-transparent overlay
        Graphics2D g2d = (Graphics2D) g;
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

        // Draw title
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.BOLD, 60));
        g.drawString("PAUSED", 350, 100);

        // Draw controls section
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        
        int startY = 180;
        int lineHeight = 35;
        
        g.drawString("CONTROLS:", 100, startY);
        g.drawString("WASD - Move", 100, startY + lineHeight);
        g.drawString("Mouse Click - Attack/Shoot", 100, startY + lineHeight * 2);
        g.drawString("1 - Pistol ", 100, startY + lineHeight * 3);
        g.drawString("2 - Assault Rifle ", 100, startY + lineHeight * 4);
        g.drawString("3 - Shotgun ", 100, startY + lineHeight * 5);
        g.drawString("Space - Reload", 100, startY + lineHeight * 6);
        g.drawString("P - Resume/Pause", 100, startY + lineHeight * 7);
        g.drawString("R - Restart (end of game)", 100, startY + lineHeight * 8);
        g.drawString("ESC - Main Menu", 100, startY + lineHeight * 9);
    }
}
