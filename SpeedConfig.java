package mygame;



public class SpeedConfig {
    // Player movement speed (pixels per frame)
    public static final double PLAYER_SPEED = 5.0;

    // Base zombie movement speed (pixels per frame) - multiplied by wave scaling
    public static final double ZOMBIE_BASE_SPEED = 0.5;
    public static final double ZOMBIE_WAVE_MULTIPLIER = 0.2; // Speed increase per wave

    public static double getZombieSpeed(int wave) {
        return ZOMBIE_BASE_SPEED + (wave * ZOMBIE_WAVE_MULTIPLIER);
    }

    /**
     * Validate that zombie is slower than player
     */
    public static boolean isSpeedBalanced() {
        // Check wave 1 (slowest zombies) is slower than player
        double wave1ZombieSpeed = getZombieSpeed(1);
        if (wave1ZombieSpeed >= PLAYER_SPEED) {
            System.err.println("Warning: Wave 1 zombie speed (" + wave1ZombieSpeed + ") >= player speed (" + PLAYER_SPEED + ")");
            return false;
        }
        return true;
    }
}
