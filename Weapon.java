package mygame;

public class Weapon {
    private String name;
    protected int magazineSize, ammo, reloadsLeft;
    protected int damage;
    protected long fireRate, lastShot;
    private int initialReloads;
    // Muzzle offset (distance in pixels from player center where bullets originate)
    private double muzzleOffset = 0.0;
    // Lateral muzzle offset to the right of the facing direction (pixels)
    private double muzzleRightOffset = 11;
    // Automatic reload state
    
    private boolean reloading = false;
    private long reloadStartTime = 0;
    private long reloadDuration = 1500; // milliseconds to reload

    public Weapon(String name, int mag, int reloads, long fireRate, int damage) {
        this.name = name;
        this.magazineSize = mag;
        this.ammo = mag;
        this.reloadsLeft = reloads;
        this.initialReloads = reloads;
        this.fireRate = fireRate;
        this.damage = damage;
        this.lastShot = 0;
    }

    // Optional muzzle offset setter/getter so each weapon can define where bullets spawn
    public void setMuzzleOffset(double offset) { this.muzzleOffset = offset; }
    public double getMuzzleOffset() { return this.muzzleOffset; }
    public void setMuzzleRightOffset(double offset) { this.muzzleRightOffset = offset; }
    public double getMuzzleRightOffset() { return this.muzzleRightOffset; }

    public boolean canShoot() {
        // can't shoot while reloading
        return !reloading && ammo > 0 && System.currentTimeMillis() - lastShot >= fireRate;
    }

    public void shoot() {
        if (reloading) return;
        if (ammo > 0) {
            ammo--;
            lastShot = System.currentTimeMillis();
            // start automatic reload when magazine empty
            if (ammo == 0 && reloadsLeft > 0) {
                startReload();
            }
        }
    }

    // Initiate reload (manual or automatic)
    public void reload() {
        startReload();
    }

    private void startReload() {
        if (!reloading && reloadsLeft > 0) {
            reloading = true;
            reloadStartTime = System.currentTimeMillis();
        }
    }

    // Call this each tick from the game loop to process reload timing
    public void update() {
        if (reloading) {
            if (System.currentTimeMillis() - reloadStartTime >= reloadDuration) {
                ammo = magazineSize;
                reloadsLeft--;
                reloading = false;
            }
        }
    }

    public void reset() {
        ammo = magazineSize;
        reloadsLeft = initialReloads;
        reloading = false;
    }

    public int getDamage() { return damage; }
    public String getName() { return name; }
    public int getAmmo() { return ammo; }
    public int getReloadsLeft() { return reloadsLeft; }
    public boolean isReloading() { return reloading; }
    public long getReloadDuration() { return reloadDuration; }

    // Give the player additional reload(s) (e.g., dropped from zombie).
    public void addReloads(int count) {
        if (count <= 0) return;
        reloadsLeft += count;
    }
}

