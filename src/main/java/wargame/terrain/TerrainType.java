package wargame.terrain;

/**
 * Represents different types of terrain in the game.
 */
public enum TerrainType {
    PLAINS(1, 0),
    FOREST(2, 1),
    MOUNTAIN(3, 2),
    WATER(4, 0),
    ROAD(1, 0);

    private final int movementCost;
    private final int defenseBonus;

    TerrainType(int movementCost, int defenseBonus) {
        this.movementCost = movementCost;
        this.defenseBonus = defenseBonus;
    }

    public int getMovementCost() {
        return movementCost;
    }

    public int getDefenseBonus() {
        return defenseBonus;
    }
} 