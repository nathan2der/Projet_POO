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
            // Create the unit - it will be added to the player's list in the Unit constructor
            Unit unit = new Unit(type, this);
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
        System.out.println("Player.resetTurn called for " + name + " with " + units.size() + " units");
        
        // Use a copy to avoid potential concurrent modification issues
        List<Unit> unitsCopy = new ArrayList<>(units);
        
        // Reset all units
        for (Unit unit : unitsCopy) {
            try {
                System.out.println("Resetting unit " + unit.getType() + " (from Player.resetTurn)");
                
                // First try the standard reset method
                unit.resetTurn();
                
                // Verify reset was successful
                if (unit.getRemainingMovement() < unit.getType().getMovement() || 
                    unit.hasMoved() || unit.hasAttacked()) {
                    
                    System.out.println("WARNING: Unit not properly reset via resetTurn, trying resetMovementPoints");
                    unit.resetMovementPoints();
                    
                    // Final check - if it's still not reset, use direct method calls
                    if (unit.getRemainingMovement() < unit.getType().getMovement() || 
                        unit.hasMoved() || unit.hasAttacked()) {
                        
                        System.out.println("CRITICAL: Multiple reset methods failed, using direct setter methods");
                        unit.setRemainingMovement(unit.getType().getMovement());
                        unit.setHasMoved(false);
                        unit.setHasAttacked(false);
                    }
                }
                
                // Verify final state
                System.out.println("Final unit state: movement=" + unit.getRemainingMovement() + 
                                   "/" + unit.getType().getMovement() + 
                                   ", hasMoved=" + unit.hasMoved() + 
                                   ", hasAttacked=" + unit.hasAttacked());
            } catch (Exception e) {
                // Catch any exceptions during reset to prevent a single unit from breaking the turn reset
                System.err.println("ERROR: Exception during unit reset: " + e.getMessage());
                e.printStackTrace();
            }
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