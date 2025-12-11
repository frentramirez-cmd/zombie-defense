package mygame;

public class Shotgun extends Weapon {
    public Shotgun() {
        // name, mag size 2, reloads 20, fireRate ms, damage per pellet
        super("Shotgun", 2, 10, 300, 50);
    }

    @Override
    public boolean canShoot() {
        return !isReloading() && getAmmo() > 0 && System.currentTimeMillis() - lastShot >= fireRate;
    }

    @Override
    public void shoot() {
        if (isReloading()) return;
        if (getAmmo() > 0) {
            // decrease ammo using protected field - we don't have access here, so call parent shoot to handle ammo/time
            super.shoot();
        }
    }
}
