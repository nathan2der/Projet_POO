package wargame.game;

import wargame.map.GameMap;
import wargame.map.HexTile;
import wargame.player.Player;
import wargame.unit.Unit;
import java.util.List;
import java.util.ArrayList;

/**
 * Main game class that manages the game state and logic.
 */
public class Game {
    private final GameMap gameMap;
    private final TurnManager turnManager;
    private static final int MIN_SPAWN_DISTANCE = 5;

    public Game(GameMap gameMap, List<Player> players) {
        this.gameMap = gameMap;
        this.turnManager = new TurnManager(players, gameMap);
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public int getTurnNumber() {
        return turnManager.getTurnNumber();
    }

    public Player getCurrentPlayer() {
        return turnManager.getCurrentPlayer();
    }

    public Unit getSelectedUnit() {
        return turnManager.getSelectedUnit();
    }

    public boolean selectUnit(Unit unit) {
        return turnManager.selectUnit(unit);
    }

    public void deselectUnit() {
        turnManager.deselectUnit();
    }

    public boolean moveSelectedUnit(int x, int y) {
        return turnManager.moveSelectedUnit(gameMap.getTile(x, y));
    }

    public boolean attackWithSelectedUnit(Unit targetUnit) {
        return turnManager.attackWithSelectedUnit(targetUnit);
    }

    public boolean endTurn() {
        return turnManager.endTurn();
    }

    public Player getWinner() {
        return turnManager.getWinner();
    }

    /**
     * Gets all valid moves for the selected unit.
     * @return List of valid destination tiles
     */
    public List<HexTile> getValidMoves() {
        return turnManager.getValidMoves();
    }

    /**
     * Gets all valid attack targets for the selected unit.
     * @return List of valid target units
     */
    public List<Unit> getValidTargets() {
        return turnManager.getValidTargets();
    }

    /**
     * Gets all players in the game.
     * @return List of players
     */
    public List<Player> getPlayers() {
        return turnManager.getPlayers();
    }

    /**
     * Places a unit on the map, ensuring minimum distance from enemy units.
     * @param unit The unit to place
     * @param x The x coordinate
     * @param y The y coordinate
     * @return true if placement was successful, false otherwise
     */
    public boolean placeUnit(Unit unit, int x, int y) {
        HexTile targetTile = gameMap.getTile(x, y);
        if (targetTile == null || targetTile.getUnit() != null) {
            return false;
        }

        // Check distance from enemy units
        for (Player player : turnManager.getPlayers()) {
            if (player != unit.getOwner()) {
                for (Unit enemyUnit : player.getUnits()) {
                    if (enemyUnit.getTile() != null) {
                        int distance = targetTile.distanceTo(enemyUnit.getTile());
                        if (distance < MIN_SPAWN_DISTANCE) {
                            return false;
                        }
                    }
                }
            }
        }

        return gameMap.placeUnit(unit, x, y);
    }

    /**
     * Gets all valid spawn positions for a player's units.
     * @param player The player to get spawn positions for
     * @return List of valid spawn positions
     */
    public List<HexTile> getValidSpawnPositions(Player player) {
        List<HexTile> validPositions = new ArrayList<>();
        
        // Determine spawn area based on player
        int startX, endX;
        if (player == turnManager.getPlayers().get(0)) {
            // First player spawns on the left
            startX = 0;
            endX = gameMap.getWidth() / 3;
        } else {
            // Second player spawns on the right
            startX = (gameMap.getWidth() * 2) / 3;
            endX = gameMap.getWidth();
        }

        // Check each position in the spawn area
        for (int x = startX; x < endX; x++) {
            for (int y = 0; y < gameMap.getHeight(); y++) {
                HexTile tile = gameMap.getTile(x, y);
                if (tile != null && tile.getUnit() == null) {
                    boolean isValid = true;
                    
                    // Check distance from enemy units
                    for (Player otherPlayer : turnManager.getPlayers()) {
                        if (otherPlayer != player) {
                            for (Unit enemyUnit : otherPlayer.getUnits()) {
                                if (enemyUnit.getTile() != null) {
                                    int distance = tile.distanceTo(enemyUnit.getTile());
                                    if (distance < MIN_SPAWN_DISTANCE) {
                                        isValid = false;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    
                    if (isValid) {
                        validPositions.add(tile);
                    }
                }
            }
        }
        
        return validPositions;
    }
} 