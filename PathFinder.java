package mygame;

public class PathFinder {

    public static double[] getNextDirection(Zombie zombie, int playerX, int playerY) {
        double dx = playerX - zombie.getX();
        double dy = playerY - zombie.getY();
        double dist = Math.hypot(dx, dy);
        
        if (dist > 0) {
            return new double[]{dx / dist, dy / dist};
        }
        return new double[]{0.0, 0.0};
    }
}