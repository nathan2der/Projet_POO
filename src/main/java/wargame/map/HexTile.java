package wargame.map;

import wargame.unit.Unit;
import wargame.terrain.TerrainType;

/**
 * Represents a single hexagonal tile on the game map.
 * Each tile has a terrain type and can contain a unit.
 */
public class HexTile {
    private final int x;
    private final int y;
    private TerrainType terrainType;
    private Unit unit;
    private final GameMap map;

    /**
     * Creates a new hex tile at the specified coordinates.
     * @param x The x coordinate
     * @param y The y coordinate
     * @param terrainType The type of terrain
     * @param map The game map
     */
    public HexTile(int x, int y, TerrainType terrainType, GameMap map) {
        this.x = x;
        this.y = y;
        this.terrainType = terrainType;
        this.map = map;
        this.unit = null;
    }

    /**
     * Gets the x coordinate of this tile.
     * @return The x coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the y coordinate of this tile.
     * @return The y coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * Gets the terrain type of this tile.
     * @return The terrain type
     */
    public TerrainType getTerrainType() {
        return terrainType;
    }

    /**
     * Sets the terrain type of this tile.
     * @param terrainType The new terrain type
     */
    public void setTerrainType(TerrainType terrainType) {
        this.terrainType = terrainType;
    }

    /**
     * Gets the unit currently on this tile.
     * @return The unit, or null if the tile is empty
     */
    public Unit getUnit() {
        return unit;
    }

    /**
     * Sets the unit on this tile.
     * @param unit The unit to place, or null to remove the current unit
     */
    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    /**
     * Checks if this tile is empty (has no unit).
     * @return true if the tile is empty, false otherwise
     */
    public boolean isEmpty() {
        return unit == null;
    }

    /**
     * Calculates the distance to another hex tile.
     * @param other The other hex tile
     * @return The distance in hexes
     */
    public int distanceTo(HexTile other) {
        // Using axial coordinates for hex distance calculation
        int dx = Math.abs(x - other.x);
        int dy = Math.abs(y - other.y);
        return Math.max(dx, dy);
    }

    /**
     * Checks if this tile is valid for unit placement.
     * A tile is valid if it is empty and its terrain is not impassable.
     * @return true if a unit can be placed on this tile
     */
    public boolean isValidForUnitPlacement() {
        return isEmpty() && terrainType.getMovementCost() < 4; // Assuming movement cost >= 4 means impassable
    }

    /**
     * Gets the game map associated with this tile.
     * @return The game map
     */
    public GameMap getMap() {
        return map;
    }
}