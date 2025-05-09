package wargame.game;

import wargame.unit.UnitType;
import wargame.terrain.TerrainType;
import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages loading and caching of game assets (images).
 */
public class AssetManager {
    private static final Map<UnitType, Image> unitImages = new HashMap<>();
    private static final Map<TerrainType, Image> terrainImages = new HashMap<>();
    private static Image defaultUnitImage;
    private static Image defaultTerrainImage;

    static {
        try {
            // Load unit images
            unitImages.put(UnitType.INFANTRY, ImageIO.read(AssetManager.class.getResource("/assets/units/guerrier1.png")));
            unitImages.put(UnitType.HEAVY_INFANTRY, ImageIO.read(AssetManager.class.getResource("/assets/units/guerrier2.png")));
            unitImages.put(UnitType.CAVALRY, ImageIO.read(AssetManager.class.getResource("/assets/units/chevalier1.png")));
            unitImages.put(UnitType.ARCHER, ImageIO.read(AssetManager.class.getResource("/assets/units/archer1.png")));
            unitImages.put(UnitType.MAGE, ImageIO.read(AssetManager.class.getResource("/assets/units/mage1.png")));

            // Load terrain images
            terrainImages.put(TerrainType.PLAINS, ImageIO.read(AssetManager.class.getResource("/assets/terrain/grass01.png")));
            terrainImages.put(TerrainType.FOREST, ImageIO.read(AssetManager.class.getResource("/assets/terrain/foret3.png")));
            terrainImages.put(TerrainType.MOUNTAIN, ImageIO.read(AssetManager.class.getResource("/assets/terrain/motagne3.png")));
            terrainImages.put(TerrainType.WATER, ImageIO.read(AssetManager.class.getResource("/assets/terrain/riviere3.png")));
            terrainImages.put(TerrainType.ROAD, ImageIO.read(AssetManager.class.getResource("/assets/terrain/grass01.png")));

            // Load default images
            defaultUnitImage = ImageIO.read(AssetManager.class.getResource("/assets/units/ptInterrogation.png"));
            defaultTerrainImage = ImageIO.read(AssetManager.class.getResource("/assets/terrain/grass01.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the image for a unit type.
     * @param type The unit type
     * @return The unit's image
     */
    public static Image getUnitImage(UnitType type) {
        return unitImages.getOrDefault(type, defaultUnitImage);
    }

    /**
     * Gets the image for a terrain type.
     * @param type The terrain type
     * @return The terrain's image
     */
    public static Image getTerrainImage(TerrainType type) {
        return terrainImages.getOrDefault(type, defaultTerrainImage);
    }
} 