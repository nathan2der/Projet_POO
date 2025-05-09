package wargame.player;

import wargame.unit.Unit;
import wargame.unit.UnitType;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a player in the game.
 * Manages player's units and resources.
 */
public class Player {
    private final String name;
    private final List<Unit> units;
    private int gold;
    private static final int STARTING_GOLD = 1000;

    /**
     * Creates a new player with the specified name.
     * @param name The player's name
     */
    public Player(String name) {
        this.name = name;
        this.units = new ArrayList<>();
        this.gold = STARTING_GOLD;
    }

    /**
     * Gets the player's name.
     * @return The player's name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the player's current gold amount.
     * @return The amount of gold
     */
    public int getGold() {
        return gold;
    }

    /**
     * Adds gold to the player's treasury.
     * @param amount The amount to add
     */
    public void addGold(int amount) {
        this.gold += amount;
    }

    /**
     * Gets all units owned by this player.
     * @return List of units
     */
    public List<Unit> getUnits() {
        return new ArrayList<>(units);
    }

    /**
     * Adds a unit to the player's army.
     * @param unit The unit to add
     */
    public void addUnit(Unit unit) {
        units.add(unit);
    }

    /**
     * Removes a unit from the player's army.
     * @param unit The unit to remove
     */
    public void removeUnit(Unit unit) {
        units.remove(unit);
    }

    /**
     * Creates a new unit of the specified type.
     * @param type The type of unit to create
     * @return The created unit, or null if the player can't afford it
     */
    public Unit createUnit(UnitType type) {
        int cost = calculateUnitCost(type);
        if (gold >= cost) {
            gold -= cost;
            Unit unit = new Unit(type, this);
            units.add(unit);
            return unit;
        }
        return null;
    }

    /**
     * Calculates the cost of a unit type.
     * @param type The unit type
     * @return The cost in gold
     */
    private int calculateUnitCost(UnitType type) {
        // Base cost calculation based on unit stats
        return type.getHealth() * 10 + 
               type.getMovement() * 20 + 
               type.getAttack() * 30 + 
               type.getRange() * 40;
    }

    /**
     * Resets all units for a new turn.
     */
    public void resetTurn() {
        for (Unit unit : units) {
            unit.resetTurn();
        }
    }

    /**
     * Checks if the player has any units left.
     * @return true if the player has units, false otherwise
     */
    public boolean hasUnits() {
        return !units.isEmpty();
    }
} 