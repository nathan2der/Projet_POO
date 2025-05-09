package wargame.unit;

/**
 * Defines different types of units in the game.
 * Each unit type has specific combat stats and movement capabilities.
 */
public enum UnitType {
    INFANTRY(12, 3, 4, 2, 1),     // Balanced unit
    HEAVY_INFANTRY(20, 2, 6, 4, 1), // Strong but slow
    CAVALRY(10, 4, 5, 1, 1),      // Fast and strong attack
    ARCHER(8, 2, 4, 1, 3),        // Ranged attack
    MAGE(8, 2, 5, 1, 3);          // Ranged attack with magic

    private final int health;
    private final int movement;
    private final int attack;
    private final int defense;
    private final int range;

    /**
     * Creates a new unit type with specified stats.
     * @param health Base health points
     * @param movement Movement points per turn
     * @param attack Base attack strength
     * @param defense Base defense strength
     * @param range Attack range in hexes
     */
    UnitType(int health, int movement, int attack, int defense, int range) {
        this.health = health;
        this.movement = movement;
        this.attack = attack;
        this.defense = defense;
        this.range = range;
    }

    /**
     * Gets the base health points for this unit type.
     * @return The health points
     */
    public int getHealth() {
        return health;
    }

    /**
     * Gets the movement points per turn for this unit type.
     * @return The movement points
     */
    public int getMovement() {
        return movement;
    }

    /**
     * Gets the base attack strength for this unit type.
     * @return The attack strength
     */
    public int getAttack() {
        return attack;
    }

    /**
     * Gets the base defense strength for this unit type.
     * @return The defense strength
     */
    public int getDefense() {
        return defense;
    }

    /**
     * Gets the attack range for this unit type.
     * @return The attack range in hexes
     */
    public int getRange() {
        return range;
    }
}