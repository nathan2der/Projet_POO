package wargame.unit;

import wargame.map.HexTile;
import java.util.List;
import java.util.ArrayList;

/**
 * Handles combat calculations and resolution between units.
 * Manages complex combat scenarios and modifiers.
 */
public class CombatResolver {
    private static final double SURROUNDED_BONUS = 0.10;
    private static final double ISOLATED_PENALTY = -0.10;
    private static final int MAX_ADJACENT_HEXES = 6;

    /**
     * Calculates the morale modifier for a unit based on surrounding units.
     * @param unit The unit to calculate morale for
     * @return The morale modifier
     */
    public static double calculateMoraleModifier(Unit unit) {
        List<HexTile> adjacentTiles = getAdjacentTiles(unit.getCurrentTile());
        int friendlyUnits = 0;
        int enemyUnits = 0;

        for (HexTile tile : adjacentTiles) {
            Unit adjacentUnit = tile.getUnit();
            if (adjacentUnit != null) {
                if (adjacentUnit.getOwner() == unit.getOwner()) {
                    friendlyUnits++;
                } else {
                    enemyUnits++;
                }
            }
        }

        // Calculate morale based on unit presence
        if (friendlyUnits >= 3) {
            return 1.0 + SURROUNDED_BONUS; // Surrounded by allies
        } else if (enemyUnits > friendlyUnits) {
            return 1.0 + ISOLATED_PENALTY; // Outnumbered by enemies
        }

        return 1.0; // Neutral morale
    }

    /**
     * Gets all adjacent tiles to a given tile.
     * @param tile The center tile
     * @return List of adjacent tiles
     */
    private static List<HexTile> getAdjacentTiles(HexTile tile) {
        List<HexTile> adjacentTiles = new ArrayList<>(MAX_ADJACENT_HEXES);
        // TODO: Implement hex grid adjacency calculation
        // This will need to be implemented once we have the GameMap class
        return adjacentTiles;
    }

    /**
     * Calculates the total damage modifier for a combat situation.
     * @param attacker The attacking unit
     * @param defender The defending unit
     * @param distance The distance between units
     * @return The total damage modifier
     */
    public static double calculateTotalDamageModifier(Unit attacker, Unit defender, int distance) {
        double modifier = 1.0;

        // Apply terrain modifier
        modifier *= (1.0 + (defender.getCurrentTile().getTerrainType().getDefenseBonus() / 100.0));

        // Apply range falloff for ranged units
        if (attacker.getType().getRange() > 1) {
            int optimalRange = attacker.getType().getRange() / 2;
            if (distance > optimalRange) {
                double falloff = 0.10 * (distance - optimalRange);
                modifier *= (1.0 - falloff);
            }
        }

        // Apply morale modifier
        modifier *= calculateMoraleModifier(defender);

        return modifier;
    }
} 