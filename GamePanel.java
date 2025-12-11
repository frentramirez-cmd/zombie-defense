package mygame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Iterator;

public class GamePanel extends JPanel implements ActionListener, MouseListener, MouseMotionListener, KeyListener {
    private Timer timer;
    private Player player;
    private ArrayList<Zombie> zombies;
    private ArrayList<Bullet> bullets;
    private ArrayList<MuzzleFlash> muzzleFlashes;
    private ArrayList<TextEffect> textEffects;
    private ArrayList<ReloadDrop> reloadDrops;
    private ArrayList<BloodParticle> bloodParticles;
    private boolean isRunning, isPaused;
    private boolean mouseHeld = false;
    private int mouseX, mouseY;
    private int wave = 1, spawnCount = 5;
    private int score = 0;
    private Weapon pistol, rifle, shotgun;
    private Weapon currentWeapon;
    private boolean playerDeathSplatterCreated = false; // Track if death splatter was created

    // Screen dimensions
    private static final int SCREEN_WIDTH = 1200;
    private static final int SCREEN_HEIGHT = 800;

    // Background image
    private Image backgroundImage;

    // Menu state
    private Menu menu;
    private boolean showMenu = true;

    public GamePanel() {
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setBackground(Color.BLACK);
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        setFocusable(true);

        // Load background image
        backgroundImage = ImageLoader.loadImage("map_background.png");

        // Set custom crosshair cursor
        setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
            createCrosshairImage(), new Point(15, 15), "crosshair"));

        // Initialize weapons
        pistol = new Weapon("Pistol", 12, 10, 450, 70); // slow, strong
        rifle = new Weapon("Assault Rifle", 40, 5, 130, 55); // fast, weaker // 130 // 55
        shotgun = new Shotgun();

        // Load muzzle offsets from config file (if present)
        pistol.setMuzzleOffset(WeaponConfigLoader.getForwardOffset("Pistol"));
        pistol.setMuzzleRightOffset(WeaponConfigLoader.getLateralOffset("Pistol"));
        rifle.setMuzzleOffset(WeaponConfigLoader.getForwardOffset("Assault Rifle"));
        rifle.setMuzzleRightOffset(WeaponConfigLoader.getLateralOffset("Assault Rifle"));
        shotgun.setMuzzleOffset(WeaponConfigLoader.getForwardOffset("Shotgun"));
        shotgun.setMuzzleRightOffset(WeaponConfigLoader.getLateralOffset("Shotgun"));
        currentWeapon = pistol;

        player = new Player(600, 400); // Center of 1200x800 screen
        zombies = new ArrayList<>();
        bullets = new ArrayList<>();
        muzzleFlashes = new ArrayList<>();
        textEffects = new ArrayList<>();
        reloadDrops = new ArrayList<>();
        bloodParticles = new ArrayList<>();
        menu = new Menu();
        timer = new Timer(16, this);

        // Validate speed configuration (no-op if not needed)
        SpeedConfig.isSpeedBalanced();
    }

    public void startGame() {
        isRunning = true;
        if (!timer.isRunning()) {
            timer.start();
        }
        spawnZombies();
    }

    public void restartGame() {
        if (!isRunning) {
            player = new Player(600, 400);
            zombies.clear();
            bullets.clear();
            textEffects.clear();
            bloodParticles.clear();
            wave = 1;
            spawnCount = 5;
            score = 0;
            currentWeapon = pistol;
            pistol.reset();
            rifle.reset();
            shotgun.reset();
            isRunning = true;
            isPaused = false;
            playerDeathSplatterCreated = false; // Reset death splatter flag
            spawnZombies();
        }
    }

    public void pauseGame() {
        if (isRunning) {
            isPaused = !isPaused;
        }
    }

    private void spawnZombies() {
        for (int i = 0; i < spawnCount; i++) {
            int x, y;
            // Spawn outside the window (1200x800) with 100px buffer
            if (Math.random() < 0.5) {
                x = (int) (Math.random() * SCREEN_WIDTH);
                y = Math.random() < 0.5 ? -100 : SCREEN_HEIGHT + 100;
            } else {
                x = Math.random() < 0.5 ? -100 : SCREEN_WIDTH + 100;
                y = (int) (Math.random() * SCREEN_HEIGHT);
            }
            zombies.add(new Zombie(x, y, wave));
        }
    }

    private void updateGame() {
        if (isRunning && !isPaused) {
            // Update weapons
            pistol.update();
            rifle.update();
            shotgun.update();

            // Update and remove expired reload drops
            Iterator<ReloadDrop> dropIt = reloadDrops.iterator();
            while (dropIt.hasNext()) {
                ReloadDrop drop = dropIt.next();
                if (drop.isExpired()) {
                    dropIt.remove();
                } else if (drop.collidesWith(player)) {
                    currentWeapon.addReloads(1);
                    textEffects.add(new TextEffect((int) drop.getX(), (int) drop.getY() - 20, "+1 AMMO", Color.GREEN));
                    dropIt.remove();
                }
            }

            // Update and remove finished text effects
            Iterator<TextEffect> textIt = textEffects.iterator();
            while (textIt.hasNext()) {
                TextEffect effect = textIt.next();
                effect.update();
                if (effect.isDone()) {
                    textIt.remove();
                }
            }

            // Update and remove expired blood particles
            Iterator<BloodParticle> bloodIt = bloodParticles.iterator();
            while (bloodIt.hasNext()) {
                BloodParticle particle = bloodIt.next();
                particle.update();
                if (particle.isDone()) {
                    bloodIt.remove();
                }
            }

            // Update and remove expired muzzle flashes
            Iterator<MuzzleFlash> flashIt = muzzleFlashes.iterator();
            while (flashIt.hasNext()) {
                MuzzleFlash f = flashIt.next();
                if (f.isExpired()) {
                    flashIt.remove();
                }
            }

            // Player and bullet updates
            player.update();
            player.checkZombieCollisions(zombies, bloodParticles);

            if (mouseHeld && currentWeapon.canShoot()) {
                currentWeapon.shoot();

                double angle = Math.atan2(mouseY - player.getY(), mouseX - player.getX());
                double muzzleDist = currentWeapon.getMuzzleOffset();
                double muzzleRightDist = currentWeapon.getMuzzleRightOffset();

                double muzzleX = player.getX() + Math.cos(angle) * muzzleDist + Math.cos(angle + Math.PI / 2) * muzzleRightDist;
                double muzzleY = player.getY() + Math.sin(angle) * muzzleDist + Math.sin(angle + Math.PI / 2) * muzzleRightDist;

                if (currentWeapon.getName().equals("Shotgun")) {
                    int pellets = 5;
                    double cone = Math.toRadians(40);
                    for (int i = 0; i < pellets; i++) {
                        double offset = (i - (pellets - 1) / 2.0) * (cone / (pellets - 1));
                        bullets.add(new Bullet(muzzleX, muzzleY, angle + offset, 150));
                    }
                    muzzleFlashes.add(new MuzzleFlash(muzzleX, muzzleY, angle, currentWeapon.getName()));
                } else {
                    bullets.add(new Bullet(muzzleX, muzzleY, angle));
                    muzzleFlashes.add(new MuzzleFlash(muzzleX, muzzleY, angle, currentWeapon.getName()));
                }
            }

            Iterator<Bullet> bulletIt = bullets.iterator();
            while (bulletIt.hasNext()) {
                Bullet b = bulletIt.next();
                b.update();
                if (b.isOffScreen()) {
                    bulletIt.remove();
                    continue;
                }
                Iterator<Zombie> zIt = zombies.iterator();
                while (zIt.hasNext()) {
                    Zombie z = zIt.next();
                    if (b.collidesWith(z)) {
                        double impactAngle = Math.atan2(z.getY() - b.getY(), z.getX() - b.getX());
                        z.applyStagger(impactAngle, currentWeapon.getName());

                        for (int i = 0; i < 5; i++) {
                            double a = Math.random() * Math.PI * 2;
                            double speed = 2 + Math.random() * 3;
                            double vx = Math.cos(a) * speed;
                            double vy = Math.sin(a) * speed;
                            bloodParticles.add(new BloodParticle(b.getX(), b.getY(), vx, vy, 30));
                        }

                        int damage = currentWeapon.getDamage();
                        if (currentWeapon.getName().equals("Shotgun") && b.getDistanceTraveled() < 100) {
                            damage = (int) (damage * 2);
                        }

                        z.takeDamage(damage);
                        bulletIt.remove();
                        if (z.getHealth() <= 0) {
                            for (int i = 0; i < 15; i++) {
                                double a = Math.random() * Math.PI * 2;
                                double speed = 3 + Math.random() * 4;
                                double vx = Math.cos(a) * speed;
                                double vy = Math.sin(a) * speed;
                                bloodParticles.add(new BloodParticle(z.getX(), z.getY(), vx, vy, 50));
                            }
                            if (Math.random() < 0.35) {
                                reloadDrops.add(new ReloadDrop(z.getX(), z.getY()));
                                textEffects.add(new TextEffect(z.getX(), z.getY() - 20, "AMMO!", Color.YELLOW));
                            }
                            zIt.remove();
                            score += 10;
                        }
                        break;
                    }
                }
            }

            // Zombie updates
            Iterator<Zombie> zombieIt = zombies.iterator();
            while (zombieIt.hasNext()) {
                Zombie z = zombieIt.next();
                z.moveTowards(player.getX(), player.getY(), zombies, player);
            }

            // Check wave completion
            if (zombies.isEmpty()) {
                wave++;
                spawnCount += 5;
                spawnZombies();
            }

            // Check game over
            if (player.getHealth() <= 0) {
                if (!playerDeathSplatterCreated) {
                    for (int i = 0; i < 20; i++) {
                        double a = Math.random() * Math.PI * 2;
                        double speed = 3 + Math.random() * 5;
                        double vx = Math.cos(a) * speed;
                        double vy = Math.sin(a) * speed;
                        bloodParticles.add(new BloodParticle(player.getX(), player.getY(), vx, vy, 60));
                    }
                    playerDeathSplatterCreated = true;
                }
                isRunning = false;
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!showMenu) {
            updateGame();
        }
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (showMenu) {
            menu.draw(g);
        } else {
            // Draw background image
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, this);
            }

            // Draw game
            for (Bullet b : bullets) {
                b.draw(g);
            }
            // Draw reload drops
            for (ReloadDrop drop : reloadDrops) {
                drop.draw(g);
            }
            player.draw(g, mouseX, mouseY, currentWeapon);
            // Draw active muzzle flashes
            for (MuzzleFlash f : muzzleFlashes) {
                f.draw(g);
            }
            // Draw text effects
            for (TextEffect effect : textEffects) {
                effect.draw(g);
            }
            for (Zombie z : zombies) {
                z.draw(g, player);
            }

            // Draw blood particles
            for (BloodParticle particle : bloodParticles) {
                particle.draw(g);
            }

            // Draw HUD
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            g.drawString("Wave: " + wave, 20, 60);
            g.drawString("Score: " + score, 20, 100);

            // Draw player health bar at top right
            int barWidth = 250;
            int barHeight = 40;
            int barX = SCREEN_WIDTH - barWidth - 20;
            int barY = 20;

            // Health bar background
            g.setColor(Color.DARK_GRAY);
            g.fillRect(barX, barY, barWidth, barHeight);

            // Health bar foreground
            int healthPercent = (player.getHealth() * barWidth) / 100;
            if (healthPercent > 0) {
                if (healthPercent > barWidth * 0.5) {
                    g.setColor(Color.GREEN);
                } else if (healthPercent > barWidth * 0.25) {
                    g.setColor(Color.YELLOW);
                } else {
                    g.setColor(Color.RED);
                }
                g.fillRect(barX, barY, healthPercent, barHeight);
            }

            // Health bar border
            g.setColor(Color.WHITE);
            g.drawRect(barX, barY, barWidth, barHeight);

            // Health text
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            String healthText = "HP: " + player.getHealth() + "/150";
            FontMetrics fm = g.getFontMetrics();
            g.drawString(healthText, barX + (barWidth - fm.stringWidth(healthText)) / 2, barY + 27);

            // Draw weapon HUD below health bar
            int weaponHudY = barY + barHeight + 15;
            g.setColor(Color.CYAN);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            String weaponName = currentWeapon.getName();
            g.drawString("Weapon: " + weaponName, barX, weaponHudY);

            // Draw ammo count
            int ammoY = weaponHudY + 25;
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.BOLD, 16));
            String ammoText = "Ammo: " + currentWeapon.getAmmo() + "/" + currentWeapon.getReloadsLeft();
            g.drawString(ammoText, barX, ammoY);

            // Draw reloading status
            int reloadY = ammoY + 25;
            if (currentWeapon.isReloading()) {
                g.setColor(Color.RED);
                g.setFont(new Font("Arial", Font.BOLD, 16));
                g.drawString("RELOADING...", barX, reloadY);
            } else {
                g.setColor(Color.GREEN);
                g.setFont(new Font("Arial", Font.BOLD, 14));
                g.drawString("Ready", barX, reloadY);
            }

            // Game state overlays
            if (!isRunning) {
                FontMetrics gameOverMetrics;
                g.setColor(Color.RED);
                g.setFont(new Font("Arial", Font.BOLD, 60));
                String gameOverText = "GAME OVER";
                gameOverMetrics = g.getFontMetrics();
                int gameOverX = (SCREEN_WIDTH - gameOverMetrics.stringWidth(gameOverText)) / 2;
                g.drawString(gameOverText, gameOverX, 250);

                g.setFont(new Font("Arial", Font.BOLD, 32));
                String scoreText = "Final Score: " + score;
                gameOverMetrics = g.getFontMetrics();
                int scoreX = (SCREEN_WIDTH - gameOverMetrics.stringWidth(scoreText)) / 2;
                g.drawString(scoreText, scoreX, 330);

                String restartText = "Press R to Restart";
                gameOverMetrics = g.getFontMetrics();
                int restartX = (SCREEN_WIDTH - gameOverMetrics.stringWidth(restartText)) / 2;
                g.drawString(restartText, restartX, 400);
            } else if (isPaused) {
                menu.drawPauseOverlay(g);
            }
        }
    }

    // Mouse
    @Override
    public void mousePressed(MouseEvent e) {
        if (showMenu) {
            if (menu.isPlayButtonClicked(e.getX(), e.getY())) {
                showMenu = false;
                if (!isRunning) {
                    startGame();
                }
            }
        } else if (SwingUtilities.isLeftMouseButton(e)) {
            mouseHeld = true;
            mouseX = e.getX();
            mouseY = e.getY();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            mouseHeld = false;
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    // Unused MouseListener methods required by the interface
    @Override
    public void mouseClicked(MouseEvent e) { }

    @Override
    public void mouseEntered(MouseEvent e) { }

    @Override
    public void mouseExited(MouseEvent e) { }

    // Keyboard
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_ESCAPE) {
            showMenu = true;
            isRunning = false;
            isPaused = false;
            return;
        }

        if (key == KeyEvent.VK_W) player.setMovingUp(true);
        if (key == KeyEvent.VK_S) player.setMovingDown(true);
        if (key == KeyEvent.VK_A) player.setMovingLeft(true);
        if (key == KeyEvent.VK_D) player.setMovingRight(true);

        if (key == KeyEvent.VK_P) pauseGame();
        if (key == KeyEvent.VK_R) restartGame();
        if (key == KeyEvent.VK_1) currentWeapon = pistol;
        if (key == KeyEvent.VK_2) currentWeapon = rifle;
        if (key == KeyEvent.VK_3) currentWeapon = shotgun;
        if (key == KeyEvent.VK_SPACE) currentWeapon.reload();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_W) player.setMovingUp(false);
        if (key == KeyEvent.VK_S) player.setMovingDown(false);
        if (key == KeyEvent.VK_A) player.setMovingLeft(false);
        if (key == KeyEvent.VK_D) player.setMovingRight(false);
    }

    @Override
    public void keyTyped(KeyEvent e) { }

    // Create a custom crosshair cursor image
    private java.awt.Image createCrosshairImage() {
        int size = 32;
        java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(
            size, size, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Make background transparent
        g.setColor(new Color(0, 0, 0, 0));
        g.fillRect(0, 0, size, size);

        // Draw crosshair in green
        g.setColor(Color.green);
        g.setStroke(new BasicStroke(2));

        int center = size / 2;
        int length = 8;

        // Horizontal line
        g.drawLine(center - length, center, center + length, center);

        // Vertical line
        g.drawLine(center, center - length, center, center + length);

        // Center circle
        g.setStroke(new BasicStroke(1));
        g.drawOval(center - 2, center - 2, 4, 4);

        g.dispose();
        return image;
    }
}