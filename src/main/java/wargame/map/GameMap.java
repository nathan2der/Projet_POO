package wargame.map;

import wargame.unit.Unit;
import wargame.terrain.TerrainType;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the game map and its grid of hex tiles.
 * Manages the hexagonal grid and unit placement.
 */
public class GameMap {
    private final HexTile[][] tiles; // 2D array to store hex tiles
    private final int width;
    private final int height;

    /**
     * Creates a new game map with the specified dimensions.
     * @param width The width of the map
     * @param height The height of the map
     */
    public GameMap(int width, int height) {
        this.width = width;
        this.height = height;
        this.tiles = new HexTile[width][height];
        initializeMap();
    }

    /**
     * Initializes the map with default terrain.
     */
    private void initializeMap() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // Default to plains, can be modified later
                tiles[x][y] = new HexTile(x, y, TerrainType.PLAINS, this);
            }
        }
    }

    /**
     * Gets the width of the map.
     * @return The width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Gets the height of the map.
     * @return The height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Gets a tile at the specified coordinates.
     * @param x The x coordinate
     * @param y The y coordinate
     * @return The tile at the coordinates, or null if out of bounds
     */
    public HexTile getTile(int x, int y) {
        if (isValidCoordinate(x, y)) {
            return tiles[x][y];
        }
        return null;
    }

    /**
     * Checks if the given coordinates are valid.
     * @param x The x coordinate
     * @param y The y coordinate
     * @return true if the coordinates are valid, false otherwise
     */
    public boolean isValidCoordinate(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    /**
     * Gets all adjacent tiles to a given tile.
     * @param tile The center tile
     * @return List of adjacent tiles
     */
    public List<HexTile> getAdjacentTiles(HexTile tile) {
        List<HexTile> adjacentTiles = new ArrayList<>(6);
        int x = tile.getX();
        int y = tile.getY();

        // Define the six directions in a hex grid
        int[][] directions = {
            {1, 0}, {1, -1}, {0, -1},
            {-1, 0}, {-1, 1}, {0, 1}
        };

        for (int[] dir : directions) {
            int newX = x + dir[0];
            int newY = y + dir[1];
            if (isValidCoordinate(newX, newY)) {
                adjacentTiles.add(tiles[newX][newY]);
            }
        }

        return adjacentTiles;
    }

    /**
     * Places a unit on the map.
     * @param unit The unit to place
     * @param x The x coordinate
     * @param y The y coordinate
     * @return true if the unit was placed successfully, false otherwise
     */
    public boolean placeUnit(Unit unit, int x, int y) {
        if (!isValidCoordinate(x, y)) {
            return false;
        }

        HexTile tile = tiles[x][y];
        if (!tile.isEmpty()) {
            return false;
        }

        tile.setUnit(unit);
        unit.setCurrentTile(tile);
        return true;
    }

    /**
     * Removes a unit from the map.
     * @param unit The unit to remove
     */
    public void removeUnit(Unit unit) {
        HexTile tile = unit.getCurrentTile();
        if (tile != null) {
            tile.setUnit(null);
            unit.setCurrentTile(null);
        }
    }

    /**
     * Calculates the movement cost between two adjacent tiles.
     * @param from The starting tile
     * @param to The destination tile
     * @return The movement cost, or -1 if tiles are not adjacent
     */
    public int calculateMovementCost(HexTile from, HexTile to) {
        if (!areAdjacent(from, to)) {
            return -1;
        }
        return to.getTerrainType().getMovementCost();
    }

    /**
     * Checks if two tiles are adjacent.
     * @param tile1 The first tile
     * @param tile2 The second tile
     * @return true if the tiles are adjacent, false otherwise
     */
    private boolean areAdjacent(HexTile tile1, HexTile tile2) {
        int dx = Math.abs(tile1.getX() - tile2.getX());
        int dy = Math.abs(tile1.getY() - tile2.getY());
        return (dx == 1 && dy == 0) || (dx == 0 && dy == 1) || (dx == 1 && dy == 1);
    }

    /**
     * Gets all tiles within a certain range of a center tile.
     * @param center The center tile
     * @param range The range in hexes
     * @return List of tiles within range
     */
    public List<HexTile> getTilesInRange(HexTile center, int range) {
        List<HexTile> tilesInRange = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                HexTile tile = tiles[x][y];
                if (center.distanceTo(tile) <= range) {
                    tilesInRange.add(tile);
                }
            }
        }
        return tilesInRange;
    }

    /**
     * Calculates the distance between two hex tiles using cube coordinates.
     * @param tile1 First hex tile
     * @param tile2 Second hex tile
     * @return The distance between the tiles
     */
    public int getDistance(HexTile tile1, HexTile tile2) {
        if (tile1 == null || tile2 == null) {
            return -1;
        }

        // Convert to cube coordinates
        int x1 = tile1.getX() - (tile1.getY() - (tile1.getY() & 1)) / 2;
        int z1 = tile1.getY();
        int y1 = -x1 - z1;

        int x2 = tile2.getX() - (tile2.getY() - (tile2.getY() & 1)) / 2;
        int z2 = tile2.getY();
        int y2 = -x2 - z2;

        // Calculate distance using cube coordinates
        return (Math.abs(x1 - x2) + Math.abs(y1 - y2) + Math.abs(z1 - z2)) / 2;
    }
} 