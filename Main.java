package mygame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Top-Down Zombie Defense");
        GamePanel gamePanel = new GamePanel();

        frame.add(gamePanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800); // Smaller, standard window size
        frame.setLocationRelativeTo(null); // Center on screen
        frame.setVisible(true);
        gamePanel.startGame();
    }
}