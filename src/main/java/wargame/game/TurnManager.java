package wargame.game;

import wargame.player.Player;
import wargame.unit.Unit;
import wargame.map.GameMap;
import wargame.map.HexTile;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.LinkedList;

/**
 * Manages game turns and player order.
 * Handles turn-based gameplay mechanics and player actions.
 */
public class TurnManager {
    private final List<Player> players;
    private final Queue<Player> turnOrder;
    private Player currentPlayer;
    private int turnNumber;
    private final GameMap gameMap;
    private Unit selectedUnit;
    private static final int MAX_TURNS = 100; // Optional turn limit
    private int currentPlayerIndex;

    /**
     * Creates a new turn manager.
     * @param players The list of players
     * @param gameMap The game map
     */
    public TurnManager(List<Player> players, GameMap gameMap) {
        this.players = players;
        this.turnOrder = new LinkedList<>(players);
        this.gameMap = gameMap;
        this.turnNumber = 1;
        this.currentPlayer = turnOrder.peek();
        this.currentPlayerIndex = 0;
        this.selectedUnit = null;
    }

    /**
     * Gets the current player.
     * @return The current player
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Gets the current turn number.
     * @return The turn number
     */
    public int getTurnNumber() {
        return turnNumber;
    }

    /**
     * Gets the currently selected unit.
     * @return The selected unit, or null if no unit is selected
     */
    public Unit getSelectedUnit() {
        return selectedUnit;
    }

    /**
     * Selects a unit for the current player.
     * @param unit The unit to select
     * @return true if the unit was selected, false otherwise
     */
    public boolean selectUnit(Unit unit) {
        if (unit != null && unit.getOwner() == currentPlayer) {
            selectedUnit = unit;
            return true;
        }
        return false;
    }

    /**
     * Deselects the currently selected unit.
     */
    public void deselectUnit() {
        selectedUnit = null;
    }

    /**
     * Moves the selected unit to a new tile.
     * @param targetTile The tile to move to
     * @return true if the move was successful, false otherwise
     */
    public boolean moveSelectedUnit(HexTile targetTile) {
        if (selectedUnit == null || selectedUnit.getOwner() != currentPlayer) {
            return false;
        }

        int movementCost = gameMap.calculateMovementCost(selectedUnit.getCurrentTile(), targetTile);
        if (movementCost == -1 || movementCost > selectedUnit.getRemainingMovement()) {
            return false;
        }

        return selectedUnit.move(targetTile, movementCost);
    }

    /**
     * Makes the selected unit attack a target unit.
     * @param targetUnit The unit to attack
     * @return true if the attack was successful, false otherwise
     */
    public boolean attackWithSelectedUnit(Unit targetUnit) {
        if (selectedUnit == null || selectedUnit.getOwner() != currentPlayer) {
            return false;
        }

        if (targetUnit == null || targetUnit.getOwner() == currentPlayer) {
            return false;
        }

        boolean attackSuccess = selectedUnit.attack(targetUnit);
        if (attackSuccess) {
            // Check if target unit was destroyed
            if (targetUnit.getCurrentHealth() <= 0) {
                gameMap.removeUnit(targetUnit);
                targetUnit.getOwner().removeUnit(targetUnit);
            }
        }
        return attackSuccess;
    }

    /**
     * Ends the current player's turn.
     * @return true if the game should continue, false if the game is over
     */
    public boolean endTurn() {
        // Reset current player's units
        currentPlayer.resetTurn();
        
        // Move to next player
        turnOrder.add(turnOrder.poll());
        currentPlayer = turnOrder.peek();
        
        // If we've gone through all players, increment turn number
        if (currentPlayer == players.get(0)) {
            turnNumber++;
        }

        // Check for game end conditions
        return !isGameOver();
    }

    /**
     * Checks if the game is over.
     * @return true if the game is over, false otherwise
     */
    private boolean isGameOver() {
        // Check for turn limit
        if (turnNumber > MAX_TURNS) {
            return true;
        }

        // Check for player elimination
        int activePlayers = 0;
        for (Player player : players) {
            if (player.hasUnits()) {
                activePlayers++;
            }
        }
        return activePlayers <= 1;
    }

    /**
     * Gets the winner of the game.
     * @return The winning player, or null if the game is not over
     */
    public Player getWinner() {
        Player winner = null;
        for (Player player : players) {
            if (!player.getUnits().isEmpty()) {
                if (winner == null) {
                    winner = player;
                } else {
                    return null; // Multiple players still have units
                }
            }
        }
        return winner;
    }

    /**
     * Gets all valid moves for the selected unit.
     * @return List of valid destination tiles
     */
    public List<HexTile> getValidMoves() {
        List<HexTile> validMoves = new ArrayList<>();
        if (selectedUnit == null || selectedUnit.getOwner() != getCurrentPlayer()) {
            return validMoves;
        }

        int range = selectedUnit.getMovementPoints();
        HexTile currentTile = selectedUnit.getTile();
        
        for (int x = 0; x < gameMap.getWidth(); x++) {
            for (int y = 0; y < gameMap.getHeight(); y++) {
                HexTile tile = gameMap.getTile(x, y);
                if (tile != null && tile != currentTile && tile.getUnit() == null) {
                    int distance = gameMap.getDistance(currentTile, tile);
                    if (distance <= range) {
                        validMoves.add(tile);
                    }
                }
            }
        }
        
        return validMoves;
    }

    /**
     * Gets all valid attack targets for the selected unit.
     * @return List of valid target units
     */
    public List<Unit> getValidTargets() {
        List<Unit> validTargets = new ArrayList<>();
        if (selectedUnit == null || selectedUnit.getOwner() != getCurrentPlayer()) {
            return validTargets;
        }

        int range = selectedUnit.getAttackRange();
        HexTile currentTile = selectedUnit.getTile();
        
        for (int x = 0; x < gameMap.getWidth(); x++) {
            for (int y = 0; y < gameMap.getHeight(); y++) {
                HexTile tile = gameMap.getTile(x, y);
                if (tile != null && tile != currentTile) {
                    Unit targetUnit = tile.getUnit();
                    if (targetUnit != null && targetUnit.getOwner() != getCurrentPlayer()) {
                        int distance = gameMap.getDistance(currentTile, tile);
                        if (distance <= range) {
                            validTargets.add(targetUnit);
                        }
                    }
                }
            }
        }
        
        return validTargets;
    }

    public List<Player> getPlayers() {
        return players;
    }
} 