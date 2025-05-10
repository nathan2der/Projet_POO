package wargame.unit;

import wargame.map.HexTile;
import wargame.player.Player;
import java.util.Random;

/**
 * Represents a unit in the game with its current state and capabilities.
 */
public class Unit {
    private final UnitType type;
    private final Player owner;
    private int currentHealth;
    private int remainingMovement;
    private HexTile currentTile;
    private boolean hasAttacked;
    private boolean hasMoved;
    private int turnsStationary;
    private static final Random random = new Random();
    private static final double CRIT_CHANCE = 0.10;
    private static final double MIN_CRIT_MULTIPLIER = 1.2;
    private static final double MAX_CRIT_MULTIPLIER = 1.5;
    private static final double HEAL_PERCENTAGE = 0.10;
    private static final double MAX_HEAL_PERCENTAGE = 0.50;

    /**
     * Creates a new unit of the specified type.
     * @param type The type of unit
     * @param owner The player who owns this unit
     */
    public Unit(UnitType type, Player owner) {
        this.type = type;
        this.owner = owner;
        this.currentHealth = type.getHealth();
        this.remainingMovement = type.getMovement();
        this.hasAttacked = false;
        this.hasMoved = false;
        this.turnsStationary = 0;
        
        // Automatically add this unit to the player's unit list
        owner.addUnit(this);
    }

    /**
     * Gets the type of this unit.
     * @return The unit type
     */
    public UnitType getType() {
        return type;
    }

    /**
     * Gets the owner of this unit.
     * @return The player who owns this unit
     */
    public Player getOwner() {
        return owner;
    }

    /**
     * Gets the current health of this unit.
     * @return The current health
     */
    public int getCurrentHealth() {
        return currentHealth;
    }

    /**
     * Gets the remaining movement points for this turn.
     * @return The remaining movement points
     */
    public int getRemainingMovement() {
        return remainingMovement;
    }

    /**
     * Gets the current tile this unit is on.
     * @return The current tile
     */
    public HexTile getCurrentTile() {
        return currentTile;
    }

    /**
     * Sets the current tile for this unit.
     * @param tile The new tile
     */
    public void setCurrentTile(HexTile tile) {
        this.currentTile = tile;
    }

    /**
     * Checks if this unit has attacked this turn.
     * @return true if the unit has attacked, false otherwise
     */
    public boolean hasAttacked() {
        return hasAttacked;
    }

    /**
     * Sets the attack status for this unit.
     * @param hasAttacked true if the unit has attacked, false otherwise
     */
    public void setHasAttacked(boolean hasAttacked) {
        this.hasAttacked = hasAttacked;
    }

    /**
     * Checks if this unit has moved this turn.
     * @return true if the unit has moved, false otherwise
     */
    public boolean hasMoved() {
        return hasMoved;
    }

    /**
     * Sets the move status for this unit.
     * @param hasMoved true if the unit has moved, false otherwise
     */
    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    /**
     * Sets the remaining movement points for this unit.
     * @param remainingMovement The new remaining movement points
     */
    public void setRemainingMovement(int remainingMovement) {
        this.remainingMovement = remainingMovement;
    }

    /**
     * Attacks another unit.
     * @param target The unit to attack
     * @return true if the attack was successful
     */
    public boolean attack(Unit target) {
        if (target == null || target.getOwner() == this.owner) {
            return false;
        }

        // Check if target is in range
        if (currentTile.distanceTo(target.getCurrentTile()) > type.getRange()) {
            return false;
        }

        // Calculate base damage
        int damage = calculateDamage(target);

        // Apply damage to target
        target.takeDamage(damage);
        
        // Set attacked flag
        hasAttacked = true;
        
        return true;
    }

    /**
     * Calculates damage to be dealt to a target.
     * @param target The target unit
     * @return The amount of damage
     */
    private int calculateDamage(Unit target) {
        int baseDamage = type.getAttack();
        
        // Apply critical hit
        if (random.nextDouble() < CRIT_CHANCE) {
            double critMultiplier = MIN_CRIT_MULTIPLIER + 
                random.nextDouble() * (MAX_CRIT_MULTIPLIER - MIN_CRIT_MULTIPLIER);
            baseDamage = (int) (baseDamage * critMultiplier);
        }

        // Apply terrain defense bonus
        int terrainDefenseBonus = target.getCurrentTile().getTerrainType().getDefenseBonus();
        double terrainMultiplier = 1.0 - (terrainDefenseBonus * 0.1); // Each point reduces damage by 10%
        baseDamage = (int)(baseDamage * terrainMultiplier);

        // Apply defense reduction
        baseDamage = Math.max(1, baseDamage - target.type.getDefense());

        return baseDamage;
    }

    /**
     * Takes damage from an attack.
     * @param damage The amount of damage to take
     */
    public void takeDamage(int damage) {
        int previousHealth = currentHealth;
        currentHealth = Math.max(0, currentHealth - damage);
        
        System.out.println(owner.getName() + "'s " + type + " took " + damage + 
                          " damage (health: " + previousHealth + " -> " + currentHealth + ")");
        
        if (currentHealth == 0) {
            System.out.println("UNIT DESTROYED: " + owner.getName() + "'s " + type);
            owner.removeUnit(this);
            
            // Debug output to show remaining units
            System.out.println("  " + owner.getName() + " now has " + owner.getUnits().size() + " units left");
        }
    }

    /**
     * Resets the unit's state for a new turn.
     */
    public final void resetTurn() {
        System.out.println("DEBUG: Unit.resetTurn() called for " + type + " owned by " + owner.getName());
        System.out.println("  Before reset: movement=" + remainingMovement + "/" + type.getMovement());
        
        // Reset all status flags first
        hasAttacked = false;
        hasMoved = false;
        
        // Explicitly set movement points to the max value from the unit type
        // This ensures we always get the correct max movement regardless of current state
        remainingMovement = type.getMovement();
        
        System.out.println("  After reset: movement=" + remainingMovement + "/" + type.getMovement() 
                          + ", hasMoved=" + hasMoved + ", hasAttacked=" + hasAttacked);
        
        // Handle healing for stationary units
        if (turnsStationary > 0) {
            turnsStationary++;
            if (turnsStationary >= 2) {  // Start healing after 2 turns stationary
                int maxHeal = (int) (type.getHealth() * MAX_HEAL_PERCENTAGE);
                int currentMaxHealth = type.getHealth() - maxHeal;
                if (currentHealth < currentMaxHealth) {
                    int healAmount = (int) (type.getHealth() * HEAL_PERCENTAGE);
                    currentHealth = Math.min(currentMaxHealth, currentHealth + healAmount);
                }
            }
        }
    }

    /**
     * Checks if the unit is still alive.
     * @return true if the unit has health remaining
     */
    public boolean isAlive() {
        return currentHealth > 0;
    }

    /**
     * Moves the unit to a new tile.
     * @param newTile The tile to move to
     * @param movementCost The cost to move to the new tile
     * @return true if the move was successful
     */
    public boolean move(HexTile newTile, int movementCost) {
        // Original implementation with movement point restrictions:
        /*
        if (movementCost > remainingMovement || !newTile.isEmpty()) {
            return false;
        }
        */
        
        // NEW IMPLEMENTATION: Allow movement regardless of movement points
        if (!newTile.isEmpty()) {
            return false;
        }
        
        if (currentTile != null) {
            currentTile.setUnit(null);
        }
        
        newTile.setUnit(this);
        currentTile = newTile;
        
        // Still deduct movement points to track that the unit moved
        // But cap at 0 to prevent negative movement
        remainingMovement = Math.max(0, remainingMovement - movementCost);
        hasMoved = true;
        turnsStationary = 0;  // Reset stationary turns when moving
        return true;
    }

    public int getHealth() {
        return currentHealth;
    }

    public int getMaxHealth() {
        return type.getHealth();
    }

    public int getMovementPoints() {
        return remainingMovement;
    }

    public HexTile getTile() {
        return currentTile;
    }

    public int getAttackRange() {
        return type.getRange();
    }

    public boolean move(HexTile destination) {
        // Original implementation with movement point restrictions:
        /*
        if (destination == null || destination.getUnit() != null) {
            return false;
        }

        int distance = currentTile.getMap().getDistance(currentTile, destination);
        if (distance > remainingMovement) {
            return false;
        }

        currentTile.setUnit(null);
        destination.setUnit(this);
        currentTile = destination;
        remainingMovement -= distance;
        hasMoved = true;
        turnsStationary = 0;  // Reset stationary turns when moving
        return true;
        */
        
        // NEW IMPLEMENTATION: Allow movement to any empty tile
        if (destination == null || destination.getUnit() != null) {
            return false;
        }

        int distance = currentTile.getMap().getDistance(currentTile, destination);
        
        currentTile.setUnit(null);
        destination.setUnit(this);
        currentTile = destination;
        
        // Still deduct movement points to track that the unit moved
        // But cap at 0 to prevent negative movement
        remainingMovement = Math.max(0, remainingMovement - 1);
        hasMoved = true;
        turnsStationary = 0;  // Reset stationary turns when moving
        
        System.out.println("Moved unit with unlimited movement (distance: " + distance + ")");
        return true;
    }

    /**
     * Explicitly resets movement points to their maximum value.
     * This method is an alternative to the full resetTurn and focuses only on movement.
     */
    public final void resetMovementPoints() {
        System.out.println("DEBUG: Unit.resetMovementPoints() explicit call for " + type);
        System.out.println("  Before reset: movement=" + remainingMovement + "/" + type.getMovement() 
                          + ", hasMoved=" + hasMoved + ", hasAttacked=" + hasAttacked);
        
        // Reset movement points
        remainingMovement = type.getMovement();
        
        // Also reset movement flags for consistency
        hasMoved = false;
        hasAttacked = false;
        
        System.out.println("  After reset: movement=" + remainingMovement + "/" + type.getMovement()
                          + ", hasMoved=" + hasMoved + ", hasAttacked=" + hasAttacked);
    }
} 