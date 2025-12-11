package mygame;

import java.util.HashMap;
import java.util.Map;

public class WeaponConfigLoader {
    // Editable muzzle offset configuration (forward, lateral)
    private static final Map<String, double[]> muzzleOffsets = new HashMap<>();

    static {
        // Default muzzle offsets - edit these values to adjust where bullets spawn
        muzzleOffsets.put("Pistol", new double[]{10.0, 22});
        muzzleOffsets.put("Assault Rifle", new double[]{38.0, 11});
        muzzleOffsets.put("Shotgun", new double[]{38.0, 11});
    }

    public static double getForwardOffset(String weaponName) {
        double[] offsets = muzzleOffsets.get(weaponName);
        if (offsets != null) {
            return offsets[0];
        }
        return 38.0; // Default
    }

    public static double getLateralOffset(String weaponName) {
        double[] offsets = muzzleOffsets.get(weaponName);
        if (offsets != null) {
            return offsets[1];
        }
        return 0.0; // Default
    }

    // Setter methods to adjust offsets at runtime if needed
    public static void setForwardOffset(String weaponName, double offset) {
        muzzleOffsets.computeIfAbsent(weaponName, k -> new double[2])[0] = offset;
    }

    public static void setLateralOffset(String weaponName, double offset) {
        muzzleOffsets.computeIfAbsent(weaponName, k -> new double[2])[1] = offset;
    }
}
