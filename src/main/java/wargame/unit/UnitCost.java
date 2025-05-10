package wargame.unit;

/**
 * Manages unit costs and placement rules.
 */
public class UnitCost {
    private static final int MAX_PLACES = 100;
    
    // Unit costs in places
    public static final int ARCHER_COST = 1;
    public static final int INFANTRY_COST = 2;
    public static final int CAVALRY_COST = 5;
    public static final int HEAVY_INFANTRY_COST = 7;
    public static final int MAGE_COST = 2;
    
    /**
     * Gets the cost in places for a unit type.
     * @param type The unit type
     * @return The cost in places
     */
    public static int getCost(UnitType type) {
        return switch (type) {
            case ARCHER -> ARCHER_COST;
            case INFANTRY -> INFANTRY_COST;
            case CAVALRY -> CAVALRY_COST;
            case HEAVY_INFANTRY -> HEAVY_INFANTRY_COST;
            case MAGE -> MAGE_COST;
        };
    }
    
    /**
     * Gets the maximum number of places available for each player.
     * @return The maximum number of places
     */
    public static int getMaxPlaces() {
        return MAX_PLACES;
    }
} 